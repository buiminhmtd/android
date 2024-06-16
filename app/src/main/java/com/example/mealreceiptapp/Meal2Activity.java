package com.example.mealreceiptapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Meal2Activity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int mealID;
    private ImageView mealImageView;
    private TextView mealNameTextView, reviewCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal2);

        mealID = getIntent().getIntExtra("mealID", -1);

        if (mealID == -1) {
            finish();
            return;
        }

        dbHelper = new DBHelper(this);

        mealImageView = findViewById(R.id.food_thumbnail);
        mealNameTextView = findViewById(R.id.title_ek1);
        reviewCountTextView = findViewById(R.id.total_reviews___label);

        populateMealDetails();
    }

    private void populateMealDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {"mealName", "mealImage", "steps"};
        String selection = "mealID=?";
        String[] selectionArgs = {String.valueOf(mealID)};
        Cursor cursor = db.query("MEALS", projection, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String mealName = cursor.getString(cursor.getColumnIndexOrThrow("mealName"));
            mealNameTextView.setText(mealName);

            byte[] mealImageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("mealImage"));
            if (mealImageBytes != null) {
                Bitmap mealImageBitmap = BitmapFactory.decodeByteArray(mealImageBytes, 0, mealImageBytes.length);
                mealImageView.setImageBitmap(mealImageBitmap);
            }

            String steps = cursor.getString(cursor.getColumnIndexOrThrow("steps"));
            displaySteps(steps);

            // Display reviews
            displayReviews(mealID);

            cursor.close();
        } else {
            Log.d("MealActivity", "No meal found with ID: " + mealID);
        }

        int reviewCount = getReviewCountForMeal(mealID);
        reviewCountTextView.setText(String.valueOf(reviewCount));

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

    private void displaySteps(String steps) {
        LinearLayout stepsContainer = findViewById(R.id.steps_container);
        String[] stepsArray = steps.split(";");
        for (String step : stepsArray) {
            TextView stepTextView = new TextView(this);
            stepTextView.setText(step);
            stepsContainer.addView(stepTextView);
        }
    }

    private void displayReviews(int mealID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT r.content, u.username FROM REVIEWS r JOIN USERS u ON r.userID = u.userID WHERE r.mealID=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(mealID)});

        if (cursor != null && cursor.moveToFirst()) {
            LinearLayout reviewsContainer = findViewById(R.id.reviews_list);

            reviewsContainer.removeAllViews();

            do {
                String reviewContent = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));

                TextView reviewTextView = new TextView(this);
                reviewTextView.setText(username + ": " + reviewContent);
                reviewsContainer.addView(reviewTextView);
            } while (cursor.moveToNext());

            cursor.close();
        }
    }
}
