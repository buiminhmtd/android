// Inside DBHelper class

package com.example.mealreceiptapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    // Method to insert a new meal-ingredient relationship into the database
    public void insertMealIngredient(long mealID, long ingredientID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mealID", mealID);
        values.put("ingredientID", ingredientID);
        db.insert("MEAL_INGREDIENTS", null, values);
        db.close();
    }
}
