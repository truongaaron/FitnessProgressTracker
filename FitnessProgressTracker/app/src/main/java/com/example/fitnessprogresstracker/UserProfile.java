package com.example.fitnessprogresstracker;

public class UserProfile {
    public String userAge;
    public String userEmail;
    public String userName;
    public String userCalories;

    public UserProfile() {

    }

    public UserProfile(String userAge, String userEmail, String userName, String userCalories) {
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userCalories = userCalories;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCalories() { return userCalories; }

    public void setUserCalories() { this.userCalories = userCalories; }
}
