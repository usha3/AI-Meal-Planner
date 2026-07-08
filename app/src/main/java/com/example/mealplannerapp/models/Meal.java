package com.example.mealplannerapp.models;
public class Meal {

    private String name;
    private String type;
    private String imageUrl;
    private String cuisine;
    public String breakfast;
    public String lunch;
    public String dinner;
    public Meal(String name, String type, String imageUrl, String cuisine) {
        this.name = name;
        this.type = type;
        this.imageUrl = imageUrl;
        this.cuisine = cuisine;
    }

    public String getCuisine()
    {
        return cuisine;
    }
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}