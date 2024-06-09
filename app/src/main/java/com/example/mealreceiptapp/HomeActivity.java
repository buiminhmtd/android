package com.example.mealreceiptapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements MealAdapter.OnItemClickListener {

    private RecyclerView mealRecyclerView;
    private MealAdapter mealAdapter;
    private List<Map<String, Object>> mealList;
    private ImageButton savedMealsButton; // Add reference to the saved meals button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mealRecyclerView = findViewById(R.id.mealRecyclerView);
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        DBHelper dbHelper = new DBHelper(this);
        mealList = dbHelper.getAllMeals();

        mealAdapter = new MealAdapter(this, mealList);
        mealAdapter.setOnItemClickListener(this); // Set item click listener
        mealRecyclerView.setAdapter(mealAdapter);

        // Find the saved meals button by ID
        savedMealsButton = findViewById(R.id.savedMeals);

        // Set OnClickListener for the saved meals button
        savedMealsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open SavedMealsActivity when the button is clicked
                Intent intent = new Intent(HomeActivity.this, SavedMealsActivity.class);
                startActivity(intent);
            }
        });
    }

    // Implement the onItemClick method from MealAdapter.OnItemClickListener interface
    @Override
    public void onItemClick(int position) {
        // Get the meal ID of the clicked item
        int mealID = (int) mealList.get(position).get("mealID");

        // Open the MealActivity with the selected meal ID
        Intent intent = new Intent(this, MealActivity.class);
        intent.putExtra("mealID", mealID);
        startActivity(intent);
    }
}
