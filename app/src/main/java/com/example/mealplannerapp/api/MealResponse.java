package com.example.mealplannerapp.api;

import com.example.mealplannerapp.models.Meal;

import java.util.List;

public class MealResponse {

    private List<Meal> meals;

    public List<Meal> getMeals() {
        return meals;
    }
}