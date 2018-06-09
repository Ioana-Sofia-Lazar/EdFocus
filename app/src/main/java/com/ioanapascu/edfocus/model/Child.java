package com.ioanapascu.edfocus.model;

import java.util.List;

/**
 * Created by Ioana Pascu on 4/27/2018.
 */

public class Child {

    String id;
    String name;
    String photo;
    List<String> classIds;

    public Child(String id, String name, String photo, List<String> classIds) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.classIds = classIds;
    }

    public Child() {
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<String> getClassIds() {
        return classIds;
    }

    public void setClassIds(List<String> classIds) {
        this.classIds = classIds;
    }
}
