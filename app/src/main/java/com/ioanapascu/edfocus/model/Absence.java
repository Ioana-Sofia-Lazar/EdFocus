package com.ioanapascu.edfocus.model;

/**
 * Created by Ioana Pascu on 3/22/2018.
 */

public class Absence {
    private AbsenceDb absenceDb;
    private String courseName;

    public Absence() {
    }

    public Absence(AbsenceDb absenceDb, String courseName) {
        this.absenceDb = absenceDb;
        this.courseName = courseName;
    }

    @Override
    public String toString() {
        return "Absence{" +
                "absenceDb=" + absenceDb +
                ", courseName='" + courseName + '\'' +
                '}';
    }

    public String getId() {
        return absenceDb.getId();
    }

    public void setId(String id) {
        absenceDb.setId(id);
    }

    public String getDate() {
        return absenceDb.getDate();
    }

    public void setDate(String date) {
        absenceDb.setDate(date);
    }

    public String getClassId() {
        return absenceDb.getClassId();
    }

    public void setClassId(String classId) {
        absenceDb.setClassId(classId);
    }

    public String getCourseId() {
        return absenceDb.getCourseId();
    }

    public void setCourseId(String courseId) {
        absenceDb.setCourseId(courseId);
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public boolean isAuthorised() {
        return absenceDb.isAuthorised();
    }

    public void setAuthorised(boolean authorised) {
        absenceDb.setAuthorised(authorised);
    }

}
