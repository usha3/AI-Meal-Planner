package com.example.mealplannerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mealplannerapp.R;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ImageView image = findViewById(R.id.detailImage);

        TextView name = findViewById(R.id.detailName);
        TextView time = findViewById(R.id.detailTime);
        TextView calories = findViewById(R.id.detailCalories);
        TextView mealType = findViewById(R.id.detailMealType);
        TextView ingredients = findViewById(R.id.detailIngredients);
        TextView steps = findViewById(R.id.detailSteps);

        // NEW
        TextView cuisine = findViewById(R.id.detailCuisine);
        TextView course = findViewById(R.id.detailCourse);
        TextView description = findViewById(R.id.detailDescription);

        Intent intent = getIntent();

        name.setText(intent.getStringExtra("name"));

        time.setText("Cooking Time: " +
                intent.getStringExtra("time"));

        calories.setText("Calories: " +
                intent.getStringExtra("calories"));

        mealType.setText("Meal Type: " +
                intent.getStringExtra("mealType"));

        // NEW
        cuisine.setText("Cuisine: " +
                intent.getStringExtra("cuisine"));

        course.setText("Course: " +
                intent.getStringExtra("course"));

        description.setText(
                intent.getStringExtra("description"));

        ingredients.setText(
                intent.getStringExtra("ingredients"));

        steps.setText(
                intent.getStringExtra("instructions"));

        Glide.with(this)
                .load(intent.getStringExtra("image"))
                .into(image);
    }
}