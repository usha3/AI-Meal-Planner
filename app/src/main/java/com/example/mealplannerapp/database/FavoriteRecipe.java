package com.example.mealplannerapp.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class FavoriteRecipe {

    @PrimaryKey
    @NonNull
    private String name;

    private String imageUrl;

    public FavoriteRecipe(@NonNull String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}