package com.example.mealplannerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mealplannerapp.models.MealDay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MealStorage {

    private static final String PREF_NAME = "meal_planner";
    private static final String KEY_DATA = "week_meals";

    // ---------------- SAVE ----------------
    public static void saveMeals(Context context, List<MealDay> list) {

        try {
            JSONArray array = new JSONArray();

            for (MealDay d : list) {

                JSONObject obj = new JSONObject();

                obj.put("day", safe(d.getDay()));
                obj.put("breakfast", safe(d.getBreakfast()));
                obj.put("lunch", safe(d.getLunch()));
                obj.put("dinner", safe(d.getDinner()));
                obj.put("date", safe(d.getDate())); // ✅ IMPORTANT

                array.put(obj);
            }

            SharedPreferences prefs =
                    context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            prefs.edit()
                    .putString(KEY_DATA, array.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- LOAD ----------------
    public static List<MealDay> loadMeals(Context context) {

        List<MealDay> list = new ArrayList<>();

        try {

            SharedPreferences prefs =
                    context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            String json = prefs.getString(KEY_DATA, null);

            if (json == null || json.isEmpty()) {
                return list;
            }

            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                MealDay d = new MealDay(
                        obj.optString("day", ""),
                        obj.optString("breakfast", ""),
                        obj.optString("Morning Snack", ""),
                        obj.optString("lunch", ""),
                        obj.optString("Evening Snack", ""),
                        obj.optString("dinner", ""),
                        obj.optString("date", "") // ✅ IMPORTANT
                );

                list.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ---------------- SAFE HELPER ----------------
    private static String safe(String value) {
        return value == null ? "" : value;
    }
}