package com.ioanap.classbook.shared;

import android.annotation.TargetApi;
import android.content.Context;
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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.parent.ChildrenFragment;
import com.ioanap.classbook.utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

import static android.support.v4.view.MenuItemCompat.getActionView;

public class DrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        UserProfileFragment.OnFragmentInteractionListener {

    private static final String TAG = "DrawerActivity";

    // variables
    private Context mContext = DrawerActivity.this;

    // widgets
    private TextView mNotificationsCounterText, mRequestsCounterText;

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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        if (getCurrentUserType().equals("teacher") || getCurrentUserType().equals("student")) {
            // show "My classes" for teacher and student
            navigationView.getMenu().findItem(R.id.nav_classes).setVisible(true);
        }
        if (getCurrentUserType().equals("parent")) {
            // show "My children" for parent
            navigationView.getMenu().findItem(R.id.nav_children).setVisible(true);
        }
        if (getCurrentUserType().equals("parent") || getCurrentUserType().equals("student")) {
            // show "Notifications" for parent and student
            navigationView.getMenu().findItem(R.id.nav_notifications).setVisible(true);
        }

        // number badges
        mNotificationsCounterText = (TextView) getActionView(navigationView.getMenu().
                findItem(R.id.nav_notifications));
        mRequestsCounterText = (TextView) getActionView(navigationView.getMenu().
                findItem(R.id.nav_contacts));
        initializeDrawerCounters();
    }

    private void initializeDrawerCounters() {
        // style
        styleBadgeCounter(mNotificationsCounterText);
        styleBadgeCounter(mRequestsCounterText);

        // get number of new notifications (not seen)
        mNotificationsRef.child(CURRENT_USER_ID).orderByChild("seen").equalTo(false)
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
        mRequestsRef.child(CURRENT_USER_ID)
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
