package com.example.mealplannerapp.database;

import android.util.Log;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mealplannerapp.models.MealIngredientEntity;

import java.util.List;

@Dao
public interface MealIngredientDao {
    @Query("SELECT * FROM meal_ingredients")
    List<MealIngredientEntity> getAll();

    @Insert
    void insert(MealIngredientEntity entity);

    @Query("SELECT ingredientName FROM meal_ingredients WHERE LOWER(mealName) = LOWER(:mealName)")
    List<String> getIngredientNamesForMeal(String mealName);

    @Query("SELECT COUNT(*) FROM meal_ingredients")
    int getCount();
}