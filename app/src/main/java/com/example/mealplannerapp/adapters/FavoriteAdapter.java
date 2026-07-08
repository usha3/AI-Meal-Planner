package com.example.mealplannerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealplannerapp.R;
import com.example.mealplannerapp.activities.RecipeDetailActivity;
import com.example.mealplannerapp.database.FavoriteRecipe;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private Context context;
    private List<FavoriteRecipe> list;

    public FavoriteAdapter(Context context, List<FavoriteRecipe> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_favorite_recipe, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FavoriteRecipe item = list.get(position);

        holder.name.setText(item.getName());

        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.favoriteImage);
            name = itemView.findViewById(R.id.favoriteName);
        }
    }

    public void updateList(List<FavoriteRecipe> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }
}