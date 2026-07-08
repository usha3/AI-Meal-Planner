package com.example.mealplannerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealplannerapp.R;
import com.example.mealplannerapp.activities.RecipeDetailActivity;
import com.example.mealplannerapp.database.DatabaseClient;
import com.example.mealplannerapp.database.FavoriteRecipe;
import com.example.mealplannerapp.models.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private Context context;
    private List<Recipe> list;
    private Set<String> favoriteNames = new HashSet<>();

    public RecipeAdapter(Context context, List<Recipe> list) {
        this.context = context;
        this.list = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Recipe recipe = list.get(position);

        holder.recipeName.setText(
                recipe.getName() != null ? recipe.getName() : "No Name"
        );

        Glide.with(holder.itemView.getContext())
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.recipeImage);

        // OPEN DETAILS
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, RecipeDetailActivity.class);

            intent.putExtra("name", recipe.getName());
            intent.putExtra("image", recipe.getImageUrl());
            intent.putExtra("time", recipe.getCookingTime());
            intent.putExtra("calories", recipe.getCalories());
            intent.putExtra("ingredients", recipe.getIngredients());
            intent.putExtra("mealType", recipe.getMealType());
            intent.putExtra("instructions", recipe.getInstructions());
            intent.putExtra("cuisine", recipe.getCuisine());
            intent.putExtra("course", recipe.getCourse());
            intent.putExtra("description", recipe.getDescription());

            context.startActivity(intent);
        });

        // FAVORITE STATE
        boolean isFav = favoriteNames.contains(recipe.getName());
        updateFavoriteUI(holder, isFav);

        // FAVORITE CLICK
        holder.favoriteBtn.setOnClickListener(v -> {

            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Recipe recipeItem = list.get(pos);
            String name = recipeItem.getName();

            boolean currentlyFav = favoriteNames.contains(name);

            Executors.newSingleThreadExecutor().execute(() -> {

                if (currentlyFav) {
                    DatabaseClient.getInstance(context)
                            .getDatabase()
                            .favoriteDao()
                            .deleteByName(name);
                } else {
                    DatabaseClient.getInstance(context)
                            .getDatabase()
                            .favoriteDao()
                            .insert(new FavoriteRecipe(name, recipeItem.getImageUrl()));
                }

                holder.itemView.post(() -> {

                    if (currentlyFav) {
                        favoriteNames.remove(name);
                    } else {
                        favoriteNames.add(name);
                    }

                    notifyItemChanged(pos);
                });
            });
        });
    }

    private void updateFavoriteUI(ViewHolder holder, boolean isFav) {

        if (isFav) {
            holder.favoriteBtn.setImageResource(R.drawable.ic_favorite);
            holder.favoriteBtn.setColorFilter(
                    ContextCompat.getColor(context, R.color.favorite_pink)
            );
        } else {
            holder.favoriteBtn.setImageResource(R.drawable.ic_favorite_border);
            holder.favoriteBtn.clearColorFilter();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // 🔥 IMPORTANT: FIXED update method (best practice)
    public void updateList(List<Recipe> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    // FAVORITES SYNC
    public void setFavorites(List<FavoriteRecipe> favorites) {

        favoriteNames.clear();

        for (FavoriteRecipe f : favorites) {
            favoriteNames.add(f.getName());
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView recipeImage;
        TextView recipeName;
        ImageView favoriteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeName = itemView.findViewById(R.id.recipeName);
            favoriteBtn = itemView.findViewById(R.id.favoriteBtn);
        }
    }
}