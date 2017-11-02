package com.ioanap.classbook.model;

public class UserAccountSettings {

    private String id;
    private String email;
    private String userType;
    private String name;
    private String description;
    private String location;
    private String profilePhoto;
    private String phoneNumber;
    private int noOfContacts;
    private int noOfClasses;

    public UserAccountSettings() {
        this("", "", "", "", "", "", "", "", 0, 0);
    }

    public UserAccountSettings(String id, String email, String userType, String name, String description, String location, String profilePhoto, String phoneNumber, int noOfContacts, int noOfClasses) {
        this.id = id;
        this.email = email;
        this.userType = userType;
        this.name = name;
        this.description = description;
        this.location = location;
        this.profilePhoto = profilePhoto;
        this.phoneNumber = phoneNumber;
        this.noOfContacts = noOfContacts;
        this.noOfClasses = noOfClasses;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getNoOfContacts() {
        return noOfContacts;
    }

    public void setNoOfContacts(int noOfContacts) {
        this.noOfContacts = noOfContacts;
    }

    public int getNoOfClasses() {
        return noOfClasses;
    }

    public void setNoOfClasses(int noOfClasses) {
        this.noOfClasses = noOfClasses;
    }
}
