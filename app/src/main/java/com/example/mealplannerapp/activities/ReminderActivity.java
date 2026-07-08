package com.example.mealplannerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.adapters.ReminderAdapter;
import com.example.mealplannerapp.notifications.ReminderReceiver;

import java.util.ArrayList;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {

    EditText searchBox;
    Button btnPickTime;

    int selectedHour, selectedMinute;
    ListView listMeals, listFitness;

    ArrayList<String> mealsList = new ArrayList<>();
    ArrayList<String> fitnessList = new ArrayList<>();

    ReminderAdapter mealsAdapter;
    ReminderAdapter fitnessAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        searchBox = findViewById(R.id.searchBox);
        listMeals = findViewById(R.id.listMeals);
        listFitness = findViewById(R.id.listFitness);

        // SAMPLE DATA
        mealsList.add("Breakfast - Oats");
        mealsList.add("Lunch - Rice & Chicken");
        mealsList.add("Dinner - Salad");

        fitnessList.add("Morning Workout");
        fitnessList.add("Evening Yoga");

        // ADAPTERS
        mealsAdapter = new ReminderAdapter(this, mealsList);
        fitnessAdapter = new ReminderAdapter(this, fitnessList);

        listMeals.setAdapter(mealsAdapter);
        listFitness.setAdapter(fitnessAdapter);

        // 🔍 SEARCH FUNCTION
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filterList(s.toString());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
        ImageView btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v -> {
            Toast.makeText(this, "Add clicked", Toast.LENGTH_SHORT).show();

            // show input or add dialog
        });
        btnPickTime = findViewById(R.id.btnPickTime);

        btnPickTime.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            TimePickerDialog timePicker = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {

                        selectedHour = hourOfDay;
                        selectedMinute = minute;

                        Toast.makeText(this,
                                "Time selected: " + hourOfDay + ":" + minute,
                                Toast.LENGTH_SHORT).show();

                        // After selecting time, add reminder
                        addReminderWithNotification("Drink water / Meal / Workout");
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true);

            timePicker.show();
        });
    }

    // 🔍 FILTER BOTH LISTS
    private void filterList(String text) {

        ArrayList<String> filteredMeals = new ArrayList<>();
        ArrayList<String> filteredFitness = new ArrayList<>();

        for (String item : mealsList) {
            if (item.toLowerCase().contains(text.toLowerCase())) {
                filteredMeals.add(item);
            }
        }

        for (String item : fitnessList) {
            if (item.toLowerCase().contains(text.toLowerCase())) {
                filteredFitness.add(item);
            }
        }

        listMeals.setAdapter(new ReminderAdapter(this, filteredMeals));
        listFitness.setAdapter(new ReminderAdapter(this, filteredFitness));
    }
    private boolean canScheduleExactAlarm() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            return alarmManager.canScheduleExactAlarms();
        }
        return true;
    }
    private void addReminderWithNotification(String text) {

        if (!canScheduleExactAlarm()) {
            Toast.makeText(this,
                    "Please allow exact alarms in settings",
                    Toast.LENGTH_LONG).show();

            Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("title", text);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }

        Toast.makeText(this, "Reminder Set!", Toast.LENGTH_SHORT).show();
    }
}
