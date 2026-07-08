package com.example.mealplannerapp.adapters;

import android.content.Context;
import android.os.Handler;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.models.MealDay;
import com.example.mealplannerapp.utils.MealStorage;
import com.example.mealplannerapp.utils.SimpleTextWatcher;

import java.util.List;

public class MealGridAdapter extends RecyclerView.Adapter<MealGridAdapter.ViewHolder> {

    private List<MealDay> list;
    private Context context;
    private Handler handler = new Handler();

    public MealGridAdapter(Context context, List<MealDay> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView day;
        AutoCompleteTextView breakfast, lunch, dinner;

        TextWatcher breakfastWatcher, lunchWatcher, dinnerWatcher;

        public ViewHolder(View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.txtDay);
            breakfast = itemView.findViewById(R.id.etBreakfast);
            lunch = itemView.findViewById(R.id.etLunch);
            dinner = itemView.findViewById(R.id.etDinner);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_day_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {

        MealDay item = list.get(position);

        h.day.setText(item.getDay());

        // Remove old watchers
        if (h.breakfastWatcher != null)
            h.breakfast.removeTextChangedListener(h.breakfastWatcher);
        if (h.lunchWatcher != null)
            h.lunch.removeTextChangedListener(h.lunchWatcher);
        if (h.dinnerWatcher != null)
            h.dinner.removeTextChangedListener(h.dinnerWatcher);

        // Set values
        h.breakfast.setText(item.getBreakfast());
        h.lunch.setText(item.getLunch());
        h.dinner.setText(item.getDinner());

        // Suggestions
        String[] meals = {
                "Idli", "Dosa", "Upma", "Poha",
                "Rice", "Dal", "Paneer Curry",
                "Roti", "Salad", "Pasta", "Sandwich"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_dropdown_item_1line,
                meals
        );

        h.breakfast.setAdapter(adapter);
        h.lunch.setAdapter(adapter);
        h.dinner.setAdapter(adapter);

        // 🔥 debounce save
        final Runnable[] saveRunnable = new Runnable[1];

        h.breakfastWatcher = new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.setBreakfast(s.toString());

                if (saveRunnable[0] != null) handler.removeCallbacks(saveRunnable[0]);
                saveRunnable[0] = () -> MealStorage.saveMeals(context, list);
                handler.postDelayed(saveRunnable[0], 500);
            }
        };

        h.lunchWatcher = new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.setLunch(s.toString());

                if (saveRunnable[0] != null) handler.removeCallbacks(saveRunnable[0]);
                saveRunnable[0] = () -> MealStorage.saveMeals(context, list);
                handler.postDelayed(saveRunnable[0], 500);
            }
        };

        h.dinnerWatcher = new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.setDinner(s.toString());

                if (saveRunnable[0] != null) handler.removeCallbacks(saveRunnable[0]);
                saveRunnable[0] = () -> MealStorage.saveMeals(context, list);
                handler.postDelayed(saveRunnable[0], 500);
            }
        };

        h.breakfast.addTextChangedListener(h.breakfastWatcher);
        h.lunch.addTextChangedListener(h.lunchWatcher);
        h.dinner.addTextChangedListener(h.dinnerWatcher);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}