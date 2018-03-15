package com.ioanap.classbook.model;

/**
 * Created by ioana on 3/15/2018.
 */

public class Grade {
    private String id;
    private String name;
    private String grade;
    private String courseId;
    private String studentId;
    private String date;
    private String description;
    private String courseName;

    public Grade() {
    }

    public Grade(String id, String name, String grade, String courseId, String studentId,
                 String date, String description, String courseName) {

        this.id = id;
        this.name = name;
        this.grade = grade;
        this.courseId = courseId;
        this.studentId = studentId;
        this.date = date;
        this.description = description;
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
