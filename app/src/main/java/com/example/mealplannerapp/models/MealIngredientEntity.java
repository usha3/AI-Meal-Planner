package com.example.mealplannerapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meal_ingredients")
public class MealIngredientEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String mealName;
    public String ingredientName;

    public MealIngredientEntity(String mealName, String ingredientName) {
        this.mealName = mealName;
        this.ingredientName = ingredientName;
    }
}