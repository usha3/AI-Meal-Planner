package com.example.mealplannerapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealplannerapp.R;
import com.example.mealplannerapp.models.Recipe;

import java.util.List;

public class AiRecipeAdapter extends RecyclerView.Adapter<AiRecipeAdapter.ViewHolder> {

    private List<Recipe> recipeList;
    private OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public AiRecipeAdapter(List<Recipe> recipeList,
                           OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ai_recipe, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        Recipe recipe = recipeList.get(position);

        holder.tvRecipeName.setText(recipe.getName());

        holder.tvMealType.setText(
                recipe.getMealType() == null
                        ? "Meal"
                        : recipe.getMealType()
        );

        holder.tvCookingTime.setText(
                "⏱ " +
                        (recipe.getCookingTime() == null
                                ? "N/A"
                                : recipe.getCookingTime())
        );

        holder.tvCalories.setText(
                "🔥 " + recipe.getCalories() + " kcal"
        );

        Glide.with(holder.itemView.getContext())
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.ic_food_placeholder)
                .error(R.drawable.ic_food_placeholder)
                .centerCrop()
                .into(holder.ivRecipe);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList == null ? 0 : recipeList.size();
    }

    public void updateList(List<Recipe> newList) {
        recipeList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivRecipe;
        TextView tvRecipeName;
        TextView tvMealType;
        TextView tvCookingTime;
        TextView tvCalories;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivRecipe = itemView.findViewById(R.id.ivRecipe);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvMealType = itemView.findViewById(R.id.tvMealType);
            tvCookingTime = itemView.findViewById(R.id.tvCookingTime);
            tvCalories = itemView.findViewById(R.id.tvCalories);
        }
    }
}