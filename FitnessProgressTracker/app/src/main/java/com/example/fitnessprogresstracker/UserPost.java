package com.example.fitnessprogresstracker;

public class UserPost {

    public String name;
    public String post;

    public UserPost() {

    }

    public UserPost(String name, String post) {
        this.name = name;
        this.post = post;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

}
