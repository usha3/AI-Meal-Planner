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
import com.example.mealplannerapp.activities.FitnessDetailActivity;
import com.example.mealplannerapp.R;
import com.example.mealplannerapp.models.FitnessItem;

import java.util.List;

public class FitnessAdapter extends RecyclerView.Adapter<FitnessAdapter.ViewHolder> {

    Context context;
    List<FitnessItem> list;

    public FitnessAdapter(Context context, List<FitnessItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_fitness, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FitnessItem item = list.get(position);

        holder.txtTitle.setText(item.getTitle());
        holder.txtDuration.setText(item.getDuration());

        Glide.with(context)
                .load(item.getImage())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context,
                    FitnessDetailActivity.class);

            intent.putExtra("title", item.getTitle());
            intent.putExtra("image", item.getImage());
            intent.putExtra("duration", item.getDuration());
            intent.putExtra("video", item.getVideoId());
            intent.putExtra("benefits", item.getBenefits());
            intent.putExtra("description", item.getDescription());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView txtTitle, txtDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgFitness);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDuration = itemView.findViewById(R.id.txtDuration);
        }
    }
}