package com.example.mealreceiptapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int mealID;
    private ImageView mealImageView;
    private TextView mealNameTextView, reviewCountTextView;
    private static List<Integer> savedMealIDs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        mealID = getIntent().getIntExtra("mealID", -1);
        if (mealID == -1) {
            finish();
            return;
        }

        dbHelper = new DBHelper(this);

        mealImageView = findViewById(R.id.mealImage);
        mealNameTextView = findViewById(R.id.title_ek1);
        reviewCountTextView = findViewById(R.id.reviewCount);

        populateMealDetails();

        //Xem các bước làm
        Button viewRecipeButton = findViewById(R.id.xemCongThuc);
        viewRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MealActivity.this, Meal2Activity.class);
                intent.putExtra("mealID", mealID);
                startActivity(intent);
            }
        });

        //Lưu công thức
        ImageButton saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedMealIDs.add(mealID);
                Toast.makeText(MealActivity.this, "Meal saved successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static List<Integer> getSavedMealIDs() {
        return savedMealIDs;
    }

    private void populateMealDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {"mealName", "mealImage"};
        String selection = "mealID=?";
        String[] selectionArgs = {String.valueOf(mealID)};
        Cursor cursor = db.query("MEALS", projection, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String mealName = cursor.getString(cursor.getColumnIndexOrThrow("mealName"));
            mealNameTextView.setText(mealName);

            // Populate meal image
            byte[] mealImageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("mealImage"));
            if (mealImageBytes != null) {
                Bitmap mealImageBitmap = BitmapFactory.decodeByteArray(mealImageBytes, 0, mealImageBytes.length);
                mealImageView.setImageBitmap(mealImageBitmap);
            } else {
                Log.d("MealActivity", "No meal image found.");
            }

            cursor.close();
        } else {
            Log.d("MealActivity", "No meal found with ID: " + mealID);
        }

        int reviewCount = getReviewCountForMeal(mealID);
        reviewCountTextView.setText(String.valueOf(reviewCount));

        displayIngredients(mealID);

        db.close();
    }

    private int getReviewCountForMeal(int mealID) {
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

    //Lấy danh sách các nguyên liệu
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
                ingredient.put("ingredientImage", cursor.getBlob(cursor.getColumnIndexOrThrow("ingredientImage")));
                ingredients.add(ingredient);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return ingredients;
    }

    //Hiển thị danh sách nguyên liệu
    private void displayIngredients(long mealID) {
        List<Map<String, Object>> ingredients = getIngredientsForMeal(mealID);
        RecyclerView recyclerView = findViewById(R.id.ingredients_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapter adapter = new IngredientAdapter(ingredients);
        recyclerView.setAdapter(adapter);
    }

    class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

        private List<Map<String, Object>> ingredients;

        IngredientAdapter(List<Map<String, Object>> ingredients) {
            this.ingredients = ingredients;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ingredient_row2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Map<String, Object> ingredient = ingredients.get(position);
            holder.ingredientName.setText((String) ingredient.get("ingredientName"));
            byte[] ingredientImageBytes = (byte[]) ingredient.get("ingredientImage");
            if (ingredientImageBytes != null) {
                Bitmap ingredientImageBitmap = BitmapFactory.decodeByteArray(ingredientImageBytes, 0, ingredientImageBytes.length);
                holder.ingredientImage.setImageBitmap(ingredientImageBitmap);
            } else {
                holder.ingredientImage.setImageResource(R.drawable.placeholder_image); // Placeholder image
            }
        }

        @Override
        public int getItemCount() {
            return ingredients.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView ingredientName;
            ImageView ingredientImage;

            ViewHolder(View itemView) {
                super(itemView);
                ingredientName = itemView.findViewById(R.id.ingredientName);
                ingredientImage = itemView.findViewById(R.id.ingredientImage);
            }
        }
    }
}
