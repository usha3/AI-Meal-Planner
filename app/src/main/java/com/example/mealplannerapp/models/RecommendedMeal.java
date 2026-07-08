package com.example.mealplannerapp.models;

public class RecommendedMeal {

    String name;
    String time;
    String calories;
    String recipeLink;
    String imageUrl;

    public RecommendedMeal(String name,
                           String time,
                           String calories,
                           String recipeLink,
                           String imageUrl) {

        this.name = name;
        this.time = time;
        this.calories = calories;
        this.recipeLink = recipeLink;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getCalories() {
        return calories;
    }

    public String getRecipeLink() {
        return recipeLink;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}