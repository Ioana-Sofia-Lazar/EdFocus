package com.ioanap.classbook.model;

import java.util.Comparator;

/**
 * Created by ioana on 2/28/2018.
 */

public class ScheduleEntryAndCourseComparator implements Comparator<ScheduleEntryAndCourse> {
    public int compare(ScheduleEntryAndCourse a, ScheduleEntryAndCourse b) {
        // a.getEntry().getStartsAt() is in format hh:mm
        // compare them as floats: hh.mm
        String[] parts = a.getEntry().getStartsAt().split(":");
        float time1 = Float.parseFloat(parts[0] + "." + parts[1]);

        parts = b.getEntry().getStartsAt().split(":");
        float time2 = Float.parseFloat(parts[0] + "." + parts[1]);

        if (time1 == time2) return 0;
        if (time1 < time2) return -1;
        return 1;
    }
}
