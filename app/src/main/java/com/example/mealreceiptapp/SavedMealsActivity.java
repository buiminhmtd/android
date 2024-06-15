package com.example.mealreceiptapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;

public class SavedMealsActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    private ImageButton createMeal;

    private ImageButton Home;

    private ImageButton Notification;

    private ImageButton Profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        dbHelper = new DBHelper(this);
        createMeal = findViewById(R.id.union_ek4);
        Home =  findViewById(R.id.union_ek5);
        Notification =  findViewById(R.id.union_ek2);
        Profile = findViewById(R.id.union_ek1);

        List<Integer> savedMealIDs = getIntent().getIntegerArrayListExtra("savedMealIDs");
        if (savedMealIDs == null) {
            savedMealIDs = MealActivity.getSavedMealIDs(); // Use the public static method to get savedMealIDs
        }
        displaySavedMeals(savedMealIDs);

        createMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CreateMealActivity
                Intent createMealIntent = new Intent(SavedMealsActivity.this, CreateMealActivity.class);
                startActivity(createMealIntent);
            }
        });

        // Set click listener for the "Home" button
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start HomeActivity
                Intent homeIntent = new Intent(SavedMealsActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });

        // Set click listener for the "Discover" button

        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start DiscoverActivity
                Intent discoverIntent = new Intent(SavedMealsActivity.this, ProfileActivity.class);
                startActivity(discoverIntent);
            }
        });

        // Set click listener for the "Notification" button
        Notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start NotificationActivity
                Intent notificationIntent = new Intent(SavedMealsActivity.this, NotificationActivity.class);
                startActivity(notificationIntent);
            }
        });
    }

    private void displaySavedMeals(List<Integer> savedMealIDs) {
        if (savedMealIDs != null) {
            LinearLayout contentContainer = findViewById(R.id.saved_meals_list); // Corrected ID

            for (int mealID : savedMealIDs) {
                Map<String, Object> meal = dbHelper.getMealByID(mealID);
                if (meal != null) {
                    View savedMealLayout = LayoutInflater.from(this).inflate(R.layout.saved_meal_item, contentContainer, false);
                    ImageView mealImage = savedMealLayout.findViewById(R.id.saved_meal_image);
                    TextView mealName = savedMealLayout.findViewById(R.id.saved_meal_name);

                    byte[] mealImageBytes = (byte[]) meal.get("mealImage");
                    if (mealImageBytes != null) {
                        Bitmap mealImageBitmap = BitmapFactory.decodeByteArray(mealImageBytes, 0, mealImageBytes.length);
                        mealImage.setImageBitmap(mealImageBitmap);
                    } else {
                        mealImage.setImageResource(R.drawable.placeholder_image); // Placeholder image
                    }

                    mealName.setText((String) meal.get("mealName"));
                    contentContainer.addView(savedMealLayout);
                }
            }
        }
    }
}
