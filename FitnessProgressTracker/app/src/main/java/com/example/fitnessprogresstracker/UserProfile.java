package com.example.fitnessprogresstracker;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    public String userAge;
    public String userEmail;
    public String userName;
    public String userCalories;
    public List<String> userFoodList = new ArrayList<String>(), userCalorieList = new ArrayList<String>();
    public List<ImageView> userDeleteBtns = new ArrayList<ImageView>();


    public UserProfile() {

    }

    public UserProfile(String userAge, String userEmail, String userName, String userCalories, List<String> userFoodList, List<String> userCalorieList, List<ImageView> userDeleteBtns) {
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userCalories = userCalories;
        this.userFoodList = userFoodList;
        this.userCalorieList = userCalorieList;
        this.userDeleteBtns = userDeleteBtns;
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

    public void setUserCalories(String userCalories) { this.userCalories = userCalories; }

    public List<String> getUserFoodList() {
        return userFoodList;
    }

    public void setUserFoodList(List<String> userFoodList) {
        this.userFoodList = userFoodList;
    }

    public List<String> getUserCalorieList() {
        return userCalorieList;
    }

    public void setUserCalorieList(List<String> userCalorieList) {
        this.userCalorieList = userCalorieList;
    }

    public List<ImageView> getUserDeleteBtns() {
        return userDeleteBtns;
    }

    public void setUserDeleteBtns(List<ImageView> userDeleteBtns) {
        this.userDeleteBtns = userDeleteBtns;
    }
}
