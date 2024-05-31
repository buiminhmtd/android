package com.example.mealreceiptapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private long mealID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        // Get meal ID from intent
        mealID = getIntent().getLongExtra("mealID", -1);

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Populate meal details
        populateMealDetails();
    }

    private void populateMealDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query meal details
        String[] projection = {"mealName", "mealImage"};
        String selection = "mealID=?";
        String[] selectionArgs = {String.valueOf(mealID)};
        Cursor cursor = db.query("MEALS", projection, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Populate meal name
            String mealName = cursor.getString(cursor.getColumnIndexOrThrow("mealName"));
            byte[] mealImageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("mealImage"));

            ImageView mealImageView = findViewById(R.id.food_thumbnail);
            Bitmap mealImageBitmap = BitmapFactory.decodeByteArray(mealImageBytes, 0, mealImageBytes.length);
            mealImageView.setImageBitmap(mealImageBitmap);

            cursor.close();
        }

        // Get number of reviews for this meal
        int reviewCount = getReviewCountForMeal(mealID);
        TextView reviewCountTextView = findViewById(R.id.reviewCount);
        reviewCountTextView.setText(String.valueOf(reviewCount));

        // Display ingredients for this meal
        displayIngredients(mealID);

        // Close database connection
        db.close();
    }

    private int getReviewCountForMeal(long mealID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM REVIEWS WHERE mealID=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(mealID)});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    private List<Map<String, Object>> getIngredientsForMeal(long mealID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT i.ingredientName, i.ingredientImage FROM INGREDIENTS i " +
                "JOIN MEAL_INGREDIENTS mi ON i.ingredientID = mi.ingredientID " +
                "WHERE mi.mealID=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(mealID)});
        List<Map<String, Object>> ingredients = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, Object> ingredient = new HashMap<>();
                ingredient.put("ingredientName", cursor.getString(cursor.getColumnIndexOrThrow("ingredientName")));
                byte[] ingredientImage = cursor.getBlob(cursor.getColumnIndexOrThrow("ingredientImage"));
                ingredient.put("ingredientImage", ingredientImage);
                ingredients.add(ingredient);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return ingredients;
    }

    private void displayIngredients(long mealID) {
        List<Map<String, Object>> ingredients = getIngredientsForMeal(mealID);
        RecyclerView recyclerView = findViewById(R.id.ingredients_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapter adapter = new IngredientAdapter(ingredients);
        recyclerView.setAdapter(adapter);
    }
}
