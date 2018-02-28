package com.ioanap.classbook.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.ioanap.classbook.BaseActivity;
import com.ioanap.classbook.R;
import com.ioanap.classbook.utils.ScheduleFragmentPagerAdapter;

public class ScheduleActivity extends BaseActivity implements View.OnClickListener {

    // widgets
    ImageView mBackButton;

    // variables
    private String mClassId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // get class id
        Intent myIntent = getIntent();
        mClassId = myIntent.getStringExtra("classId");

        // widgets
        mBackButton = (ImageView) findViewById(R.id.img_back);

        mBackButton.setOnClickListener(this);

        // get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ScheduleFragmentPagerAdapter(getSupportFragmentManager(),
                ScheduleActivity.this, mClassId));

        // give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View view) {
        if (view == mBackButton) {
            finish();
        }
    }
}
