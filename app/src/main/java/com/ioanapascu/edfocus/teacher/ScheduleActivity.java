package com.ioanapascu.edfocus.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.ioanapascu.edfocus.BaseActivity;
import com.ioanapascu.edfocus.R;
import com.ioanapascu.edfocus.others.ScheduleFragmentPagerAdapter;

import java.util.Calendar;

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
        // default tab is with the schedule of current day
        tabLayout.getTabAt(getDayOfWeek()).select();
    }

    private int getDayOfWeek() {
        // get what day of the week today is
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            case Calendar.SUNDAY:
                return 6;
            default:
                return 0;
        }
    }

}
