package com.example.fitnessprogresstracker;

public class UserPost {

    public String timeStamp;
    public String post;

    public UserPost() {

    }

    public UserPost(String timeStamp, String post) {
        this.timeStamp = timeStamp;
        this.post = post;
    }


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

}
