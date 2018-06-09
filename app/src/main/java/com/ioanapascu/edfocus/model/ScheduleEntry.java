package com.ioanapascu.edfocus.model;

/**
 * Created by ioana on 2/28/2018.
 * Object like the one stored in firebase. Helps at savin and retrieving data from firebase.
 */

public class ScheduleEntry {

    String id;
    String startsAt;
    String endsAt;
    String courseId;
    float compareValue;

    public ScheduleEntry(String id, String startsAt, String endsAt, String courseId, float compareValue) {
        this.id = id;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.courseId = courseId;
        this.compareValue = compareValue;
    }

    public ScheduleEntry() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(String startsAt) {
        this.startsAt = startsAt;
    }

    public String getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(String endsAt) {
        this.endsAt = endsAt;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public float getCompareValue() {
        return compareValue;
    }

    public void setCompareValue(float compareValue) {
        this.compareValue = compareValue;
    }
}
