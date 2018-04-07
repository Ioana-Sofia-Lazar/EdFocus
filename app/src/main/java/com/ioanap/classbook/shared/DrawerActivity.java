package com.ioanap.classbook.shared;

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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.teacher.ContactsFragment;
import com.ioanap.classbook.teacher.MessagesFragment;
import com.ioanap.classbook.utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        UserProfileFragment.OnFragmentInteractionListener {

    private static final String TAG = "DrawerActivity";

    private Context mContext = DrawerActivity.this;

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
            displayFragment(new UserProfileFragment());
        } else if (id == R.id.nav_contacts) {
            displayFragment(new ContactsFragment());
        } else if (id == R.id.nav_messages) {
            displayFragment(new MessagesFragment());
        } else if (id == R.id.nav_classes) {
            displayFragment(new ClassesFragment());
        } else if (id == R.id.nav_settings) {

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
