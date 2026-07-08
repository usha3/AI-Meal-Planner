package com.example.mealplannerapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mealplannerapp.models.MealRecommendationEntity;

import java.util.List;

@Dao
public interface MealRecommendationDao {

    @Insert
    void insert(MealRecommendationEntity meal);

    @Query("SELECT * FROM recommended_meals WHERE ingredient LIKE '%' || :ingredient || '%'")
    List<MealRecommendationEntity> getMealsByIngredient(String ingredient);
}