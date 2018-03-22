package com.ioanap.classbook.model;

/**
 * Created by Ioana Pascu on 3/22/2018.
 */

public class AbsenceDb {
    private String id;
    private String date;
    private boolean authorised;
    private String classId;
    private String courseId;
    private String studentId;

    public AbsenceDb() {
    }

    public AbsenceDb(String id, String date, boolean authorised, String classId, String courseId, String studentId) {
        this.id = id;
        this.date = date;
        this.authorised = authorised;
        this.classId = classId;
        this.courseId = courseId;
        this.studentId = studentId;
    }

    @Override
    public String toString() {
        return "AbsenceDb{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", authorised=" + authorised +
                ", classId='" + classId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", studentId='" + studentId + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public boolean isAuthorised() {
        return authorised;
    }

    public void setAuthorised(boolean authorised) {
        this.authorised = authorised;
    }
}
