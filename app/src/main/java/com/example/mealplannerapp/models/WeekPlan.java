package com.example.mealplannerapp.models;

import java.util.HashMap;

public class WeekPlan {
    public String weekId;
    public HashMap<String, Meal> dayMeals = new HashMap<>();
}