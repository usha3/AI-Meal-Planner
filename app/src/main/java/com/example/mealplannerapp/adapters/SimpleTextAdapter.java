package com.example.mealplannerapp.adapters;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;

import java.util.List;

public class SimpleTextAdapter extends RecyclerView.Adapter<SimpleTextAdapter.ViewHolder> {

    List<String> list;

    public SimpleTextAdapter(List<String> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {

        View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String item = list.get(position);

        String label = "";
        String value = "";

        if (item.contains("-")) {
            String[] parts = item.split("-", 2);
            label = parts[0].trim();
            value = parts[1].trim();
        }

        holder.left.setText(label);
        holder.right.setText(value);
        holder.dash.setText("-");

        // COLORS
        if (label.equalsIgnoreCase("Breakfast")) {
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor("#FFF3E0"));
        } else if (label.equalsIgnoreCase("Morning Snack")) {
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor("#E3F2FD"));
        } else if (label.equalsIgnoreCase("Lunch")) {
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"));
        } else if (label.equalsIgnoreCase("Evening Snack")) {
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor("#F3E5F5"));
        } else if (label.equalsIgnoreCase("Dinner")) {
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor("#ECEFF1"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        android.widget.TextView left, dash, right;

        public ViewHolder(android.view.View itemView) {
            super(itemView);

            left = itemView.findViewById(R.id.txtLeft);
            dash = itemView.findViewById(R.id.txtDash);
            right = itemView.findViewById(R.id.txtRight);
        }
    }
}