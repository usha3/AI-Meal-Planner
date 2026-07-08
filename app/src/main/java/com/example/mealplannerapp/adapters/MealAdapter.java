package com.example.mealplannerapp.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.database.DatabaseHelper;
import com.example.mealplannerapp.models.MealRow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    private Context context;
    private List<MealRow> list;
    private DatabaseHelper dbHelper;
    private String weekDate;

    private Map<String, String> cache = new HashMap<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    public MealAdapter(Context context, List<MealRow> list,
                       DatabaseHelper dbHelper, String weekDate) {
        this.context = context;
        this.list = list;
        this.dbHelper = dbHelper;
        this.weekDate = weekDate;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtMeal;
        LinearLayout layoutDays;
        EditText[] days = new EditText[7];

        public ViewHolder(View itemView) {
            super(itemView);

            txtMeal = itemView.findViewById(R.id.txtMeal);
            layoutDays = itemView.findViewById(R.id.layoutDays);

            for (int i = 0; i < 7; i++) {
                EditText et = new EditText(itemView.getContext());

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(120, 120);

                et.setLayoutParams(params);
                et.setGravity(Gravity.CENTER);
                et.setBackgroundResource(R.drawable.cell_border);

                layoutDays.addView(et);
                days[i] = et;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_meal_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MealRow row = list.get(position);

        holder.txtMeal.setText(row.mealName);

        for (int i = 0; i < 7; i++) {

            EditText et = holder.days[i];

            String date = weekDate + "_D" + i; // simple unique day slot key
            String mealType = row.mealName;
            String key = mealType + "_" + date;

            // ================= LOAD =================
            String value;

            if (cache.containsKey(key)) {
                value = cache.get(key);
            } else {
                value = dbHelper.getMeal(mealType, date);
                cache.put(key, value);
            }

            et.setText(value == null ? "" : value);

            // ================= SAFE TEXT WATCHER =================
            et.addTextChangedListener(new TextWatcher() {

                Runnable runnable;

                @Override
                public void afterTextChanged(Editable s) {

                    String newValue = s.toString();

                    cache.put(key, newValue);

                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                    }

                    runnable = () -> dbHelper.saveMeal(
                            mealType,
                            newValue,
                            date
                    );

                    handler.postDelayed(runnable, 800);
                }

                @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
                @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}