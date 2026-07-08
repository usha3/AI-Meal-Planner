package com.example.mealplannerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.adapters.WeekAdapter;
import com.example.mealplannerapp.data.AppDatabase;
import com.example.mealplannerapp.models.MealIngredientEntity;
import com.example.mealplannerapp.models.WeekDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import com.example.mealplannerapp.models.MealDay;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerMonth, spinnerYear;
    RecyclerView weekRecycler, mealRecycler;
    WeekAdapter weekAdapter;
    SimpleTextAdapter mealAdapter;
    List<String> mealList = new ArrayList<>();
    private WeekDay selectedDay;
    LinearLayout suggestionContainer;
    Animation pulse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ================= VIEWS =================
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        weekRecycler = findViewById(R.id.weekRecycler);
        mealRecycler = findViewById(R.id.mealRecycler);
        suggestionContainer = findViewById(R.id.suggestionContainer);
        suggestionContainer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AiRecipesActivity.class);
            startActivity(intent);
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Meal Planner");
        }
        pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        suggestionContainer.startAnimation(pulse);
        setupSpinners();
        setupWeekRecycler();
        setupMealRecycler();
        setupBottomNavigation();
        preloadIngredients();
    }

    private List<WeekDay> generateMonthDays(int month, int year) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);

        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        List<WeekDay> list = new ArrayList<>();

        for (int i = 1; i <= maxDays; i++) {

            Calendar dayCal = (Calendar) cal.clone();
            dayCal.set(Calendar.DAY_OF_MONTH, i);

            String dayName = new SimpleDateFormat("EEE", Locale.getDefault())
                    .format(dayCal.getTime());

            boolean isToday =
                    (i == today && month == currentMonth && year == currentYear);

            String fullDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(dayCal.getTime());

            list.add(new WeekDay(
                    dayName,
                    i,
                    isToday,
                    fullDate
            ));
        }
        return list;
    }
    // ================= SPINNERS =================
    private void setupSpinners() {

        List<String> months = Arrays.asList(
                "January","February","March","April","May","June",
                "July","August","September","October","November","December"
        );

        ArrayAdapter<String> monthAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        spinnerMonth.setAdapter(monthAdapter);

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        spinnerMonth.setSelection(currentMonth);

        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            years.add(String.valueOf(i));
        }

        ArrayAdapter<String> yearAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        spinnerYear.setAdapter(yearAdapter);

        spinnerYear.setSelection(5);

        AdapterView.OnItemSelectedListener listener =
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        loadWeek();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                };

        spinnerMonth.setOnItemSelectedListener(listener);
        spinnerYear.setOnItemSelectedListener(listener);
    }

    // ================= WEEK RECYCLER =================
    private void setupWeekRecycler() {

        weekRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        SnapHelper snapHelper = new androidx.recyclerview.widget.LinearSnapHelper();
        snapHelper.attachToRecyclerView(weekRecycler);
    }

    // REAL MONTH BASED WEEK (FIXED)
    private void loadWeek() {

        int month = spinnerMonth.getSelectedItemPosition();
        int year = Integer.parseInt(spinnerYear.getSelectedItem().toString());

        List<WeekDay> days = generateMonthDays(month, year);

        weekAdapter = new WeekAdapter(days, day -> {
            selectedDay = day;
            loadMealsForDay(day);
        });

        weekRecycler.setAdapter(weekAdapter);

        // SCROLL TO TODAY
        int todayPosition = -1;

        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).isToday()) {
                todayPosition = i;
                break;
            }
        }

        if (todayPosition != -1) {
            weekAdapter.setSelectedPosition(todayPosition);
            selectedDay = days.get(todayPosition);
            loadMealsForDay(selectedDay);
            final int finalTodayPosition = todayPosition;
            weekRecycler.post(() -> {

                LinearLayoutManager layoutManager =
                        (LinearLayoutManager) weekRecycler.getLayoutManager();

                View child = layoutManager.findViewByPosition(finalTodayPosition);

                int itemWidth = 150; // approximate item width

                int offset = (weekRecycler.getWidth() / 2) - (itemWidth / 2);

                layoutManager.scrollToPositionWithOffset(
                        finalTodayPosition,
                        offset
                );
            });
        }
        // ALWAYS load today if nothing selected
        if (selectedDay == null && !days.isEmpty()) {
            selectedDay = days.get(0);
            loadMealsForDay(selectedDay);
        }
    }
    private void setupMealRecycler() {

        mealRecycler.setLayoutManager(new LinearLayoutManager(this));

        mealAdapter = new SimpleTextAdapter(mealList);
        mealRecycler.setAdapter(mealAdapter);
    }

    private void loadMealsForDay(WeekDay day) {
        getTodayMeals(day);
    }
    private void setupBottomNavigation() {

        BottomNavigationView bottomNav =
                findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_plan);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_plan) {
                startActivity(new Intent(MainActivity.this, CustomizeMealActivity.class));
                return true;
            } else if (id == R.id.nav_grocery) {
                startActivity(new Intent(MainActivity.this, GroceryActivity.class
                ));
                return true;
            } else if (id == R.id.nav_scan) {
                startActivity(new Intent(MainActivity.this, ScanActivity.class
                ));
                return true;
            } else if (id == R.id.nav_recipes) {
                startActivity(new Intent(MainActivity.this, RecipesActivity.class
                ));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class
                ));
                return true;
            }
            return false;
        });
    }

    // ================= SIMPLE ADAPTER =================
    public static class SimpleTextAdapter extends RecyclerView.Adapter<SimpleTextAdapter.ViewHolder> {

        List<String> list;

        public SimpleTextAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_meal, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            String item = list.get(position);

            String left = "";
            String right = "";

            if (item.contains("-")) {
                String[] parts = item.split("-", 2);
                left = parts[0].trim();
                right = parts[1].trim();
            }

            holder.left.setText(left);
            holder.right.setText(right);
            holder.dash.setText("-");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            TextView left, dash, right;

            public ViewHolder(View itemView) {
                super(itemView);

                left = itemView.findViewById(R.id.txtLeft);
                dash = itemView.findViewById(R.id.txtDash);
                right = itemView.findViewById(R.id.txtRight);
            }
        }
    }
    private void getTodayMeals(WeekDay day) {

        AppDatabase db = AppDatabase.getInstance(this);

        new Thread(() -> {

            MealDay mealDay =
                    db.mealDao().getMealByDate(day.getFullDate());

            runOnUiThread(() -> {

                mealList.clear();

                if (mealDay == null) {

                    mealList.add("No meals planned for " + day.getDayName());

                } else {

                    if (mealDay.getBreakfast() != null && !mealDay.getBreakfast().isEmpty())
                        mealList.add("Breakfast - " + mealDay.getBreakfast());

                    if (mealDay.getMorningSnack() != null && !mealDay.getMorningSnack().isEmpty())
                        mealList.add("Morning Snack - " + mealDay.getMorningSnack());

                    if (mealDay.getLunch() != null && !mealDay.getLunch().isEmpty())
                        mealList.add("Lunch - " + mealDay.getLunch());

                    if (mealDay.getEveningSnack() != null && !mealDay.getEveningSnack().isEmpty())
                        mealList.add("Evening Snack - " + mealDay.getEveningSnack());

                    if (mealDay.getDinner() != null && !mealDay.getDinner().isEmpty())
                        mealList.add("Dinner - " + mealDay.getDinner());
                }

                mealAdapter.notifyDataSetChanged();
            });

        }).start();
    }
    private void preloadIngredients() {

        new Thread(() -> {

            AppDatabase db =
                    AppDatabase.getInstance(getApplicationContext());

            if (db.mealIngredientDao().getCount() == 0) {

                // Egg Curry
                db.mealIngredientDao().insert(
                        new MealIngredientEntity("Egg Curry", "Egg"));

                db.mealIngredientDao().insert(
                        new MealIngredientEntity("Egg Curry", "Onion"));

                db.mealIngredientDao().insert(
                        new MealIngredientEntity("Egg Curry", "Tomato"));

                db.mealIngredientDao().insert(
                        new MealIngredientEntity("Egg Curry", "Spices"));

                // Chicken Curry
                db.mealIngredientDao().insert(
                        new MealIngredientEntity("Chicken Curry", "Chicken"));

                db.mealIngredientDao().insert(
                        new MealIngredientEntity("Chicken Curry", "Onion"));

                db.mealIngredientDao().insert(
                        new MealIngredientEntity("Chicken Curry", "Tomato"));

                db.mealIngredientDao().insert(
                        new MealIngredientEntity("Chicken Curry", "Spices"));
            }

        }).start();
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (selectedDay != null) {
            loadMealsForDay(selectedDay);
        }
    }
}