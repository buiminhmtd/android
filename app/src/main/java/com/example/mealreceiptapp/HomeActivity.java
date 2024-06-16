package com.example.mealreceiptapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements MealAdapter.OnItemClickListener {

    private RecyclerView mealRecyclerView;
    private MealAdapter mealAdapter;
    private List<Map<String, Object>> mealList;
    private ImageButton savedMealsButton;
    private ImageButton addMealButton;
    private ImageButton homeButton;
    private ImageButton notificationButton;
    private ImageButton profileButton;
    private ImageButton searchButton;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mealRecyclerView = findViewById(R.id.mealRecyclerView);
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        DBHelper dbHelper = new DBHelper(this);
        mealList = dbHelper.getAllMeals();

        mealAdapter = new MealAdapter(this, mealList);
        mealAdapter.setOnItemClickListener(this);
        mealRecyclerView.setAdapter(mealAdapter);

        savedMealsButton = findViewById(R.id.menuSavedMeals);
        addMealButton = findViewById(R.id.addMeal);
        homeButton = findViewById(R.id.menuHome);
        notificationButton = findViewById(R.id.menuNotification);
        profileButton = findViewById(R.id.menuProfile);
        searchButton = findViewById(R.id.searchButton);
        searchBar = findViewById(R.id.searchBar);

        savedMealsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SavedMealsActivity.class);
            startActivity(intent);
        });

        addMealButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CreateMealActivity.class);
            startActivity(intent);
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        notificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        searchButton.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();
            if (!query.isEmpty()) {
                searchMeal(query);
            } else {
                Toast.makeText(HomeActivity.this, "Please enter a meal name to search", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchMeal(String query) {
        DBHelper dbHelper = new DBHelper(this);
        int mealID = dbHelper.getMealIDByName(query);

        if (mealID != -1) {
            Intent intent = new Intent(HomeActivity.this, MealActivity.class);
            intent.putExtra("mealID", mealID);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No meal found with the name: " + query, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position) {
        int mealID = (int) mealList.get(position).get("mealID");
        Intent intent = new Intent(this, MealActivity.class);
        intent.putExtra("mealID", mealID);
        startActivity(intent);
    }
}
