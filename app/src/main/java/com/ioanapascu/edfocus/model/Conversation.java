package com.ioanapascu.edfocus.model;

/**
 * Created by Ioana Pascu on 5/10/2018.
 */

public class Conversation {
    private String userId;
    private String userName;
    private String userPhoto;
    private String lastMessage;
    private String from;
    private long lastMessageDate;
    private boolean seen;

    public Conversation(String userId, String userName, String userPhoto, String lastMessage, String from, long lastMessageDate, boolean seen) {
        this.userId = userId;
        this.userName = userName;
        this.userPhoto = userPhoto;
        this.lastMessage = lastMessage;
        this.from = from;
        this.lastMessageDate = lastMessageDate;
        this.seen = seen;
    }

    public Conversation() {

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(long lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
