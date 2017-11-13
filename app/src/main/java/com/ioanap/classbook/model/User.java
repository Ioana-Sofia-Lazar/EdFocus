package com.ioanap.classbook.model;

public class User {

    private String id;
    private String email;
    private String userType;

    public User() {
        this("", "", "");
    }

    public User(String email, String userType) {
        this("", email, userType);
    }

    public User(String id, String email, String userType) {
        this.id = id;
        this.email = email;
        this.userType = userType;
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

}
