package com.example.mealplannerapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recommended_meals")
public class MealRecommendationEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String mealName;

    public String ingredient;

    public String imageUrl;

    public String cookingTime;

    public String calories;

    public String recipeLink;

    public MealRecommendationEntity(
            String mealName,
            String ingredient,
            String imageUrl,
            String cookingTime,
            String calories,
            String recipeLink) {

        this.mealName = mealName;
        this.ingredient = ingredient;
        this.imageUrl = imageUrl;
        this.cookingTime = cookingTime;
        this.calories = calories;
        this.recipeLink = recipeLink;
    }
}