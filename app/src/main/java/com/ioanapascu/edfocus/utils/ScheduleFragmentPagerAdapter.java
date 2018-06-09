package com.ioanapascu.edfocus.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ioanapascu.edfocus.teacher.SchedulePageFragment;

/**
 * Created by ioana on 2/28/2018.
 */

public class ScheduleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 7;
    private String[] tabTitles = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private Context context;
    private String mClassId;

    public ScheduleFragmentPagerAdapter(FragmentManager fm, Context context, String claddId) {
        super(fm);
        this.context = context;
        mClassId = claddId;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return SchedulePageFragment.newInstance(position, mClassId);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
