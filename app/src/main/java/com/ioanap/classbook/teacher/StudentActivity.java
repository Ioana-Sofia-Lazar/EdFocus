package com.ioanap.classbook.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.model.UserAccountSettings;
import com.ioanap.classbook.utils.StudentFragmentPagerAdapter;
import com.ioanap.classbook.utils.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentActivity extends BaseActivity {

    // variables
    private String mStudentId;
    private String mClassId;

    // widgets
    private CircleImageView mProfilePhoto;
    private TextView mStudentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(StudentActivity.this, false);
        setContentView(R.layout.activity_student);

        // intent information
        Intent intent = getIntent();
        mStudentId = intent.getStringExtra("studentId");
        mClassId = intent.getStringExtra("classId");

        // widgets
        mProfilePhoto = findViewById(R.id.profile_photo);
        mStudentName = findViewById(R.id.text_student_name);

        displayStudentInfo();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new StudentFragmentPagerAdapter(getSupportFragmentManager(),
                StudentActivity.this, mStudentId, mClassId));

        // give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void displayStudentInfo() {
        // get info for student that is being viewed
        mSettingsRef.child(mStudentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // retrieve user info
                UserAccountSettings settings = dataSnapshot.getValue(UserAccountSettings.class);

                // setup widgets to display user info from the database
                mStudentName.setText(settings.getDisplayName());
                UniversalImageLoader.setImage(settings.getProfilePhoto(), mProfilePhoto, null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_selected_students, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_add_grade:
                //todo
                return true;
            case R.id.option_mark_absent:
                //todo
                return true;
            case R.id.option_view_profile:
                //todo
                return true;
            case R.id.option_remove:
                //todo
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
