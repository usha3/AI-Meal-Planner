package com.example.mealplannerapp.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealApiService {

    @GET("search.php")
    Call<MealResponse> searchMeals(@Query("s") String name);

    @GET("filter.php")
    Call<MealResponse> filterByCategory(@Query("c") String category);
}