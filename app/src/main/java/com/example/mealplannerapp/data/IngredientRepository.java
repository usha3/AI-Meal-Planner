package com.example.mealplannerapp.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class IngredientRepository {

    public static List<String> getIngredients(
            Context context,
            String mealName) {

        List<String> list = new ArrayList<>();

        try {

            InputStream is =
                    context.getAssets()
                            .open("ingredients.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            String json =
                    new String(buffer, "UTF-8");

            JSONArray mealsArray =
                    new JSONArray(json);

            for (int i = 0;
                 i < mealsArray.length();
                 i++) {

                JSONObject mealObject =
                        mealsArray.getJSONObject(i);

                String jsonMeal =
                        mealObject.getString("meal");

                if (jsonMeal.equalsIgnoreCase(
                        mealName.trim())) {

                    JSONArray ingredientsArray =
                            mealObject.getJSONArray(
                                    "ingredients");

                    for (int j = 0;
                         j < ingredientsArray.length();
                         j++) {

                        list.add(
                                ingredientsArray
                                        .getString(j)
                        );
                    }

                    break;
                }
            }

            Log.d("JSON_DEBUG",
                    mealName + " -> " + list);

        } catch (Exception e) {

            Log.e("JSON_ERROR",
                    e.toString());
        }

        return list;
    }
}