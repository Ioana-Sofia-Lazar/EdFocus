package com.ioanap.classbook.model;

/**
 * Created by ioana on 2/23/2018.
 */

public class Class {
    private String id;
    private String name;
    private String school;
    private String description;
    private String photo;
    private String token;
    private String teacherId;

    public Class() {
    }

    public Class(String id, String name, String school, String description, String photo, String token, String teacherId) {
        this.id = id;
        this.name = name;
        this.school = school;
        this.description = description;
        this.photo = photo;
        this.token = token;
        this.teacherId = teacherId;

    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
