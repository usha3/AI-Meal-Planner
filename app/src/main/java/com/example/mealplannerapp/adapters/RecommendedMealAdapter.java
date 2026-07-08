package com.example.mealplannerapp.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealplannerapp.R;
import com.example.mealplannerapp.models.RecommendedMeal;

import java.util.List;

public class RecommendedMealAdapter
        extends RecyclerView.Adapter<RecommendedMealAdapter.ViewHolder> {

    List<RecommendedMeal> list;

    public RecommendedMealAdapter(List<RecommendedMeal> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_recommended_meal,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        RecommendedMeal meal = list.get(position);

        holder.name.setText(meal.getName());

        holder.time.setText(
                "Cooking Time: " + meal.getTime());

        holder.calories.setText(
                "Calories: " + meal.getCalories());

        holder.link.setText("View Recipe");

        Glide.with(holder.itemView.getContext())
                .load(meal.getImageUrl())
                .into(holder.image);

        holder.link.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(meal.getRecipeLink()));

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView image;

        TextView name, time, calories, link;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.mealImage);

            name = itemView.findViewById(R.id.mealName);

            time = itemView.findViewById(R.id.mealTime);

            calories =
                    itemView.findViewById(R.id.mealCalories);

            link = itemView.findViewById(R.id.recipeLink);
        }
    }
}