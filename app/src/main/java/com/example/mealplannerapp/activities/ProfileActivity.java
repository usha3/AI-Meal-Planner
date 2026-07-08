package com.example.mealplannerapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mealplannerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    LinearLayout favoritesBtn, settingsBtn, logoutBtn;
    private ImageView profileImage;
    private TextView initialText;
    private TextView userName;
    private TextView userEmail;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FrameLayout avatarContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profileImage);
        initialText = findViewById(R.id.initialText);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        avatarContainer = findViewById(R.id.avatarContainer);

        favoritesBtn = findViewById(R.id.favoritesBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        // 🔹 Load user data (SharedPreferences example)
        SharedPreferences prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);

        String name = prefs.getString("name", "User Name");
        String email = prefs.getString("email", "user@email.com");

        userName.setText(name);
        userEmail.setText(email);

        // 🔹 Favorites
        favoritesBtn.setOnClickListener(v ->
                startActivity(new Intent(this, FavoritesActivity.class))
        );

        // 🔹 Settings
        settingsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class))
        );

        // 🔹 Logout
        logoutBtn.setOnClickListener(v -> {

            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });
        avatarContainer.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this,
                    EditProfileActivity.class);
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }
    private void loadProfile() {

        String uid = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    String name = doc.getString("name");
                    String email = doc.getString("email");
                    String imageUrl = doc.getString("imageUrl");

                    userName.setText(name);
                    userEmail.setText(email);

                    updateAvatar(name, imageUrl);
                });
    }
    private void updateAvatar(String name, String imageUrl) {

        if (imageUrl != null && !imageUrl.isEmpty()) {

            profileImage.setVisibility(View.VISIBLE);
            initialText.setVisibility(View.GONE);

            Glide.with(this)
                    .load(imageUrl)
                    .circleCrop()
                    .into(profileImage);

        } else {

            profileImage.setVisibility(View.GONE);
            initialText.setVisibility(View.VISIBLE);

            String initial =
                    (name != null && !name.isEmpty())
                            ? String.valueOf(name.charAt(0)).toUpperCase()
                            : "U";

            initialText.setText(initial);

            GradientDrawable bg =
                    (GradientDrawable) initialText.getBackground();

            bg.setColor(getAvatarColor(name));
        }
    }
    private int getAvatarColor(String name) {

        int[] colors = {
                0xFFE57373,
                0xFF64B5F6,
                0xFF81C784,
                0xFFFFB74D,
                0xFFBA68C8,
                0xFF26A69A,
                0xFF5C6BC0
        };

        if (name == null || name.isEmpty()) {
            return colors[0];
        }

        return colors[Math.abs(name.hashCode()) % colors.length];
    }
}