package com.example.mealplannerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mealplannerapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout editProfile, changePassword, notifications, theme, aboutApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editProfile = findViewById(R.id.editProfile);
        changePassword = findViewById(R.id.changePassword);
        notifications = findViewById(R.id.notifications);
        theme = findViewById(R.id.theme);
        aboutApp = findViewById(R.id.aboutApp);

        // 🔹 Edit Profile
        editProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class))
        );

        // 🔹 Change Password
        changePassword.setOnClickListener(v -> {

            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this,
                                    "Password reset email sent",
                                    Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this,
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT).show()
                    );
        });

        // 🔹 Notifications
        notifications.setOnClickListener(v -> {

            boolean current = getSharedPreferences("settings", MODE_PRIVATE)
                    .getBoolean("notifications", true);

            boolean newValue = !current;

            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("notifications", newValue)
                    .apply();

            Toast.makeText(this,
                    newValue ? "Notifications ON" : "Notifications OFF",
                    Toast.LENGTH_SHORT).show();
        });

        // 🔹 Dark Mode (future upgrade)
        theme.setOnClickListener(v -> {

            boolean dark = getSharedPreferences("settings", MODE_PRIVATE)
                    .getBoolean("dark_mode", false);

            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("dark_mode", !dark)
                    .apply();

            Toast.makeText(this,
                    !dark ? "Dark Mode Enabled" : "Light Mode Enabled",
                    Toast.LENGTH_SHORT).show();

            recreate(); // reload activity
        });

        // 🔹 About App
        aboutApp.setOnClickListener(v -> {

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Meal Planner App")
                    .setMessage("Version 1.0\nDeveloped by Usha\n\nA recipe & meal planning app.")
                    .setPositiveButton("OK", null)
                    .show();
        });
    }
}