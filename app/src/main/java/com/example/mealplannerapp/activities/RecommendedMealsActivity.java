package com.example.mealplannerapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.adapters.RecommendedMealAdapter;
import com.example.mealplannerapp.models.Meal;

import java.util.ArrayList;

public class RecommendedMealsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_meals);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ✅ GET DATA FROM INTENT
        ArrayList<Meal> meals =
                (ArrayList<Meal>) getIntent().getSerializableExtra("filteredMeals");

        Log.d("FILTER", "Received size = " + (meals != null ? meals.size() : "NULL"));

        // ✅ CHECK
        if (meals == null || meals.isEmpty()) {
            Toast.makeText(this, "No filtered meals received", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ SET ADAPTER (ONLY THIS — no extra method)


    }
    private void updateMeal(int position, ArrayList<Meal> meals) {

        meals.set(position, new Meal("Replaced Meal", "Updated", "", ""));

        recyclerView.getAdapter().notifyItemChanged(position);
    }
}