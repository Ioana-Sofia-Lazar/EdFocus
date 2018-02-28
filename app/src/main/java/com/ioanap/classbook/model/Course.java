package com.ioanap.classbook.model;

/**
 * Created by ioana on 2/27/2018.
 */

public class Course {

    String id;
    String name;
    String teacher;
    String description;

    public Course() {
    }

    public Course(String id, String name, String teacher, String description) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.description = description;
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

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
