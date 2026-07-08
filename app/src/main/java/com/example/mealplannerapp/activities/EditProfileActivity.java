package com.example.mealplannerapp.activities;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.mealplannerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    ImageView profileImage;
    EditText nameInput, emailInput;
    Button changeImageBtn, saveBtn;
    FrameLayout avatarContainer;
    Uri imageUri;
    TextView initialText;
    Button noImageBtn;
    String selectedImageUrl = null;
    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImage = findViewById(R.id.profileImage);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        changeImageBtn = findViewById(R.id.changeImageBtn);
        saveBtn = findViewById(R.id.saveBtn);
        noImageBtn = findViewById(R.id.noImageBtn);
        initialText = findViewById(R.id.initialText);
        avatarContainer = findViewById(R.id.avatarContainer);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        loadUserData();

        changeImageBtn.setOnClickListener(v -> pickImage());
        noImageBtn.setOnClickListener(v -> {

            imageUri = null;
            selectedImageUrl = "";

            profileImage.setVisibility(View.GONE);
            initialText.setVisibility(View.VISIBLE);

            String name = nameInput.getText().toString().trim();

            if (!name.isEmpty()) {
                initialText.setText(
                        String.valueOf(name.charAt(0)).toUpperCase()
                );
            } else {
                initialText.setText("U");
            }

            GradientDrawable bg =
                    (GradientDrawable) initialText.getBackground();

            bg.setColor(getAvatarColor(name));
        });

        saveBtn.setOnClickListener(v -> uploadData());
    }
    private void loadUserUI(String name, String imageUrl) {

        if (imageUrl != null && !imageUrl.isEmpty()) {

            profileImage.setVisibility(View.VISIBLE);
            initialText.setVisibility(View.GONE);

            Glide.with(this)
                    .load(imageUrl)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(profileImage);

        } else {

            profileImage.setVisibility(View.GONE);
            initialText.setVisibility(View.VISIBLE);

            String initial = name != null && !name.isEmpty()
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
    // ---------------- PICK IMAGE ----------------
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();

            profileImage.setVisibility(View.VISIBLE);
            initialText.setVisibility(View.GONE);

            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(profileImage);
        }
    }

    // ---------------- LOAD DATA ----------------
    private void loadUserData() {

        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {

                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        String imageUrl = doc.getString("imageUrl");

                        nameInput.setText(name);
                        emailInput.setText(email);

                        loadUserUI(name, imageUrl); // ⭐ FIX HERE
                    }
                });
    }

    // ---------------- UPLOAD DATA ----------------
    private void uploadData() {

        String uid = auth.getCurrentUser().getUid();

        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();

        if (imageUri != null) {
            uploadImage(uid, name, email);
        } else {
            saveToFirestore(uid, name, email, "");
        }
    }

    // ---------------- UPLOAD IMAGE TO FIREBASE STORAGE ----------------
    private void uploadImage(String uid, String name, String email) {

        StorageReference ref = FirebaseStorage.getInstance()
                .getReference()
                .child("profile_images/" + uid + ".jpg");

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {

                            String imageUrl = uri.toString();

                            saveToFirestore(uid, name, email, imageUrl);

                        })
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // ---------------- SAVE TO FIRESTORE ----------------
    private void saveToFirestore(String uid, String name, String email, String imageUrl) {

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        if (imageUrl != null) {
            user.put("imageUrl", imageUrl);
        }

        db.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}