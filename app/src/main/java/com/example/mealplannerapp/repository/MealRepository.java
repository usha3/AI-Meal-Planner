package com.example.mealplannerapp.repository;

import com.example.mealplannerapp.database.DatabaseHelper;

public class MealRepository {

    private final DatabaseHelper db;

    public MealRepository(DatabaseHelper db) {
        this.db = db;
    }

    public void save(String mealType, String date, String value) {
        db.saveMeal(mealType, value, date);
    }

    public String load(String mealType, String date) {
        return db.getMeal(mealType, date);
    }
}