package com.ioanap.classbook.model;

/**
 * Created by Ioana Pascu on 4/1/2018.
 * Used when adding multiple grades at once.
 */

public class GradeRow {
    private String studentId;
    private String studentName;
    private String grade;
    private String notes;

    public GradeRow(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
