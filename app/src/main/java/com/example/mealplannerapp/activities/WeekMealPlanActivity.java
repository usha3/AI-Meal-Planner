package com.example.mealplannerapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.database.DatabaseHelper;
import com.example.mealplannerapp.repository.MealRepository;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class WeekMealPlanActivity extends AppCompatActivity {

    private TableLayout tableMeals;
    private MealRepository repo;

    private Calendar weekStart;

    private final String[] mealTypes = {
            "Breakfast", "Snack1", "Lunch", "Snack2", "Dinner"
    };

    private Map<String, String> cache = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_meal);

        tableMeals = findViewById(R.id.tableMeals);

        repo = new MealRepository(new DatabaseHelper(this));

        weekStart = Calendar.getInstance();
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        setupTable();
        loadWeek();
    }

    // ---------------- DATE ----------------
    private String getDate(int col) {
        Calendar c = (Calendar) weekStart.clone();
        c.add(Calendar.DAY_OF_MONTH, col);

        return c.get(Calendar.YEAR) + "-" +
                (c.get(Calendar.MONTH) + 1) + "-" +
                c.get(Calendar.DAY_OF_MONTH);
    }

    // ---------------- SETUP ----------------
    private void setupTable() {

        for (int r = 1; r < tableMeals.getChildCount(); r++) {

            TableRow row = (TableRow) tableMeals.getChildAt(r);

            String mealType = mealTypes[r - 1];

            for (int c = 0; c < row.getChildCount(); c++) {

                EditText cell = (EditText) row.getChildAt(c);
                int colIndex = c;

                attach(cell, mealType, colIndex);
            }
        }
    }

    // ---------------- AUTO SAVE ----------------
    private void attach(EditText cell, String mealType, int col) {

        Handler handler = new Handler(Looper.getMainLooper());
        final Runnable[] task = new Runnable[1];

        cell.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                String date = getDate(col);
                String value = s.toString();

                String key = mealType + "_" + date;
                cache.put(key, value);

                if (task[0] != null) handler.removeCallbacks(task[0]);

                task[0] = () -> repo.save(mealType, date, value);

                handler.postDelayed(task[0], 800);
            }

            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
        });
    }

    // ---------------- LOAD ----------------
    private void loadWeek() {

        for (int r = 1; r < tableMeals.getChildCount(); r++) {

            TableRow row = (TableRow) tableMeals.getChildAt(r);

            String mealType = mealTypes[r - 1];

            for (int c = 0; c < row.getChildCount(); c++) {

                EditText cell = (EditText) row.getChildAt(c);

                String date = getDate(c);
                String key = mealType + "_" + date;

                String value;

                if (cache.containsKey(key)) {
                    value = cache.get(key);
                } else {
                    value = repo.load(mealType, date);
                    cache.put(key, value);
                }

                cell.setText(value == null ? "" : value);
            }
        }
    }
}