package com.ioanapascu.edfocus.model;

/**
 * Created by ioana on 3/15/2018.
 * Used to retrieve a grade from database.
 */

public class Grade {
    private GradeDb gradeDb;
    private String courseName;

    public Grade() {
    }

    public Grade(GradeDb gradeDb, String courseName) {
        this.gradeDb = gradeDb;
        this.courseName = courseName;
    }

    public GradeDb getGradeDb() {
        return gradeDb;
    }

    public void setGradeDb(GradeDb gradeDb) {
        this.gradeDb = gradeDb;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /* Getters and setters for gradeDb field */
    public String getId() {
        return gradeDb.getId();
    }

    public void setId(String id) {
        this.gradeDb.setId(id);
    }

    public String getName() {
        return gradeDb.getName();
    }

    public void setName(String name) {
        this.gradeDb.setName(name);
    }

    public String getGrade() {
        return gradeDb.getGrade();
    }

    public void setGrade(String grade) {
        this.gradeDb.setGrade(grade);
    }

    public String getDate() {
        return gradeDb.getDate();
    }

    public void setDate(String date) {
        this.gradeDb.setDate(date);
    }

    public String getDescription() {
        return gradeDb.getDescription();
    }

    public void setDescription(String description) {
        this.gradeDb.setDescription(description);
    }

    public String getClassId() {
        return gradeDb.getClassId();
    }

    public void setClassId(String classId) {
        this.gradeDb.setClassId(classId);
    }

    public String getCourseId() {
        return gradeDb.getCourseId();
    }

    public void setCourseId(String courseId) {
        this.gradeDb.setCourseId(courseId);
    }

    public String getStudentId() {
        return gradeDb.getStudentId();
    }

    public void setStudentId(String studentId) {
        this.gradeDb.setStudentId(studentId);
    }
}
