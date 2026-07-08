package com.example.mealplannerapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.data.AppDatabase;
import com.example.mealplannerapp.models.MealDay;

import java.text.SimpleDateFormat;
import java.util.*;

public class CustomizeMealActivity extends AppCompatActivity {

    private Button btnPrevWeek, btnNextWeek;
    private Spinner spinnerMonth, spinnerYear;
    private TableLayout tableMeals;
    private CheckBox cbSameEveryDay, cbSameLastWeek;

    private EditText[][] cells = new EditText[5][7];
    private Calendar currentWeek = Calendar.getInstance();

    private List<Calendar> weekDays = new ArrayList<>();

    private Handler handler = new Handler(android.os.Looper.getMainLooper());
    private Runnable saveRunnable;

    private boolean isUpdating = false;
    private boolean[][] changedCells = new boolean[5][7];

    private List<String> years;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_meal);

        initViews();
        setupSpinners();
        initTableCells();
        setupClickListeners();

        loadWeek();
    }

    // ---------------- INIT ----------------
    private void initViews() {

        btnPrevWeek = findViewById(R.id.btnPrevWeek);
        btnNextWeek = findViewById(R.id.btnNextWeek);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);

        tableMeals = findViewById(R.id.tableMeals);

        cbSameEveryDay = findViewById(R.id.cbSameEveryDay);
        cbSameLastWeek = findViewById(R.id.cbSameLastWeek);
    }

    // ---------------- SPINNERS ----------------
    private void setupSpinners() {

        List<String> months = Arrays.asList(
                "January","February","March","April","May","June",
                "July","August","September","October","November","December"
        );

        spinnerMonth.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, months));

        years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            years.add(String.valueOf(i));
        }

        spinnerYear.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, years));
    }

    // ---------------- TABLE ----------------
    private void initTableCells() {

        int rowCount = tableMeals.getChildCount();
        int mealRow = 0;

        for (int i = 1; i < rowCount && mealRow < 5; i++) {

            View rowView = tableMeals.getChildAt(i);
            if (!(rowView instanceof TableRow)) continue;

            TableRow row = (TableRow) rowView;

            int colIndex = 0;

            for (int j = 0; j < row.getChildCount() && colIndex < 7; j++) {

                View v = row.getChildAt(j);
                if (!(v instanceof EditText)) continue;

                EditText cell = (EditText) v;

                cells[mealRow][colIndex] = cell;

                int r = mealRow;
                int c = colIndex;

                cell.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
                    @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}

                    @Override
                    public void afterTextChanged(Editable s) {

                        if (isUpdating) return;

                        changedCells[r][c] = true;
                        delayedSave();
                    }
                });

                colIndex++;
            }
            mealRow++;
        }
    }

    // ---------------- WEEK GENERATION ----------------
    private void generateWeek() {

        weekDays.clear();

        Calendar start = (Calendar) currentWeek.clone();

        // Move to Monday properly
        start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        for (int i = 0; i < 7; i++) {
            Calendar day = (Calendar) start.clone();
            day.add(Calendar.DAY_OF_MONTH, i);
            weekDays.add(day);
        }
    }

    // ---------------- LOAD WEEK ----------------
    private void loadWeek() {
        Log.d("DB_TEST", "Loading week...");
        clearChangeFlags();

        generateWeek();
        updateWeekHeaderDates();

        AppDatabase db = AppDatabase.getInstance(this);

        new Thread(() -> {

            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            MealDay[] meals = new MealDay[7];

            for (int i = 0; i < 7; i++) {

                String dateKey = sdf.format(weekDays.get(i).getTime());
                meals[i] = db.mealDao().getMealByDate(dateKey);
                Log.d("DB_TEST", "dateKey = " + dateKey);
                Log.d("DB_TEST", "meal = " + (meals[i] != null));
            }

            runOnUiThread(() -> {

                isUpdating = true;

                for (int col = 0; col < 7; col++) {

                    MealDay m = meals[col];

                    setCellSafe(0, col, m != null ? m.getBreakfast() : "");
                    setCellSafe(1, col, m != null ? m.getMorningSnack() : "");
                    setCellSafe(2, col, m != null ? m.getLunch() : "");
                    setCellSafe(3, col, m != null ? m.getEveningSnack() : "");
                    setCellSafe(4, col, m != null ? m.getDinner() : "");
                }

                isUpdating = false;
                updateWeekDisplay();
            });

        }).start();
    }

    private void clearChangeFlags() {
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 7; c++) {
                changedCells[r][c] = false;
            }
        }
    }

    private void setCellSafe(int row, int col, String value) {
        Log.d("UI_TEST", "row=" + row + " col=" + col +
                " cellNull=" + (cells[row][col] == null));
        if (cells[row][col] != null) {
            cells[row][col].setText(value == null ? "" : value);
        }
    }

    // ---------------- HEADER ----------------
    private void updateWeekHeaderDates() {

        TableRow headerRow = (TableRow) tableMeals.getChildAt(0);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());

        for (int i = 0; i < 7; i++) {

            TextView view = (TextView) headerRow.getChildAt(i);

            Calendar day = weekDays.get(i);

            view.setText(dayFormat.format(day.getTime())
                    + "\n" + dateFormat.format(day.getTime()));
        }
    }

    // ---------------- SAVE ----------------
    private void saveWeekData() {

        AppDatabase db = AppDatabase.getInstance(this);

        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        new Thread(() -> {

            for (int col = 0; col < 7; col++) {

                Calendar day = weekDays.get(col);
                String dateKey = sdf.format(day.getTime());

                MealDay meal = new MealDay(
                        "",
                        getCellText(0, col),
                        getCellText(1, col),
                        getCellText(2, col),
                        getCellText(3, col),
                        getCellText(4, col),
                        dateKey
                );

                db.mealDao().upsert(meal);
                Log.d("DB_SAVE", "Saved date: " + dateKey +
                        " | breakfast=" + meal.getBreakfast());
                for (int r = 0; r < 5; r++) {
                    changedCells[r][col] = false;
                }
            }

        }).start();
    }

    private String getCellText(int row, int col) {
        return cells[row][col] != null
                ? cells[row][col].getText().toString().trim()
                : "";
    }

    // ---------------- BUTTONS ----------------
    private void setupClickListeners() {

        btnPrevWeek.setOnClickListener(v -> {
            saveWeekData();
            handler.postDelayed(() -> {
                currentWeek.add(Calendar.WEEK_OF_YEAR, -1);
                loadWeek();
            }, 200);
        });

        btnNextWeek.setOnClickListener(v -> {
            saveWeekData();
            handler.postDelayed(() -> {
                currentWeek.add(Calendar.WEEK_OF_YEAR, 1);
                loadWeek();
            }, 200);
        });
        cbSameEveryDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                applySameEveryDay();
            }
        });
        cbSameLastWeek.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                applyLastWeekMeals();
            }
        });
    }
    private void applySameEveryDay() {

        String[] dayMeals = new String[5];

        for (int r = 0; r < 5; r++) {
            dayMeals[r] = getCellText(r, 0); // take Monday as base
        }

        for (int c = 0; c < 7; c++) {
            for (int r = 0; r < 5; r++) {
                setCellSafe(r, c, dayMeals[r]);
            }
        }

        cbSameEveryDay.setChecked(false);
    }
    private void applyLastWeekMeals() {

        AppDatabase db = AppDatabase.getInstance(this);

        new Thread(() -> {

            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            Calendar start = (Calendar) currentWeek.clone();
            start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            start.add(Calendar.WEEK_OF_YEAR, -1); // 👈 LAST WEEK

            MealDay[] lastWeekMeals = new MealDay[7];

            for (int i = 0; i < 7; i++) {

                Calendar day = (Calendar) start.clone();
                day.add(Calendar.DAY_OF_MONTH, i);

                String dateKey = sdf.format(day.getTime());

                lastWeekMeals[i] = db.mealDao().getMealByDate(dateKey);
            }

            runOnUiThread(() -> {

                isUpdating = true;

                for (int col = 0; col < 7; col++) {

                    MealDay m = lastWeekMeals[col];

                    setCellSafe(0, col, m != null ? m.getBreakfast() : "");
                    setCellSafe(1, col, m != null ? m.getMorningSnack() : "");
                    setCellSafe(2, col, m != null ? m.getLunch() : "");
                    setCellSafe(3, col, m != null ? m.getEveningSnack() : "");
                    setCellSafe(4, col, m != null ? m.getDinner() : "");
                }

                isUpdating = false;

                cbSameLastWeek.setChecked(false);
            });

        }).start();
    }

    // ---------------- AUTO SAVE ----------------
    private void delayedSave() {

        if (saveRunnable != null) {
            handler.removeCallbacks(saveRunnable);
        }

        saveRunnable = this::saveWeekData;
        handler.postDelayed(saveRunnable, 800);
    }

    private void updateWeekDisplay() {

        int month = currentWeek.get(Calendar.MONTH);
        int year = currentWeek.get(Calendar.YEAR);

        spinnerMonth.setSelection(month);

        int index = years.indexOf(String.valueOf(year));
        if (index < 0) index = 0;

        spinnerYear.setSelection(index);
    }
}