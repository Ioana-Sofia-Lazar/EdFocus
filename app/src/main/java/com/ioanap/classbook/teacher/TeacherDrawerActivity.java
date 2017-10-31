package com.ioanap.classbook.teacher;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.ioanap.classbook.R;
import com.ioanap.classbook.utils.FirebaseUtils;
import com.ioanap.classbook.utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TeacherDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   TeacherProfileFragment.OnFragmentInteractionListener,
                   ContactsFragment.OnFragmentInteractionListener {

    private Context mContext = TeacherDrawerActivity.this;
    private FirebaseUtils mFirebaseUtils;

    /**
     * Displays the fragment in the container.
     *
     * @param fragment fragment to display
     */
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
        setContentView(R.layout.activity_drawer);

        mFirebaseUtils = new FirebaseUtils(mContext);

        // set initially shown fragment
        displayFragment(new TeacherProfileFragment());

        // initialize image loader
        initImageLoader();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // display fragments corresponding to clicked items from the drawer
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            displayFragment(new TeacherProfileFragment());
        } else if (id == R.id.nav_contacts) {
            displayFragment(new ContactsFragment());Log.i("showing fragment","contacts");
        } else if (id == R.id.nav_messages) {

        } else if (id == R.id.nav_classes) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            // sign user out
            mFirebaseUtils.signOut();

            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
