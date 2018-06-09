package com.ioanapascu.edfocus.model;

public class UserAccountSettings {

    private String id;
    private String email;
    private String userType;
    private String firstName;
    private String lastName;
    private String displayName;
    private String description;
    private String location;
    private String profilePhoto;
    private String phoneNumber;

    public UserAccountSettings() {
        this("", "", "", "", "", "", "", "", "", "");
    }

    public UserAccountSettings(String id, String email, String userType, String firstName, String lastName, String displayName, String description, String location, String profilePhoto, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.description = description;
        this.location = location;
        this.profilePhoto = profilePhoto;
        this.phoneNumber = phoneNumber;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

}
