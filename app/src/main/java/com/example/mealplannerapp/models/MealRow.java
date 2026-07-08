package com.example.mealplannerapp.models;

public class MealRow {

    public String mealName;
    public String[] days;

    public MealRow(String mealName) {
        this.mealName = mealName;
        this.days = new String[7];
    }
}