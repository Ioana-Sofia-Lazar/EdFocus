package com.ioanap.classbook.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.utils.ScheduleFragmentPagerAdapter;

public class ScheduleActivity extends BaseActivity {

    // variables
    private String mClassId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(ScheduleActivity.this, false);
        setContentView(R.layout.activity_schedule);

        // get class id
        Intent myIntent = getIntent();
        mClassId = myIntent.getStringExtra("classId");

        // get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new ScheduleFragmentPagerAdapter(getSupportFragmentManager(),
                ScheduleActivity.this, mClassId));

        // give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

}
