package com.example.mealplannerapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mealplannerapp.data.AppDatabase;
import com.example.mealplannerapp.models.MealDay;

import java.util.List;

public class MealViewModel extends AndroidViewModel {

    private final LiveData<List<MealDay>> meals;

    public MealViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        meals = db.mealDao().getAllMeals();
    }

    public LiveData<List<MealDay>> getMeals() {
        return meals;
    }
}