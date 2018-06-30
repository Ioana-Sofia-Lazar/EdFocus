package com.ioanapascu.edfocus.shared;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanapascu.edfocus.BaseActivity;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.SignInActivity;
import com.ioanapascu.edfocus.model.Message;
import com.ioanapascu.edfocus.model.UserAccountSettings;
import com.ioanapascu.edfocus.others.UniversalImageLoader;
import com.ioanapascu.edfocus.parent.ChildrenFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.v4.view.MenuItemCompat.getActionView;

public class DrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        UserProfileFragment.OnFragmentInteractionListener {

    private static final String TAG = "DrawerActivity";
    // widgets
    NavigationView mNavigationView;
    // variables
    private Context mContext = DrawerActivity.this;
    private int nbOfMessages = 0;
    private String mCurrentUserType, mCurrentUserId;
    private TextView mNotificationsCounterText, mRequestsCounterText, mMessagesCounterText;

    public void displayFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(DrawerActivity.this, true);
        setContentView(R.layout.activity_drawer);

        // set initially shown fragment
        displayFragment(new UserProfileFragment());

        // initialize image loader
        initImageLoader();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.bringToFront();

        loadUserInfoInHeader();

        // show and hide menu options according to user type
        if (mCurrentUserType.equals("teacher") || mCurrentUserType.equals("student")) {
            // show "My classes" for teacher and student
            mNavigationView.getMenu().findItem(R.id.nav_classes).setVisible(true);
        }
        if (mCurrentUserType.equals("parent")) {
            // show "My children" for parent
            mNavigationView.getMenu().findItem(R.id.nav_children).setVisible(true);
        }
        if (mCurrentUserType.equals("parent") || mCurrentUserType.equals("student")) {
            // show "Notifications" for parent and student
            mNavigationView.getMenu().findItem(R.id.nav_notifications).setVisible(true);
        }

        // mark user as being online
        firebase.mOnlineUsersRef.child(mCurrentUserId).setValue(true);

        // number badges
        mNotificationsCounterText = (TextView) getActionView(mNavigationView.getMenu().
                findItem(R.id.nav_notifications));
        mRequestsCounterText = (TextView) getActionView(mNavigationView.getMenu().
                findItem(R.id.nav_contacts));
        mMessagesCounterText = (TextView) getActionView(mNavigationView.getMenu().
                findItem(R.id.nav_messages));
        initializeDrawerCounters();
    }

    private void loadUserInfoInHeader() {
        // get drawer header widgets
        LinearLayout drawerHeader = (LinearLayout) mNavigationView.getHeaderView(0);
        final TextView userName = drawerHeader.findViewById(R.id.text_user_name);
        final TextView userEmail = drawerHeader.findViewById(R.id.text_user_email);
        final CircleImageView userPhoto = drawerHeader.findViewById(R.id.image_user_photo);

        // show user info
        firebase.mUserAccountSettingsRef.child(firebase.getCurrentUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserAccountSettings settings = dataSnapshot.getValue(UserAccountSettings.class);

                        UniversalImageLoader.setImage(settings.getProfilePhoto(), userPhoto, null);
                        userName.setText(settings.getFirstName() + " " + settings.getLastName());
                        userEmail.setText(settings.getEmail());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void initializeDrawerCounters() {
        // style
        styleBadgeCounter(mNotificationsCounterText);
        styleBadgeCounter(mRequestsCounterText);
        styleBadgeCounter(mMessagesCounterText);

        // get number of new notifications (not seen)
        firebase.mNotificationsRef.child(mCurrentUserId).orderByChild("seen").equalTo(false)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int nb = (int) dataSnapshot.getChildrenCount();
                        if (nb == 0) {
                            mNotificationsCounterText.setVisibility(View.GONE);
                        } else {
                            mNotificationsCounterText.setVisibility(View.VISIBLE);
                            mNotificationsCounterText.setText(String.valueOf(nb));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // get number of new notifications (not seen)
        firebase.mRequestsRef.child(mCurrentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int nb = (int) dataSnapshot.getChildrenCount();
                        if (nb == 0) {
                            mRequestsCounterText.setVisibility(View.GONE);
                        } else {
                            mRequestsCounterText.setVisibility(View.VISIBLE);
                            mRequestsCounterText.setText(String.valueOf(nb));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // get number of new notifications (not seen)
        firebase.mMessagesRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nbOfMessages = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) { // each user that current user has a conversation with
                    for (DataSnapshot snapshot : userSnapshot.getChildren()) {
                        if (snapshot.exists()) {
                            Message message = snapshot.getValue(Message.class);
                            if (!message.getFrom().equals(mCurrentUserId) && !message.isSeen()) {
                                nbOfMessages++;
                                mMessagesCounterText.setVisibility(View.VISIBLE);
                                mMessagesCounterText.setText(String.valueOf(nbOfMessages));
                            }
                        }
                    }
                }
                if (nbOfMessages == 0) {
                    mMessagesCounterText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void styleBadgeCounter(TextView textView) {
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // display fragments corresponding to clicked items from the drawer
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            checkInternetConnection();
            displayFragment(new UserProfileFragment());
        } else if (id == R.id.nav_contacts) {
            checkInternetConnection();
            displayFragment(new ContactsFragment());
        } else if (id == R.id.nav_messages) {
            checkInternetConnection();
            displayFragment(new MessagesFragment());
        } else if (id == R.id.nav_classes) {
            checkInternetConnection();
            displayFragment(new ClassesFragment());
        } else if (id == R.id.nav_settings) {
            checkInternetConnection();
            displayFragment(new SettingsFragment());
        } else if (id == R.id.nav_children) {
            checkInternetConnection();
            displayFragment(new ChildrenFragment());
        } else if (id == R.id.nav_help) {
            checkInternetConnection();
            displayFragment(new HelpFragment());
        } else if (id == R.id.nav_notifications) {
            checkInternetConnection();
            displayFragment(new NotificationsFragment());
        } else if (id == R.id.nav_logout) {
            // sign user out
            signOut();
            // jump to Sign In activity
            Intent intent = new Intent(this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);

            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
