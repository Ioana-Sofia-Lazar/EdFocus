package com.ioanapascu.edfocus.model;

/**
 * Created by Ioana Pascu on 3/26/2018.
 */

public class Event {
    private String id;
    private String date;
    private String time;
    private String location;
    private String name;
    private String description;
    // a long combining the date and the time of the event
    private long compareValue;

    public Event() {
    }

    public Event(String id, String date, String time, String location, String name, String description,
                 long compareValue) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.location = location;
        this.name = name;
        this.description = description;
        this.compareValue = compareValue;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getCompareValue() {
        return compareValue;
    }

    public void setCompareValue(long compareValue) {
        this.compareValue = compareValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
}
