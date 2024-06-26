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
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MealDB.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Method to insert a new meal into the database
    public long insertMeal(String mealName, byte[] mealImage, String description, String steps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mealName", mealName);
        values.put("mealImage", mealImage);
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

    // Method to add a saved meal to the database
    public void addSavedMeal(int mealID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mealID", mealID);
        db.insert("SAVED_MEALS", null, values);
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

    public int getMealIDByName(String mealName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int mealID = -1;
        String query = "SELECT mealID FROM MEALS WHERE mealName=?";
        Cursor cursor = db.rawQuery(query, new String[]{mealName});
        if (cursor != null && cursor.moveToFirst()) {
            mealID = cursor.getInt(cursor.getColumnIndexOrThrow("mealID"));
            cursor.close();
        }
        db.close();
        return mealID;
    }

    public long addUser(String username, String password, String fullName, byte[] profileImage, String selfDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;

        try {
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            values.put("fullname", fullName);
            values.put("profileImage", profileImage);
            values.put("selfDescription", selfDescription);

            result = db.insertWithOnConflict("USERS", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            Log.e("DBHelper", "Error adding user to database", e);
        } finally {
            db.close();
        }

        return result;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { "userID" };
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = { username, password };

        Cursor cursor = db.query("USERS", columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }


    public Map<String, Object> getUserByID(int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"fullname", "selfDescription"};
        String selection = "userID" + " = ?";
        String[] selectionArgs = {String.valueOf(userID)};
        Cursor cursor = db.query("USERS", columns, selection, selectionArgs, null, null, null);

        Map<String, Object> user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new HashMap<>();
            int usernameIndex = cursor.getColumnIndex("fullname");
            int selfDescriptionIndex = cursor.getColumnIndex("selfDescription");

            if (usernameIndex != -1 && selfDescriptionIndex != -1) {
                String username = cursor.getString(usernameIndex);
                String selfDescription = cursor.getString(selfDescriptionIndex);

                user.put("username", username);
                user.put("selfDescription", selfDescription);
            }

            cursor.close();
        }

        return user;
    }
    public boolean updateUserImage(int userID, byte[] userImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("profileImage", userImage);

        int rowsAffected = db.update("USERS", values, "userID" + " = ?", new String[]{String.valueOf(userID)});
        return rowsAffected > 0;
    }
    public boolean updateUser(int userID, String username, String mota) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fullname", username);
        values.put("mota", mota);

        int rowsAffected = db.update("USERS", values, "userID" + " = ?", new String[]{String.valueOf(userID)});
        return rowsAffected > 0;
    }
    
    public boolean updateUserProfile(long userID, String fullname, String selfDescription, byte[] profileImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fullname", fullname);
        values.put("selfDescription", selfDescription);
        values.put("profileImage", profileImage);

        // updating row
        int rowsAffected = db.update("USERS", values, "userID" + " = ?",
                new String[]{String.valueOf(userID)});

        db.close();

        return rowsAffected > 0;
    }
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

}
