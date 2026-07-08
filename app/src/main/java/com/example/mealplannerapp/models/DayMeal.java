package com.example.mealplannerapp.models;

public class DayMeal {
    public String day;
    public String breakfast = "";
    public String lunch = "";
    public String dinner = "";

    public DayMeal(String day) {
        this.day = day;
    }
}