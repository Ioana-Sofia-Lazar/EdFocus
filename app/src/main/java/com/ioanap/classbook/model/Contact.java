package com.ioanap.classbook.model;

/**
 * Data model for Contact.
 */

public class Contact {

    private String name;
    private String email;
    private String profilePhoto;

    public Contact(String name, String email, String profilePhoto) {
        this.name = name;
        this.email = email;
        this.profilePhoto = profilePhoto;
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
}
