package com.example.mealplannerapp.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.models.MealDay;

import java.util.List;

public class MealDayAdapter extends RecyclerView.Adapter<MealDayAdapter.ViewHolder> {

    private List<MealDay> list;

    public MealDayAdapter(List<MealDay> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        EditText breakfast, lunch, dinner;

        public ViewHolder(View v) {
            super(v);
            dayText = v.findViewById(R.id.dayText);
            breakfast = v.findViewById(R.id.breakfastInput);
            lunch = v.findViewById(R.id.lunchInput);
            dinner = v.findViewById(R.id.dinnerInput);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_day, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealDay item = list.get(position);

        holder.dayText.setText(item.getDay());
        holder.breakfast.setText(item.getBreakfast());
        holder.lunch.setText(item.getLunch());
        holder.dinner.setText(item.getDinner());

        // Save user input dynamically
        holder.breakfast.addTextChangedListener(new SimpleWatcher(s -> item.setBreakfast(s)));
        holder.lunch.addTextChangedListener(new SimpleWatcher(s -> item.setLunch(s)));
        holder.dinner.addTextChangedListener(new SimpleWatcher(s -> item.setDinner(s)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Helper TextWatcher
    class SimpleWatcher implements TextWatcher {
        private final OnTextChanged listener;

        SimpleWatcher(OnTextChanged l) { listener = l; }

        public void afterTextChanged(Editable s) {
            listener.onChanged(s.toString());
        }

        public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
        public void onTextChanged(CharSequence s, int a, int b, int c) {}
    }

    interface OnTextChanged {
        void onChanged(String text);
    }
}