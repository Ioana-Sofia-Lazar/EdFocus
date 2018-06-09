package com.ioanapascu.edfocus.model;

/**
 * Created by ioana on 11/13/2017.
 */

public class Person {

    private String id;
    private String name;
    private String userType;
    private String profilePhoto;

    public Person(String id, String name, String userType, String profilePhoto) {
        this.id = id;
        this.name = name;
        this.userType = userType;
        this.profilePhoto = profilePhoto;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
