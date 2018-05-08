package com.ioanap.classbook.model;

/**
 * Created by Ioana Pascu on 5/6/2018.
 */

public class Notification {
    private String title;
    private String message;
    private int icon;
    private String clickAction;
    private long compareValue;
    private boolean seen;

    public Notification(String title, String message, int icon, String clickAction, long compareValue, boolean seen) {
        this.title = title;
        this.message = message;
        this.icon = icon;
        this.clickAction = clickAction;
        this.compareValue = compareValue;
        this.seen = seen;
    }

    public Notification() {
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getClickAction() {
        return clickAction;
    }

    public void setClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public long getCompareValue() {
        return compareValue;
    }

    public void setCompareValue(long compareValue) {
        this.compareValue = compareValue;
    }
}
