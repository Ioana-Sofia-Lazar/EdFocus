package com.ioanapascu.edfocus.model;

/**
 * Created by Ioana Pascu on 3/26/2018.
 */

public class Event {
    private String id;
    private Long date;
    private String location;
    private String name;
    private String description;

    public Event() {
    }

    public Event(String id, Long date, String location, String name, String description) {
        this.id = id;
        this.date = date;
        this.location = location;
        this.name = name;
        this.description = description;
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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
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
