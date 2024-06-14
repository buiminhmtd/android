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
    private ImageButton savedMealsButton;
    private ImageButton addMealButton;

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

        savedMealsButton = findViewById(R.id.savedMeals);
        addMealButton = findViewById(R.id.addMeal);

        savedMealsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SavedMealsActivity.class);
                startActivity(intent);
            }
        });

        addMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CreateMealActivity.class);
                startActivity(intent);
            }
        });
    }

    //Lấy thông tin chi tiết món ăn theo id
    @Override
    public void onItemClick(int position) {
        int mealID = (int) mealList.get(position).get("mealID");
        Intent intent = new Intent(this, MealActivity.class);
        intent.putExtra("mealID", mealID);
        startActivity(intent);
    }
}
