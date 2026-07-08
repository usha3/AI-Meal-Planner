package com.example.mealplannerapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mealplannerapp.App;
import com.example.mealplannerapp.models.MealDay;
import com.example.mealplannerapp.models.MealIngredientEntity;
import com.example.mealplannerapp.database.MealIngredientDao;
import com.example.mealplannerapp.models.MealRecommendationEntity;
import com.example.mealplannerapp.database.MealRecommendationDao;
import com.example.mealplannerapp.data.MealDao;

import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mealplannerapp.models.MealJsonModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;
import com.example.mealplannerapp.models.MealRecommendationJsonModel;

@Database(
        entities = {
                MealDay.class,
                MealIngredientEntity.class,
                MealRecommendationEntity.class
        },
        version = 6
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    public abstract MealIngredientDao mealIngredientDao();
    public abstract MealDao mealDao();
    public abstract MealRecommendationDao mealRecommendationDao();

    public static synchronized AppDatabase getInstance(Context context) {

        if (instance == null) {

            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "meal_db"
                    )
                    .addCallback(roomCallback)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
    private static RoomDatabase.Callback roomCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onCreate(SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    Executors.newSingleThreadExecutor().execute(() -> {

                        // create temporary instance-safe call
                        if (App.context != null) {
                            AppDatabase tempDb =
                                    Room.databaseBuilder(
                                            App.context,
                                            AppDatabase.class,
                                            "meal_db"
                                    ).build();

                            loadMealsFromJson(tempDb);
                            loadRecommendedMeals(tempDb);
                        }
                    });
                }
            };
    private static void loadMealsFromJson(AppDatabase db) {

        try {

            InputStream is =
                    App.context.getAssets().open("ingredients.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            String json =
                    new String(buffer, StandardCharsets.UTF_8);

            Type type =
                    new TypeToken<List<MealJsonModel>>(){}.getType();

            List<MealJsonModel> meals =
                    new Gson().fromJson(json, type);

            MealIngredientDao dao = db.mealIngredientDao();

            for (MealJsonModel meal : meals) {

                for (String ingredient : meal.ingredients) {

                    dao.insert(new MealIngredientEntity(
                            meal.meal.trim().toLowerCase(),
                            ingredient.trim().toLowerCase()
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void loadRecommendedMeals(AppDatabase db) {

        try {

            InputStream is =
                    App.context.getAssets()
                            .open("recommended_meals.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            String json =
                    new String(buffer,
                            StandardCharsets.UTF_8);

            Type type =
                    new TypeToken<List<MealRecommendationJsonModel>>() {
                    }.getType();

            List<MealRecommendationJsonModel> meals =
                    new Gson().fromJson(json, type);

            MealRecommendationDao dao =
                    db.mealRecommendationDao();

            for (MealRecommendationJsonModel item : meals) {

                dao.insert(new MealRecommendationEntity(
                        item.mealName,
                        item.ingredient,
                        item.imageUrl,
                        item.cookingTime,
                        item.calories,
                        item.recipeLink
                ));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}