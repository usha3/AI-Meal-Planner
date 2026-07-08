package com.example.mealplannerapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteRecipe recipe);

    @Delete
    void delete(FavoriteRecipe recipe);

    @Query("SELECT * FROM favorites")
    List<FavoriteRecipe> getAllFavorites();

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE name = :name)")
    boolean isFavorite(String name);

    @Query("DELETE FROM favorites WHERE name = :name")
    void deleteByName(String name);
}