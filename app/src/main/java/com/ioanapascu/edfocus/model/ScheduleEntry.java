package com.ioanapascu.edfocus.model;

/**
 * Created by ioana on 2/28/2018.
 * Object like the one stored in firebase. Helps at savin and retrieving data from firebase.
 */

public class ScheduleEntry {

    private String id;
    private Long startsAt;
    private Long endsAt;
    private String courseId;

    public ScheduleEntry(String id, Long startsAt, Long endsAt, String courseId) {
        this.id = id;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.courseId = courseId;
    }

    public ScheduleEntry() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Long startsAt) {
        this.startsAt = startsAt;
    }

    public Long getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Long endsAt) {
        this.endsAt = endsAt;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

}
