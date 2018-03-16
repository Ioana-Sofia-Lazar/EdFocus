package com.ioanap.classbook.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ioanap.classbook.R;

public class StudentActivity extends AppCompatActivity {

    private String mStudentId = "abc";
    private String mClassId = "abc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        Intent intent = getIntent();
        mStudentId = intent.getStringExtra("studentId");
        mClassId = intent.getStringExtra("classId");

        // get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new StudentFragmentPagerAdapter(getSupportFragmentManager(),
                StudentActivity.this, mStudentId, mClassId));

        // give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

}
