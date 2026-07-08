package com.example.mealplannerapp.models;

public class Recipe {

    private String name;
    private String imageUrl;
    private String cookingTime;
    private long calories;
    private String ingredients;
    private String mealType;
    private String instructions;
    private String cuisine;
    private String course;
    private String description;
    private boolean favorite;

    public Recipe() {
    }

    public Recipe(String name, String imageUrl,
                  String cookingTime,
                  long calories,
                  String ingredients,
                  String mealType,
                  String instructions,
                  String cuisine,
                  String course,
                  String description) {

        this.name = name;
        this.imageUrl = imageUrl;
        this.cookingTime = cookingTime;
        this.calories = calories;
        this.ingredients = ingredients;
        this.mealType = mealType;
        this.instructions = instructions;
        this.cuisine = cuisine;
        this.course = course;
        this.description = description;
    }
    public Recipe(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
    public boolean isFavorite() {return favorite;}
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getCookingTime() { return cookingTime; }
    public long getCalories() { return calories; }
    public String getIngredients() { return ingredients; }
    public String getMealType() { return mealType; }
    public String getInstructions() { return instructions; }
    public String getCuisine() {return cuisine;}
    public String getCourse() {return course;}
    public String getDescription() {return description;}
    public void setFavorite(boolean favorite) {this.favorite = favorite;}
}