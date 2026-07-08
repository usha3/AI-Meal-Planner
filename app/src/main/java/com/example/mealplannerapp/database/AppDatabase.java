package com.example.mealplannerapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {FavoriteRecipe.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();
}