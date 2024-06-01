// DBHelper.java
package com.example.mealreceiptapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MealDB.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // No need to create tables here since they already exist in the database
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade logic if needed
    }

    // Method to insert a new meal into the database
    public long insertMeal(String mealName, byte[] mealImage, String description, String steps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mealName", mealName);
        values.put("mealImage", mealImage); // Store image as blob
        values.put("description", description);
        values.put("steps", steps);
        long mealID = db.insert("MEALS", null, values);
        db.close();
        return mealID;
    }

    // Method to insert a new ingredient into the database
    public long insertIngredient(String ingredientName, String ingredientQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ingredientName", ingredientName);
        values.put("ingredientQuantity", ingredientQuantity);
        long ingredientID = db.insert("INGREDIENTS", null, values);
        db.close();
        return ingredientID;
    }

    // Method to insert a new meal-ingredient relationship into the database
    public void insertMealIngredient(long mealID, long ingredientID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mealID", mealID);
        values.put("ingredientID", ingredientID);
        db.insert("MEAL_INGREDIENTS", null, values);
        db.close();
    }

    // DBHelper.java
    public List<Map<String, Object>> getAllMeals() {
        List<Map<String, Object>> mealList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("MEALS", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> meal = new HashMap<>();
                meal.put("mealID", cursor.getInt(cursor.getColumnIndexOrThrow("mealID")));
                meal.put("mealName", cursor.getString(cursor.getColumnIndexOrThrow("mealName")));
                meal.put("mealImage", cursor.getBlob(cursor.getColumnIndexOrThrow("mealImage")));
                meal.put("description", cursor.getString(cursor.getColumnIndexOrThrow("description")));
                meal.put("steps", cursor.getString(cursor.getColumnIndexOrThrow("steps")));
                mealList.add(meal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mealList;
    }

    public Map<String, Object> getMealByID(int mealID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("MEALS", null, "mealID = ?", new String[]{String.valueOf(mealID)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Map<String, Object> meal = new HashMap<>();
            meal.put("mealID", cursor.getInt(cursor.getColumnIndexOrThrow("mealID")));
            meal.put("mealName", cursor.getString(cursor.getColumnIndexOrThrow("mealName")));
            meal.put("mealImage", cursor.getBlob(cursor.getColumnIndexOrThrow("mealImage")));
            meal.put("description", cursor.getString(cursor.getColumnIndexOrThrow("description")));
            meal.put("steps", cursor.getString(cursor.getColumnIndexOrThrow("steps")));
            cursor.close();
            db.close();
            return meal;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return null;
        }
    }

}
