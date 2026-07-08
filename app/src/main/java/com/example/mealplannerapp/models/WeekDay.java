package com.example.mealplannerapp.models;

public class WeekDay {
    String dayName;
    int dayNumber;
    boolean isToday;
    String fullDate;

    public WeekDay(String dayName, int dayNumber, boolean isToday, String fullDate) {
        this.dayName = dayName;
        this.dayNumber = dayNumber;
        this.isToday = isToday;
        this.fullDate = fullDate;
    }

    public String getDayName() { return dayName; }
    public int getDayNumber() { return dayNumber; }
    public String getFullDate() { return fullDate; }
    public boolean isToday() {
        return isToday;
    }
}