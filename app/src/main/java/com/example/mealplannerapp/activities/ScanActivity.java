package com.example.mealplannerapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.adapters.RecommendedMealAdapter;
import com.example.mealplannerapp.data.AppDatabase;
import com.example.mealplannerapp.models.MealRecommendationEntity;
import com.example.mealplannerapp.models.RecommendedMeal;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    Button cameraBtn, uploadBtn;

    RecyclerView recyclerView;

    RecommendedMealAdapter adapter;

    List<RecommendedMeal> mealList = new ArrayList<>();

    ActivityResultLauncher<Intent> cameraLauncher;

    ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        cameraBtn = findViewById(R.id.cameraBtn);
        uploadBtn = findViewById(R.id.uploadBtn);

        recyclerView = findViewById(R.id.recommendRecycler);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));

        adapter = new RecommendedMealAdapter(mealList);

        recyclerView.setAdapter(adapter);

        // CAMERA RESULT
        cameraLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {

                            if (result.getResultCode() == RESULT_OK
                                    && result.getData() != null) {

                                Bitmap bitmap =
                                        (Bitmap) result.getData()
                                                .getExtras()
                                                .get("data");

                                scanImage(bitmap);
                            }
                        });

        // GALLERY RESULT
        galleryLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {

                            if (result.getResultCode() == RESULT_OK
                                    && result.getData() != null) {

                                Uri imageUri =
                                        result.getData().getData();

                                try {

                                    Bitmap bitmap =
                                            MediaStore.Images.Media.getBitmap(
                                                    getContentResolver(),
                                                    imageUri
                                            );

                                    scanImage(bitmap);

                                } catch (IOException e) {

                                    e.printStackTrace();
                                }
                            }
                        });

        cameraBtn.setOnClickListener(v -> {

            checkCameraPermission();
        });

        uploadBtn.setOnClickListener(v -> {

            openGallery();
        });
    }

    // ================= CAMERA =================

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    101);

        } else {

            openCamera();
        }
    }

    private void openCamera() {

        Intent intent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        cameraLauncher.launch(intent);
    }

    // ================= GALLERY =================

    private void openGallery() {

        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        galleryLauncher.launch(intent);
    }

    // ================= SCAN IMAGE =================

    private void scanImage(Bitmap bitmap) {

        InputImage image =
                InputImage.fromBitmap(bitmap, 0);

        ImageLabeling.getClient(
                        ImageLabelerOptions.DEFAULT_OPTIONS)

                .process(image)

                .addOnSuccessListener(labels -> {

                    String ingredient = "";

                    for (ImageLabel label : labels) {

                        ingredient =
                                label.getText().toLowerCase();

                        Toast.makeText(
                                this,
                                "Detected: " + ingredient,
                                Toast.LENGTH_LONG
                        ).show();

                        break;
                    }

                    recommendMeals(ingredient);
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Scan Failed",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // ================= RECOMMEND MEALS =================

    private void recommendMeals(String ingredient) {

        new Thread(() -> {

            AppDatabase db =
                    AppDatabase.getInstance(getApplicationContext());

            List<MealRecommendationEntity> meals =
                    db.mealRecommendationDao()
                            .getMealsByIngredient(ingredient);

            runOnUiThread(() -> {

                mealList.clear();

                for (MealRecommendationEntity item : meals) {

                    mealList.add(new RecommendedMeal(
                            item.mealName,
                            item.cookingTime,
                            item.calories,
                            item.recipeLink,
                            item.imageUrl
                    ));
                }

                adapter.notifyDataSetChanged();

                if (mealList.isEmpty()) {

                    Toast.makeText(
                            this,
                            "No meals found",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

        }).start();
    }

    // ================= CAMERA PERMISSION =================

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults);

        if (requestCode == 101
                && grantResults.length > 0
                && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {

            openCamera();
        }
    }
}