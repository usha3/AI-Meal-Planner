package com.example.mealplannerapp.activities;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.Gravity;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mealplannerapp.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

public class WeekPlanActivity extends AppCompatActivity {

    TableLayout table;
    Button btnPrev, btnNext;
    Spinner spMonth, spYear;
    CheckBox cbSameEveryday, cbLastWeek;

    Calendar calendar;

    String[] meals = {"Breakfast", "Morning Snack", "Lunch", "Evening Snack", "Dinner"};
    String[] days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

    EditText[][] cells = new EditText[5][7];

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_plan);

        table = findViewById(R.id.table);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        spMonth = findViewById(R.id.spMonth);
        spYear = findViewById(R.id.spYear);
        cbSameEveryday = findViewById(R.id.cbSameEveryday);
        cbLastWeek = findViewById(R.id.cbLastWeek);

        prefs = getSharedPreferences("meal_data", MODE_PRIVATE);

        calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);

        setupSpinners();
        buildTable();
        loadWeekData();

        btnPrev.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            buildTable();
            loadWeekData();
        });

        btnNext.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            buildTable();
            loadWeekData();
        });

        cbSameEveryday.setOnCheckedChangeListener((b, checked) -> {
            if (checked) copyFirstColumnToAll();
        });

        cbLastWeek.setOnCheckedChangeListener((b, checked) -> {
            if (checked) loadLastWeek();
        });
    }

    // ---------------- TABLE ----------------
    private void buildTable() {
        table.removeAllViews();

        // HEADER ROW
        TableRow header = new TableRow(this);

        header.addView(createHeaderCell("Meal"));

        for (int i = 0; i < 7; i++) {
            header.addView(createHeaderCell(days[i]));
        }
        table.addView(header);

        // DATA ROWS
        for (int i = 0; i < meals.length; i++) {
            TableRow row = new TableRow(this);

            TextView meal = createHeaderCell(meals[i]);
            row.addView(meal);

            for (int j = 0; j < 7; j++) {
                EditText et = new EditText(this);
                et.setHint("Enter");
                et.setMinEms(5);
                et.setPadding(8,8,8,8);
                et.setBackgroundResource(android.R.drawable.edit_text);

                final int r = i;
                final int c = j;

                et.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        saveCell(r, c, s.toString());
                    }
                });

                cells[i][j] = et;
                row.addView(et);
            }

            table.addView(row);
        }
    }

    private TextView createHeaderCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(10, 10, 10, 10);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(null, Typeface.BOLD); // ✅ correct way
        return tv;
    }

    // ---------------- SAVE / LOAD ----------------
    private String key(int r, int c) {
        return getWeekKey() + "_" + r + "_" + c;
    }

    private String getWeekKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_ww", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private void saveCell(int r, int c, String value) {
        prefs.edit().putString(key(r,c), value).apply();
    }

    private void loadWeekData() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                String val = prefs.getString(key(i,j), "");
                if (cells[i][j] != null) {
                    cells[i][j].setText(val);
                }
            }
        }
    }

    // ---------------- CHECKBOX FEATURES ----------------

    private void copyFirstColumnToAll() {
        for (int i = 0; i < 5; i++) {
            String value = cells[i][0].getText().toString();
            for (int j = 1; j < 7; j++) {
                cells[i][j].setText(value);
            }
        }
    }

    private void loadLastWeek() {
        Calendar last = (Calendar) calendar.clone();
        last.add(Calendar.WEEK_OF_YEAR, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_ww", Locale.getDefault());
        String lastKeyBase = sdf.format(last.getTime());

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                String val = prefs.getString(lastKeyBase + "_" + i + "_" + j, "");
                cells[i][j].setText(val);
            }
        }
    }

    // ---------------- SPINNERS ----------------
    private void setupSpinners() {

        String[] months = new DateFormatSymbols().getMonths();
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                months);
        spMonth.setAdapter(monthAdapter);
        spMonth.setSelection(calendar.get(Calendar.MONTH));

        int year = calendar.get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        for (int i = year - 5; i <= year + 5; i++) years.add(String.valueOf(i));

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                years);
        spYear.setAdapter(yearAdapter);
        spYear.setSelection(5);
    }
}