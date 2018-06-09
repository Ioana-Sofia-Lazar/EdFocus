package com.ioanapascu.edfocus.model;

public class RequestInfo {

    private String requestId;
    private String personId; // ID of person who sent the request
    private String name;
    private String profilePhoto;
    private String requestType; // can request someone to add you as a simple Contact, as their Teacher etc.
    private String userType;

    public RequestInfo(String requestId, String personId, String name, String profilePhoto, String requestType, String userType) {
        this.requestId = requestId;
        this.personId = personId;
        this.name = name;
        this.profilePhoto = profilePhoto;
        this.requestType = requestType;
        this.userType = userType;
    }

    public RequestInfo() {
        this("", "", "", "", "", "");
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "RequestInfo{" +
                "requestId='" + requestId + '\'' +
                ", personId='" + personId + '\'' +
                ", name='" + name + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", requestType='" + requestType + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
