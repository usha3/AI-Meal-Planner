package com.example.mealplannerapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.adapters.RecipeAdapter;
import com.example.mealplannerapp.database.DatabaseClient;
import com.example.mealplannerapp.database.FavoriteRecipe;
import com.example.mealplannerapp.models.Recipe;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class RecipesActivity extends AppCompatActivity {

    FirebaseFirestore db;

    SearchView searchView;
    RecyclerView recyclerView;
    ImageView filterIcon;
    ImageView favoritesIcon;
    TextView emptyStateText;
    RecipeAdapter adapter;

    // DATA
    List<Recipe> fullList = new ArrayList<>();
    List<Recipe> displayedList = new ArrayList<>();

    // SEARCH QUERY (NEW)
    private String searchQuery = "";

    // PAGINATION
    private static final int LIMIT = 20;
    private DocumentSnapshot lastVisible;
    private boolean isLoading = false;

    // FAVORITES
    private Set<String> favoriteNames = new HashSet<>();

    // FILTER STATES
    private String selectedCategory = "All";
    private String selectedCuisine = "All";
    private String selectedCalories = "All";
    private String selectedTime = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        searchView = findViewById(R.id.searchView);
        LinearLayout container = findViewById(R.id.searchContainer);

        container.setOnClickListener(v -> {
            searchView.setIconified(false);
            searchView.requestFocus();
        });

        // remove underline
        View plate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        if (plate != null) plate.setBackground(null);

        EditText text = searchView.findViewById(
                getResources().getIdentifier("search_src_text", "id", getPackageName()));
        if (text != null) text.setBackground(null);

        recyclerView = findViewById(R.id.recipeRecycler);
        filterIcon = findViewById(R.id.filterIcon);
        favoritesIcon = findViewById(R.id.favoritesIcon);
        emptyStateText = findViewById(R.id.emptyStateText);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new RecipeAdapter(this, displayedList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadRecipes();

        filterIcon.setOnClickListener(v -> showFilterDialog());

        favoritesIcon.setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class))
        );

        // ================= SEARCH FIX =================
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText != null ? newText.trim().toLowerCase() : "";
                applyFilters();
                return true;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (!rv.canScrollVertically(1)) {
                    loadMoreRecipes();
                }
            }
        });
    }

    // ---------------- LOAD FIRST PAGE ----------------
    private void loadRecipes() {

        db.collection("recipes")
                .limit(LIMIT)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) return;

                    fullList.clear();

                    lastVisible = snapshot.getDocuments()
                            .get(snapshot.size() - 1);

                    for (DocumentSnapshot doc : snapshot) {
                        Recipe r = doc.toObject(Recipe.class);
                        if (r != null) fullList.add(r);
                    }

                    applyFilters();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // ---------------- LOAD MORE ----------------
    private void loadMoreRecipes() {

        if (isLoading || lastVisible == null) return;

        isLoading = true;

        db.collection("recipes")
                .startAfter(lastVisible)
                .limit(LIMIT)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.isEmpty()) {

                        lastVisible = snapshot.getDocuments()
                                .get(snapshot.size() - 1);

                        for (DocumentSnapshot doc : snapshot) {
                            Recipe r = doc.toObject(Recipe.class);
                            if (r != null) fullList.add(r);
                        }

                        applyFilters();
                    }

                    isLoading = false;
                })
                .addOnFailureListener(e -> isLoading = false);
    }

    // ---------------- FILTER DIALOG ----------------
    private void showFilterDialog() {

        String[] categories = {"All", "Veg", "Non-Veg", "Vegan", "Dessert"};
        String[] cuisines = {"All", "Indian", "Italian", "Chinese", "Mexican"};
        String[] calories = {"All", "Low", "Medium", "High"};
        String[] time = {"All", "Quick (<20 min)", "Medium (20–40 min)", "Long (>40 min)"};

        new AlertDialog.Builder(this)
                .setTitle("Select Category")
                .setItems(categories, (d, i) -> {
                    selectedCategory = categories[i];

                    new AlertDialog.Builder(this)
                            .setTitle("Select Cuisine")
                            .setItems(cuisines, (d2, i2) -> {
                                selectedCuisine = cuisines[i2];

                                new AlertDialog.Builder(this)
                                        .setTitle("Select Calories")
                                        .setItems(calories, (d3, i3) -> {
                                            selectedCalories = calories[i3];

                                            new AlertDialog.Builder(this)
                                                    .setTitle("Select Cooking Time")
                                                    .setItems(time, (d4, i4) -> {
                                                        selectedTime = time[i4];
                                                        applyFilters();
                                                    })
                                                    .show();

                                        })
                                        .show();

                            })
                            .show();
                })
                .show();
    }

    // ---------------- MASTER FILTER ENGINE (FIXED) ----------------
    private void applyFilters() {

        List<Recipe> filtered = new ArrayList<>();

        for (Recipe r : fullList) {

            if (r == null || r.getName() == null) continue;

            boolean matchSearch =
                    searchQuery.isEmpty() ||
                            r.getName().toLowerCase().contains(searchQuery);

            boolean matchCategory =
                    selectedCategory.equals("All") ||
                            safeEquals(r.getMealType(), selectedCategory);

            boolean matchCuisine =
                    selectedCuisine.equals("All") ||
                            safeEquals(r.getCuisine(), selectedCuisine);

            boolean matchCalories =
                    selectedCalories.equals("All") ||
                            checkCaloriesSafe(r.getCalories(), selectedCalories);

            boolean matchTime =
                    selectedTime.equals("All") ||
                            checkTimeSafe(r.getCookingTime(), selectedTime);

            if (matchSearch && matchCategory && matchCuisine && matchCalories && matchTime) {
                filtered.add(r);
            }
        }

        displayedList.clear();
        displayedList.addAll(filtered);

        adapter.updateList(displayedList);

// ---------------- EMPTY STATE LOGIC ----------------
        if (filtered.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // ---------------- CALORIES CHECK ----------------
    private boolean checkCaloriesSafe(long calories, String filter) {

        if (calories <= 0) return true;

        switch (filter) {
            case "Low": return calories < 300;
            case "Medium": return calories <= 600;
            case "High": return calories > 600;
            default: return true;
        }
    }

    // ---------------- TIME CHECK ----------------
    private boolean checkTimeSafe(String timeStr, String filter) {

        int time = extractTime(timeStr);

        switch (filter) {
            case "Quick (<20 min)": return time < 20;
            case "Medium (20–40 min)": return time >= 20 && time <= 40;
            case "Long (>40 min)": return time > 40;
            default: return true;
        }
    }

    private int extractTime(String text) {
        if (text == null) return 0;
        try {
            return Integer.parseInt(text.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean safeEquals(String a, String b) {
        if (a == null || b == null) return false;
        return a.trim().toLowerCase().contains(b.trim().toLowerCase());
    }

    // ---------------- FAVORITES ----------------
    @Override
    protected void onResume() {
        super.onResume();
        loadFavoritesFromDB();
    }

    private void loadFavoritesFromDB() {

        Executors.newSingleThreadExecutor().execute(() -> {

            List<FavoriteRecipe> favorites =
                    DatabaseClient.getInstance(this)
                            .getDatabase()
                            .favoriteDao()
                            .getAllFavorites();

            runOnUiThread(() -> {

                favoriteNames.clear();
                for (FavoriteRecipe f : favorites) {
                    favoriteNames.add(f.getName());
                }

                adapter.setFavorites(favorites);
                adapter.notifyDataSetChanged();
            });
        });
    }
}