package com.example.mealreceiptapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class SavedMealsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        List<Integer> savedMealIDs = getIntent().getIntegerArrayListExtra("savedMealIDs");
        displaySavedMeals(savedMealIDs);
    }

    private void displaySavedMeals(List<Integer> savedMealIDs) {
        if (savedMealIDs != null) {
            LinearLayout contentContainer = findViewById(R.id.content_container);

            for (int mealID : savedMealIDs) {
                View savedMealLayout = LayoutInflater.from(this).inflate(R.layout.saved_meal_item, contentContainer, false);
                ImageView mealImage = savedMealLayout.findViewById(R.id.mealImage);
                TextView mealName = savedMealLayout.findViewById(R.id.mealName);

                mealImage.setImageResource(R.drawable.placeholder_image);
                mealName.setText("Meal Name");

                contentContainer.addView(savedMealLayout);
            }
        }
    }
}
