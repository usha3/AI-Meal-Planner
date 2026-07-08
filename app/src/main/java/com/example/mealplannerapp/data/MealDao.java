package com.example.mealplannerapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import com.example.mealplannerapp.models.MealDay;

import java.util.List;

@Dao
public interface MealDao {

    @Query("SELECT * FROM meal_day")
    LiveData<List<MealDay>> getAllMeals();

    @Query("SELECT * FROM meal_day WHERE date = :date LIMIT 1")
    MealDay getMealByDate(String date);

    @Upsert
    void upsert(MealDay meal);

    @Query("DELETE FROM meal_day")
    void deleteAll();
}