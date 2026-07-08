package com.example.mealplannerapp.models;

public class MealCell {
    public String mealType;
    public String date;
    public String value;

    public MealCell(String mealType, String date, String value) {
        this.mealType = mealType;
        this.date = date;
        this.value = value;
    }
}