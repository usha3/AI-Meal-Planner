package com.example.mealplannerapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.adapters.FavoriteAdapter;
import com.example.mealplannerapp.database.DatabaseClient;
import com.example.mealplannerapp.database.FavoriteRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private List<FavoriteRecipe> favoriteList;
    private LinearLayout emptyStateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // FIXED IDs (match XML exactly)
        recyclerView = findViewById(R.id.recyclerFavorites);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        favoriteList = new ArrayList<>();
        adapter = new FavoriteAdapter(this, favoriteList);
        recyclerView.setAdapter(adapter);

        loadFavorites();
    }

    private void loadFavorites() {

        Executors.newSingleThreadExecutor().execute(() -> {

            List<FavoriteRecipe> favorites =
                    DatabaseClient.getInstance(this)
                            .getDatabase()
                            .favoriteDao()
                            .getAllFavorites();

            runOnUiThread(() -> {

                favoriteList.clear();
                favoriteList.addAll(favorites);

                adapter.notifyDataSetChanged();

                checkEmptyState();
            });
        });
    }

    private void checkEmptyState() {
        if (favoriteList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}