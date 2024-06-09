package com.example.mealreceiptapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SavedMealsActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);
        displaySavedMeals();
    }

    // Method to display saved meals
    private void displaySavedMeals() {
        LinearLayout contentContainer = findViewById(R.id.content_container);
        List<Map<String, Object>> savedMeals = getSavedMealsFromDB();
        for (Map<String, Object> meal : savedMeals) {
            // Inflate the layout for saved meal item
            View savedMealLayout = LayoutInflater.from(this).inflate(R.layout.saved_meal_item, contentContainer, false);
            ImageView mealImage = savedMealLayout.findViewById(R.id.mealImage);
            TextView mealName = savedMealLayout.findViewById(R.id.mealName);
            TextView creatorName = savedMealLayout.findViewById(R.id.name___label);

            // Set meal image, name, and creator name
            byte[] mealImageBytes = (byte[]) meal.get("mealImage");
            if (mealImageBytes != null) {
                mealImage.setImageBitmap(BitmapFactory.decodeByteArray(mealImageBytes, 0, mealImageBytes.length));
            }
            mealName.setText((String) meal.get("mealName"));
            creatorName.setText((String) meal.get("creatorName"));

            // Add the saved meal layout to the content container
            contentContainer.addView(savedMealLayout);
        }
    }

    // Method to retrieve saved meals from the database
    private List<Map<String, Object>> getSavedMealsFromDB() {
        List<Map<String, Object>> savedMeals = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("SAVED_MEALS", null, null, null, null, null, null);
        if (cursor != null) {
            int mealIDColumnIndex = cursor.getColumnIndex("mealID");
            if (cursor.moveToFirst()) {
                do {
                    int mealID = cursor.getInt(mealIDColumnIndex);
                    Map<String, Object> meal = dbHelper.getMealByID(mealID);
                    if (meal != null) {
                        savedMeals.add(meal);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return savedMeals;
    }
}
