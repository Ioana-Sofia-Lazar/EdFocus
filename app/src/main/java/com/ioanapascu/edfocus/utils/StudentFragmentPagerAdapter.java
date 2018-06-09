package com.ioanapascu.edfocus.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ioanapascu.edfocus.teacher.StudentActivityFragment;

/**
 * Created by ioana on 2/28/2018.
 */

public class StudentFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 2;
    private String[] tabTitles = new String[]{"Grades", "Absences"};
    private Context context;
    private String mStudentId, mClassId;

    public StudentFragmentPagerAdapter(FragmentManager fm, Context context, String studentId, String classId) {
        super(fm);
        this.context = context;
        mStudentId = studentId;
        mClassId = classId;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return StudentActivityFragment.newInstance(position, mStudentId, mClassId);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
