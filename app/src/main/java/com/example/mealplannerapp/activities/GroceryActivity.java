package com.example.mealplannerapp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.adapters.GroceryAdapter;
import com.example.mealplannerapp.data.AppDatabase;
import com.example.mealplannerapp.data.IngredientRepository;
import com.example.mealplannerapp.models.GroceryItem;
import com.example.mealplannerapp.models.MealDay;
import com.example.mealplannerapp.models.MealIngredientEntity;
import com.example.mealplannerapp.viewmodel.MealViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroceryActivity extends AppCompatActivity {

    RecyclerView groceryRecycler, purchasedRecycler;
    ImageView addBtn, deleteAllBtn;

    GroceryAdapter groceryAdapter;
    GroceryAdapter purchasedAdapter;

    List<GroceryItem> groceryList = new ArrayList<>();
    List<GroceryItem> purchasedList = new ArrayList<>();

    MealViewModel viewModel;
    TextView autoBtn;
    private AppDatabase db;
    private boolean isDeletedAll = false;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);

        groceryRecycler = findViewById(R.id.groceryRecycler);
        purchasedRecycler = findViewById(R.id.purchasedRecycler);
        addBtn = findViewById(R.id.addBtn);
        deleteAllBtn = findViewById(R.id.deleteAllBtn);
        autoBtn = findViewById(R.id.autoBtn);

        db = AppDatabase.getInstance(getApplicationContext());

        executorService.execute(() -> {
            List<MealIngredientEntity> list =
                    db.mealIngredientDao().getAll();

            Log.d("DB_DEBUG", list.toString());
        });
        // ---------------- ADAPTERS ----------------
        groceryAdapter = new GroceryAdapter(groceryList, false, new GroceryAdapter.OnItemActionListener() {
            @Override
            public void onDelete(int position) {
                GroceryItem removed = groceryList.remove(position);
                groceryAdapter.notifyItemRemoved(position);

                Snackbar.make(findViewById(android.R.id.content),
                                "Item deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", v -> {
                            groceryList.add(position, removed);
                            groceryAdapter.notifyItemInserted(position);
                        })
                        .show();
            }

            @Override
            public void onIncrease(int position) {
                groceryList.get(position).quantity++;
                groceryAdapter.notifyItemChanged(position);
            }

            @Override
            public void onDecrease(int position) {
                if (groceryList.get(position).quantity > 1) {
                    groceryList.get(position).quantity--;
                    groceryAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onMoveToPurchased(int position) {
                GroceryItem item = groceryList.remove(position);
                purchasedList.add(item);

                groceryAdapter.notifyItemRemoved(position);
                purchasedAdapter.notifyItemInserted(purchasedList.size() - 1);
            }
        });

        purchasedAdapter = new GroceryAdapter(purchasedList, true, new GroceryAdapter.OnItemActionListener() {
            @Override
            public void onDelete(int position) {
                purchasedList.remove(position);
                purchasedAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onIncrease(int position) {
                purchasedList.get(position).quantity++;
                purchasedAdapter.notifyItemChanged(position);
            }

            @Override
            public void onDecrease(int position) {
                if (purchasedList.get(position).quantity > 1) {
                    purchasedList.get(position).quantity--;
                    purchasedAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onMoveToPurchased(int position) {}
        });

        groceryRecycler.setLayoutManager(new LinearLayoutManager(this));
        groceryRecycler.setAdapter(groceryAdapter);

        purchasedRecycler.setLayoutManager(new LinearLayoutManager(this));
        purchasedRecycler.setAdapter(purchasedAdapter);

        // ---------------- VIEWMODEL ----------------
        viewModel = new ViewModelProvider(this).get(MealViewModel.class);

        viewModel.getMeals().observe(this, mealDays -> {

            if (mealDays == null || mealDays.isEmpty()) return;

            if (!isDeletedAll) {
                generateGroceryFromMeals(mealDays);
            }
        });
        autoBtn.setOnClickListener(v -> {

            isDeletedAll = false;

            List<MealDay> mealDays = viewModel.getMeals().getValue();

            if (mealDays == null || mealDays.isEmpty()) {
                Toast.makeText(this, "No weekly meals found", Toast.LENGTH_SHORT).show();
                return;
            }

            generateGroceryFromMeals(mealDays);

            Toast.makeText(this, "Weekly grocery generated", Toast.LENGTH_SHORT).show();
        });
        // ---------------- ADD ----------------
        addBtn.setOnClickListener(v -> showAddDialog());

        // ---------------- DELETE ALL ----------------
        deleteAllBtn.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Delete All Items")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        isDeletedAll = true;

                        groceryList.clear();
                        purchasedList.clear();

                        groceryAdapter.notifyDataSetChanged();
                        purchasedAdapter.notifyDataSetChanged();

                        getSharedPreferences("manual_grocery", MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply();

                        Toast.makeText(this, "All items deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // ---------------- SWIPE ----------------
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                        int pos = viewHolder.getAdapterPosition();

                        if (pos >= 0 && pos < groceryList.size()) {

                            GroceryItem item = groceryList.remove(pos);
                            purchasedList.add(item);

                            groceryAdapter.notifyItemRemoved(pos);
                            purchasedAdapter.notifyItemInserted(purchasedList.size() - 1);
                        }
                    }
                });

        helper.attachToRecyclerView(groceryRecycler);
    }

    // ---------------- GENERATE GROCERY ----------------
    private void generateGroceryFromMeals(List<MealDay> mealDays) {

        executorService.execute(() -> {

            List<GroceryItem> tempList = new ArrayList<>();

            // 1. Meal-based items
            for (MealDay mealDay : mealDays) {

                if (mealDay == null) continue;

                collectIngredients(mealDay.getBreakfast(), tempList);
                collectIngredients(mealDay.getMorningSnack(), tempList);
                collectIngredients(mealDay.getLunch(), tempList);
                collectIngredients(mealDay.getEveningSnack(), tempList);
                collectIngredients(mealDay.getDinner(), tempList);
            }

            List<GroceryItem> mealList = mergeGrocery(tempList);

            // 2. Load manual items
            List<GroceryItem> manualList = getManualItems();

            // 3. Merge both
            List<GroceryItem> finalList = new ArrayList<>();
            finalList.addAll(mealList);
            finalList.addAll(manualList);

            runOnUiThread(() -> {
                groceryList.clear();
                groceryList.addAll(finalList);
                groceryAdapter.notifyDataSetChanged();
            });
        });
    }
    private List<GroceryItem> getManualItems() {

        List<GroceryItem> list = new ArrayList<>();

        android.content.SharedPreferences prefs =
                getSharedPreferences("manual_grocery", MODE_PRIVATE);

        String data = prefs.getString("list", "");

        if (!data.isEmpty()) {

            String[] items = data.split(",");

            for (String item : items) {

                if (!item.trim().isEmpty()) {
                    list.add(new GroceryItem(item.trim(), 1));
                }
            }
        }

        return list;
    }
    // ---------------- COLLECT INGREDIENTS ----------------
    private void collectIngredients(
            String mealName,
            List<GroceryItem> list) {

        if (mealName == null ||
                mealName.trim().isEmpty()) {
            return;
        }

        try {

            List<String> ingredients =
                    IngredientRepository
                            .getIngredients(
                                    this,
                                    mealName
                            );

            Log.d("GROCERY_DEBUG",
                    mealName + " -> " + ingredients);

            for (String ing : ingredients) {

                list.add(new GroceryItem(
                        normalize(ing),
                        1
                ));
            }

        } catch (Exception e) {

            Log.e("GROCERY_ERROR",
                    "Error loading ingredients",
                    e);
        }
    }
    // ---------------- MERGE DUPLICATES ----------------
    private List<GroceryItem> mergeGrocery(List<GroceryItem> list) {

        HashMap<String, GroceryItem> map = new HashMap<>();

        for (GroceryItem item : list) {

            String key = normalize(item.name);

            if (map.containsKey(key)) {
                map.get(key).quantity += item.quantity;
            } else {
                map.put(key, new GroceryItem(key, item.quantity));
            }
        }

        return new ArrayList<>(map.values());
    }
    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
    // ---------------- ADD DIALOG ----------------
    private void showAddDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Grocery Item");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Item name");

        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {

            String name = input.getText().toString().trim();

            if (!name.isEmpty()) {

                String normalizedName = normalize(name);

                boolean found = false;

                for (GroceryItem item : groceryList) {

                    if (normalize(item.name).equals(normalizedName)) {

                        item.quantity++;
                        found = true;

                        groceryAdapter.notifyDataSetChanged();
                        break;
                    }
                }

                if (!found) {

                    saveManualItem(normalizedName);

                    groceryList.add(new GroceryItem(normalizedName, 1));

                    groceryAdapter.notifyItemInserted(groceryList.size() - 1);
                }
            } else {
                Toast.makeText(this, "Enter item name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    private void saveManualItem(String item) {

        android.content.SharedPreferences prefs =
                getSharedPreferences("manual_grocery", MODE_PRIVATE);

        String existing = prefs.getString("list", "");

        existing = existing + item + ",";

        prefs.edit().putString("list", existing).apply();
    }
    private void loadManualItems() {

        android.content.SharedPreferences prefs =
                getSharedPreferences("manual_grocery", MODE_PRIVATE);

        String data = prefs.getString("list", "");

        if (!data.isEmpty()) {

            String[] items = data.split(",");

            for (String item : items) {

                if (!item.trim().isEmpty()) {
                    groceryList.add(new GroceryItem(item.trim(), 1));
                }
            }

            groceryAdapter.notifyDataSetChanged();
        }
    }
}