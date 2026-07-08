package com.example.mealplannerapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.models.WeekDay;

import java.util.List;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.VH> {

    List<WeekDay> list;
    OnDayClick listener;

    private int selectedPosition = -1;

    public interface OnDayClick {
        void onClick(WeekDay day);
    }

    public WeekAdapter(List<WeekDay> list, OnDayClick listener) {
        this.list = list;
        this.listener = listener;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_week, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        WeekDay d = list.get(position);

        holder.day.setText(d.getDayName());
        holder.date.setText(String.valueOf(d.getDayNumber()));

        boolean isSelected = position == selectedPosition;

        if (isSelected) {
            holder.itemView.setBackgroundResource(R.drawable.bg_selected);
            holder.day.setTextColor(android.graphics.Color.WHITE);
            holder.date.setTextColor(android.graphics.Color.WHITE);

        } else if (d.isToday()) {
            holder.itemView.setBackgroundResource(R.drawable.bg_today);
            holder.day.setTextColor(android.graphics.Color.BLACK);
            holder.date.setTextColor(android.graphics.Color.BLACK);

        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_normal);
            holder.day.setTextColor(android.graphics.Color.BLACK);
            holder.date.setTextColor(android.graphics.Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {

            int oldPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // 🔥 Only update changed items (smooth UI)
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onClick(d);
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView day, date;

        VH(View v) {
            super(v);
            day = v.findViewById(R.id.txtDay);
            date = v.findViewById(R.id.txtDate);
        }
    }
}