package com.ioanap.classbook.model;

/**
 * Created by ioana on 10/25/2017.
 */

public class User {

    private String firstName = "";
    private String lastName = "";
    private String userType = "";

    public User(String firstName, String lastName, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
    }

    public User() {
    }

    public User(String userType) {
        this.firstName = "";
        this.lastName = "";
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserType() {
        return userType;
    }
}
