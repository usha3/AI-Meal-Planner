package com.example.mealplannerapp.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.adapters.AiRecipeAdapter;
import com.example.mealplannerapp.data.AppDatabase;
import com.example.mealplannerapp.models.MealDay;
import com.example.mealplannerapp.models.Recipe;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;

public class AiRecipesActivity extends AppCompatActivity {

    private EditText searchRecipes;
    private MaterialButton btnGenerateDay, btnUsePlan;
    private RecyclerView recipeRecycler;

    private AiRecipeAdapter adapter;
    private List<Recipe> recipeList;
    private Chip chipBreakfast, chipLunch, chipDinner, chipProtein;
    private String selectedFilter = "";
    private List<Recipe> allRecipes = new ArrayList<>();
    private final String[] aiSuggestions = {
            "🍝 AI Suggestion: Creamy Pasta Bowl",
            "🥗 AI Suggestion: High Protein Salad",
            "🍛 AI Suggestion: Healthy Indian Thali",
            "🍗 AI Suggestion: Grilled Chicken Meal",
            "🥑 AI Suggestion: Avocado Toast Breakfast"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_recipes);

        initViews();
        setupRecyclerView();
        setupChipClicks();

        btnGenerateDay.setOnClickListener(v -> generateDayPlan());

        btnUsePlan.setOnClickListener(v ->
                Toast.makeText(this,
                        "Plan Saved Successfully",
                        Toast.LENGTH_SHORT).show());
    }

    private void initViews() {
        searchRecipes = findViewById(R.id.searchRecipes);
        btnGenerateDay = findViewById(R.id.btnGenerateDay);
        btnUsePlan = findViewById(R.id.btnUsePlan);
        recipeRecycler = findViewById(R.id.recipeRecycler);
        chipBreakfast = findViewById(R.id.chipBreakfast);
        chipLunch = findViewById(R.id.chipLunch);
        chipDinner = findViewById(R.id.chipDinner);
        chipProtein = findViewById(R.id.chipProtein);
    }

    private void setupRecyclerView() {

        recipeList = new ArrayList<>();

        recipeList.add(new Recipe(
                "Paneer Tikka",
                "https://picsum.photos/300",
                "20 min",
                350,
                "",
                "High Protein",
                "",
                "Indian",
                "Starter",
                "Healthy paneer dish"
        ));

        recipeList.add(new Recipe(
                "Veg Fried Rice",
                "https://picsum.photos/301",
                "25 min",
                420,
                "",
                "Lunch",
                "",
                "Asian",
                "Main Course",
                "Delicious rice recipe"
        ));

        recipeList.add(new Recipe(
                "Oats Breakfast Bowl",
                "https://picsum.photos/302",
                "10 min",
                250,
                "",
                "Breakfast",
                "",
                "Healthy",
                "Breakfast",
                "Nutritious oats bowl"
        ));

        adapter = new AiRecipeAdapter(recipeList, recipe ->
                Toast.makeText(
                        AiRecipesActivity.this,
                        recipe.getName(),
                        Toast.LENGTH_SHORT
                ).show()
        );

        recipeRecycler.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recipeRecycler.setAdapter(adapter);
        allRecipes.clear();
        allRecipes.addAll(recipeList);
    }
    private void setupChipClicks() {

        chipBreakfast.setOnClickListener(v ->
                filterRecipes("Breakfast"));

        chipLunch.setOnClickListener(v ->
                filterRecipes("Lunch"));

        chipDinner.setOnClickListener(v ->
                filterRecipes("Dinner"));

        chipProtein.setOnClickListener(v ->
                filterRecipes("High Protein"));
    }
    private void saveGeneratedMeals(
            String breakfast,
            String morningSnack,
            String lunch,
            String eveningSnack,
            String dinner) {

        String today =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault())
                        .format(new Date());

        Executors.newSingleThreadExecutor().execute(() -> {

            AppDatabase db =
                    AppDatabase.getInstance(this);

            MealDay mealDay =
                    db.mealDao().getMealByDate(today);

            if (mealDay == null) {
                mealDay = new MealDay();
                mealDay.setDate(today);
            }

            mealDay.setBreakfast(breakfast);
            mealDay.setMorningSnack(morningSnack);
            mealDay.setLunch(lunch);
            mealDay.setEveningSnack(eveningSnack);
            mealDay.setDinner(dinner);

            db.mealDao().upsert(mealDay);

            runOnUiThread(() ->
                    Toast.makeText(
                            this,
                            "Today's plan generated!",
                            Toast.LENGTH_SHORT
                    ).show());
        });
    }
    private void filterRecipes(String mealType) {

        if (mealType.equals(selectedFilter)) {

            selectedFilter = "";
            adapter.updateList(allRecipes);

            Toast.makeText(
                    this,
                    "Showing All Recipes",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        selectedFilter = mealType;

        List<Recipe> filteredList = new ArrayList<>();

        for (Recipe recipe : allRecipes) {

            if (recipe.getMealType() != null &&
                    recipe.getMealType()
                            .equalsIgnoreCase(mealType)) {

                filteredList.add(recipe);
            }
        }

        adapter.updateList(filteredList);
    }
    private void generateDayPlan() {

        String breakfast = "Oats Breakfast Bowl";
        String morningSnack = "Apple & Almonds";
        String lunch = "Veg Fried Rice";
        String eveningSnack = "Protein Smoothie";
        String dinner = "Paneer Tikka";

        saveGeneratedMeals(
                breakfast,
                morningSnack,
                lunch,
                eveningSnack,
                dinner
        );

        Random random = new Random();

        String suggestion =
                aiSuggestions[random.nextInt(aiSuggestions.length)];

        Toast.makeText(
                this,
                suggestion,
                Toast.LENGTH_LONG
        ).show();
    }
}