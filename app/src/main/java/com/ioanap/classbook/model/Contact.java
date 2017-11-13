package com.ioanap.classbook.model;

/**
 * Data model for Contact.
 */

public class Contact {

    private String id;
    private String name;
    private String email;
    private String profilePhoto;
    private String userType;

    public Contact(String id, String name, String email, String profilePhoto, String userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePhoto = profilePhoto;
        this.userType = userType;
    }

    public Contact() {
        this("", "", "", "", "");
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
