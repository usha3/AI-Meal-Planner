package com.example.mealplannerapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mealplannerapp.api.ApiClient;
import com.example.mealplannerapp.R;
import com.example.mealplannerapp.api.MealApiService;
import com.example.mealplannerapp.api.MealResponse;
import com.example.mealplannerapp.models.Meal;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanMealActivity extends AppCompatActivity {

    Button btnCuisine, btnMealType, btnDiet, btnAllergy, btnExclude, continueBtn;

    List<Meal> mealList = new ArrayList<>();
    List<Meal> filteredMeals = new ArrayList<>();

    String[] cuisineOptions;
    boolean[] selectedCuisine;
    ArrayList<String> selectedCuisineList = new ArrayList<>();

    String[] mealTypeOptions = {"Breakfast", "Lunch", "Dinner"};
    boolean[] selectedMealType = new boolean[mealTypeOptions.length];
    ArrayList<String> selectedMealTypeList = new ArrayList<>();

    String[] dietOptions = {"Veg", "Non-Veg", "Vegan", "Keto"};
    boolean[] selectedDiet = new boolean[dietOptions.length];
    ArrayList<String> selectedDietList = new ArrayList<>();

    String[] allergyOptions = {"Egg", "Dairy", "Peanut", "Gluten"};
    boolean[] selectedAllergy = new boolean[allergyOptions.length];
    ArrayList<String> selectedAllergyList = new ArrayList<>();

    String[] excludeOptions = {"Onion", "Garlic", "Sugar"};
    boolean[] selectedExclude = new boolean[excludeOptions.length];
    ArrayList<String> selectedExcludeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_meal);

        btnCuisine = findViewById(R.id.btnCuisine);
        btnMealType = findViewById(R.id.btnMealType);
        btnDiet = findViewById(R.id.btnDiet);
        btnAllergy = findViewById(R.id.btnAllergy);
        btnExclude = findViewById(R.id.btnExclude);
        continueBtn = findViewById(R.id.continueBtn);

        loadMealsFromApi();

        setupCuisineDialog();
        setupMealTypeDialog();
        setupDietDialog();
        setupAllergyDialog();
        setupExcludeDialog();

       // continueBtn.setOnClickListener(v -> filterMeals());
        continueBtn.setOnClickListener(v -> {

            // 1. BEFORE FILTERING (optional but useful)
            Log.d("FILTER", "Total meals before filter = " + mealList.size());

            // 2. YOUR FILTERING LOGIC
            filterMeals();

            // 3. AFTER FILTERING
            Log.d("FILTER", "Filtered meals = " + filteredMeals.size());

            // 4. CHECK EMPTY CASE
            if (filteredMeals.isEmpty()) {
                Toast.makeText(this, "No filtered meals found", Toast.LENGTH_SHORT).show();
                return;
            }

            // 5. SEND TO NEXT SCREEN
            Intent intent = new Intent(PlanMealActivity.this, RecommendedMealsActivity.class);
            intent.putExtra("filteredMeals", new ArrayList<>(filteredMeals));
            startActivity(intent);
        });
    }

    // ================= API =================
    private void loadMealsFromApi() {

        MealApiService api = ApiClient.getClient().create(MealApiService.class);

        api.searchMeals("").enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    mealList.clear();
                    mealList.addAll(response.body().getMeals());
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Toast.makeText(PlanMealActivity.this, "API Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= CUISINE =================
    private void setupCuisineDialog() {

        btnCuisine.setOnClickListener(v -> {

            cuisineOptions = getCuisineArray();
            selectedCuisine = new boolean[cuisineOptions.length];

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Cuisine");

            builder.setMultiChoiceItems(cuisineOptions, selectedCuisine,
                    (dialog, which, isChecked) -> {

                        if (isChecked)
                            selectedCuisineList.add(cuisineOptions[which]);
                        else
                            selectedCuisineList.remove(cuisineOptions[which]);
                    });

            builder.setPositiveButton("OK", (d, w) ->
                    btnCuisine.setText(TextUtils.join(", ", selectedCuisineList)));

            builder.show();
        });
    }

    private String[] getCuisineArray() {

        return mealList.stream()
                .map(m -> m.getCuisine() != null ? m.getCuisine() : "Unknown")
                .distinct()
                .toArray(String[]::new);
    }

    // ================= MEAL TYPE =================
    private void setupMealTypeDialog() {

        btnMealType.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Meal Type");

            builder.setMultiChoiceItems(mealTypeOptions, selectedMealType,
                    (dialog, which, isChecked) -> {

                        if (isChecked)
                            selectedMealTypeList.add(mealTypeOptions[which]);
                        else
                            selectedMealTypeList.remove(mealTypeOptions[which]);
                    });

            builder.setPositiveButton("OK", (d, w) ->
                    btnMealType.setText(TextUtils.join(", ", selectedMealTypeList)));

            builder.show();
        });
    }

    // ================= DIET =================
    private void setupDietDialog() {

        btnDiet.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Diet");

            builder.setMultiChoiceItems(dietOptions, selectedDiet,
                    (dialog, which, isChecked) -> {

                        if (isChecked)
                            selectedDietList.add(dietOptions[which]);
                        else
                            selectedDietList.remove(dietOptions[which]);
                    });

            builder.setPositiveButton("OK", (d, w) ->
                    btnDiet.setText(TextUtils.join(", ", selectedDietList)));

            builder.show();
        });
    }

    // ================= ALLERGY =================
    private void setupAllergyDialog() {

        btnAllergy.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Allergies");

            builder.setMultiChoiceItems(allergyOptions, selectedAllergy,
                    (dialog, which, isChecked) -> {

                        if (isChecked)
                            selectedAllergyList.add(allergyOptions[which]);
                        else
                            selectedAllergyList.remove(allergyOptions[which]);
                    });

            builder.setPositiveButton("OK", (d, w) ->
                    btnAllergy.setText(TextUtils.join(", ", selectedAllergyList)));

            builder.show();
        });
    }

    // ================= EXCLUDE =================
    private void setupExcludeDialog() {

        btnExclude.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exclude Items");

            builder.setMultiChoiceItems(excludeOptions, selectedExclude,
                    (dialog, which, isChecked) -> {

                        if (isChecked)
                            selectedExcludeList.add(excludeOptions[which]);
                        else
                            selectedExcludeList.remove(excludeOptions[which]);
                    });

            builder.setPositiveButton("OK", (d, w) ->
                    btnExclude.setText(TextUtils.join(", ", selectedExcludeList)));

            builder.show();
        });
    }

    // ================= FILTER =================
    private void filterMeals() {

        filteredMeals.clear();

        for (Meal meal : mealList) {

            String name = meal.getName() != null ? meal.getName().toLowerCase() : "";
            String cuisine = meal.getCuisine() != null ? meal.getCuisine() : "";
            String diet = getDietType(meal);

            boolean cuisineMatch = selectedCuisineList.isEmpty();

            for (String c : selectedCuisineList) {
                if (meal.getCuisine() != null &&
                        meal.getCuisine().toLowerCase().contains(c.toLowerCase())) {
                    cuisineMatch = true;
                    break;
                }
            }
            boolean mealTypeMatch = selectedMealTypeList.isEmpty();

            for (String m : selectedMealTypeList) {
                if (getMealTypeByLogic(meal).toLowerCase().contains(m.toLowerCase())) {
                    mealTypeMatch = true;
                    break;
                }
            }
            boolean dietMatch = selectedDietList.isEmpty();

            for (String d : selectedDietList) {
                if (diet.toLowerCase().contains(d.toLowerCase())) {
                    dietMatch = true;
                    break;
                }
            }

            boolean allergyMatch = true;
            for (String a : selectedAllergyList) {
                if (name.contains(a.toLowerCase())) {
                    allergyMatch = false;
                    break;
                }
            }

            boolean excludeMatch = true;
            for (String e : selectedExcludeList) {
                if (name.contains(e.toLowerCase())) {
                    excludeMatch = false;
                    break;
                }
            }
            boolean noFiltersSelected =
                    selectedCuisineList.isEmpty() &&
                            selectedMealTypeList.isEmpty() &&
                            selectedDietList.isEmpty() &&
                            selectedAllergyList.isEmpty() &&
                            selectedExcludeList.isEmpty();

            if (noFiltersSelected ||
                    (cuisineMatch && mealTypeMatch && dietMatch && allergyMatch && excludeMatch)) {
                filteredMeals.add(meal);
            }
        }
        Log.d("FILTER", "Before sending size = " + filteredMeals.size());

        ArrayList<Meal> listToSend = new ArrayList<>(filteredMeals);

        Intent intent = new Intent(PlanMealActivity.this, RecommendedMealsActivity.class);
        intent.putExtra("filteredMeals", listToSend);
        startActivity(intent);
    }

    // ================= DIET LOGIC =================
    private String getDietType(Meal meal) {

        String name = meal.getName() != null ? meal.getName().toLowerCase() : "";

        if (name.contains("rice") || name.contains("bread") || name.contains("pasta"))
            return "Non-Keto";

        if (name.contains("chicken") || name.contains("egg") || name.contains("cheese"))
            return "Keto";

        return "Veg";
    }

    private String getMealTypeByLogic(Meal meal) {

        String name = meal.getName() != null ? meal.getName().toLowerCase() : "";

        if (name.contains("egg") || name.contains("toast"))
            return "Breakfast";

        if (name.contains("rice") || name.contains("chicken"))
            return "Lunch";

        return "Dinner";
    }
    private String clean(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }
}