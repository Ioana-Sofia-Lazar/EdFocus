package com.ioanapascu.edfocus.model;

/**
 * Created by ioana on 3/15/2018.
 * Used to retrieve a grade from database.
 */

public class GradeDb {
    private String id;
    private String name;
    private String grade;
    private Long date;
    private String description;
    private String classId;
    private String courseId;
    private String studentId;

    public GradeDb() {
    }

    public GradeDb(String id, String name, String grade, Long date, String description, String classId, String courseId, String studentId) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.date = date;
        this.description = description;
        this.classId = classId;
        this.courseId = courseId;
        this.studentId = studentId;
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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
