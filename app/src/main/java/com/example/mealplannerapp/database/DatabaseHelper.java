package com.example.mealplannerapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "MealPlannerDB";
    private static final int DB_VERSION = 12;

    private static final String TABLE_MEALS = "meals";

    private static final String COL_ID = "id";
    private static final String COL_MEAL_TYPE = "meal_type";
    private static final String COL_MEAL_VALUE = "meal_value";
    private static final String COL_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + TABLE_MEALS + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_MEAL_TYPE + " TEXT, " +
                        COL_MEAL_VALUE + " TEXT, " +
                        COL_DATE + " TEXT, " +
                        "UNIQUE(" + COL_MEAL_TYPE + "," + COL_DATE + ") ON CONFLICT REPLACE)"
        );
    }

    // ================= SAVE =================
    public void saveMeal(String mealType, String value, String date) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_MEAL_TYPE, mealType);
        cv.put(COL_MEAL_VALUE, value);
        cv.put(COL_DATE, date);

        db.insertWithOnConflict(TABLE_MEALS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // ================= GET =================
    public String getMeal(String mealType, String date) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_MEAL_VALUE +
                        " FROM " + TABLE_MEALS +
                        " WHERE " + COL_MEAL_TYPE + "=? AND " + COL_DATE + "=?",
                new String[]{mealType, date}
        );

        String result = "";

        if (cursor.moveToFirst()) {
            result = cursor.getString(0);
        }

        cursor.close();
        return result;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS);
        onCreate(db);
    }
}