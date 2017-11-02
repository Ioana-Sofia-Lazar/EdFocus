package com.ioanap.classbook.model;

import java.util.ArrayList;

public class User {

    private String id;
    private String email;
    private String userType;
    private ArrayList<String> classes;

    public User() {
        this("", "", new ArrayList<String>());
    }

    public User(String email, String userType) {
        this(email, userType, new ArrayList<String>());
    }

    public User(String email, String userType, ArrayList<String> classes) {
        this("", email, userType, classes);
    }

    public User(String id, String email, String userType, ArrayList<String> classes) {
        this.id = id;
        this.email = email;
        this.userType = userType;
        this.classes = classes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public ArrayList<String> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }
}
