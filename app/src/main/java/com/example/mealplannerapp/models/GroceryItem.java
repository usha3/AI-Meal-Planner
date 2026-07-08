package com.example.mealplannerapp.models;

import java.util.Locale;

public class GroceryItem {
    public String name;
    public int quantity;
    public boolean purchased;

    public GroceryItem(String name, int quantity) {
        this.name = clean(name);
        this.quantity = quantity;
        this.purchased = false;
    }

    private String clean(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}