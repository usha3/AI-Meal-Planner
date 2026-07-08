package com.example.mealplannerapp.models;

import java.io.Serializable;
import java.util.List;

public class MealDetail implements Serializable {

    private String mealId;
    private String name;
    private String image;
    private String instructions;
    private String cookingTime;
    private String fullRecipeUrl;
    private List<String> ingredients;
    private String strMealThumb;

    public MealDetail() {}

    public MealDetail(String mealId, String name, String image,
                      String instructions, String cookingTime,
                      String fullRecipeUrl, List<String> ingredients, String strMealThumb) {

        this.mealId = mealId;
        this.name = name;
        this.image = image;
        this.instructions = instructions;
        this.cookingTime = cookingTime;
        this.fullRecipeUrl = fullRecipeUrl;
        this.ingredients = ingredients;
        this.strMealThumb = strMealThumb;
    }

    public String getMealId() { return mealId; }

    public String getName() { return name; }

    public String getImage() { return image; }

    public String getInstructions() { return instructions; }
    public String getStrMealThumb() {
        return strMealThumb;
    }

    public String getCookingTime() { return cookingTime; }

    public String getFullRecipeUrl() { return fullRecipeUrl; }

    public List<String> getIngredients() { return ingredients; }
}