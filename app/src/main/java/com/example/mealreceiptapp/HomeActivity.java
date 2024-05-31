package com.example.mealreceiptapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView mealRecyclerView;
    private MealAdapter mealAdapter;
    private List<Map<String, Object>> mealList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mealRecyclerView = findViewById(R.id.mealRecyclerView);
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        DBHelper dbHelper = new DBHelper(this);
        mealList = dbHelper.getAllMeals();

        mealAdapter = new MealAdapter(this, mealList);
        mealRecyclerView.setAdapter(mealAdapter);
    }
}

