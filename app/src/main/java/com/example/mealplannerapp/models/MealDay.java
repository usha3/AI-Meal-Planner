package com.example.mealplannerapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "meal_day",
        indices = {@Index(value = {"date"}, unique = true)}
)
public class MealDay {
    @PrimaryKey
    @NonNull
    private String date;
    private String day;
    private String breakfast;
    private String morningSnack;
    private String lunch;
    private String eveningSnack;
    private String dinner;
    public MealDay() {
    }
    public MealDay(String day,
                   String breakfast,
                   String morningSnack,
                   String lunch,
                   String eveningSnack,
                   String dinner,
                   String date) {

        this.day = day;
        this.breakfast = breakfast;
        this.morningSnack = morningSnack;
        this.lunch = lunch;
        this.eveningSnack = eveningSnack;
        this.dinner = dinner;
        this.date = date;
    }

    public String getDay() { return day; }
    public String getBreakfast() { return breakfast; }
    public String getMorningSnack() { return morningSnack; }
    public String getLunch() { return lunch; }
    public String getEveningSnack() { return eveningSnack; }
    public String getDinner() { return dinner; }
    public String getDate() { return date; }

    public void setBreakfast(String breakfast) { this.breakfast = breakfast; }
    public void setMorningSnack(String morningSnack) { this.morningSnack = morningSnack; }
    public void setLunch(String lunch) { this.lunch = lunch; }
    public void setEveningSnack(String eveningSnack) { this.eveningSnack = eveningSnack; }
    public void setDinner(String dinner) { this.dinner = dinner; }

    public void setDate(String today) {
    }

    public void setDay(String day) {
        this.day = day;
    }
}