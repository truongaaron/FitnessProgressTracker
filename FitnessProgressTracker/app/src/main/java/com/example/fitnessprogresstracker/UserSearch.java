package com.example.fitnessprogresstracker;

public class UserSearch {

    public String name, id;

    public UserSearch() {

    }

    public UserSearch(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return id;
    }

    public void setImage(String id) {
        this.id = id;
    }
}
