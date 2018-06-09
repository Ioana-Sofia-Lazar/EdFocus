package com.ioanapascu.edfocus.model;

/**
 * Created by ioana on 2/28/2018.
 */

public class ScheduleEntryAndCourse {

    ScheduleEntry entry;
    Course course;

    public ScheduleEntryAndCourse(ScheduleEntry entry, Course course) {
        this.entry = entry;
        this.course = course;
    }

    public ScheduleEntryAndCourse() {
    }

    public ScheduleEntry getEntry() {
        return entry;
    }

    public void setEntry(ScheduleEntry entry) {
        this.entry = entry;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
