package com.ioanap.classbook.model;

import java.util.Map;

/**
 * Created by Ioana Pascu on 5/6/2018.
 */

public class Notification {
    private String title;
    private String message;
    private String icon;
    private String clickAction;
    private long compareValue;
    private boolean seen;
    private Map<String, Object> extras;

    public Notification(String title, String message, String icon, String clickAction, long compareValue, boolean seen, Map<String, Object> extras) {
        this.title = title;
        this.message = message;
        this.icon = icon;
        this.clickAction = clickAction;
        this.compareValue = compareValue;
        this.seen = seen;
        this.extras = extras;
    }

    public Notification() {
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
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
