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

        // Get meal ID from intent
        mealID = getIntent().getIntExtra("mealID", -1);

        if (mealID == -1) {
            // Handle error, mealID was not passed correctly
            // For example, show a toast or finish the activity
            finish();
            return;
        }

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Find views
        mealImageView = findViewById(R.id.food_thumbnail);
        mealNameTextView = findViewById(R.id.title_ek1);
        reviewCountTextView = findViewById(R.id.total_reviews___label);

        // Populate meal details
        populateMealDetails();
    }

    private void populateMealDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query meal details including steps
        String[] projection = {"mealName", "mealImage", "steps"};
        String selection = "mealID=?";
        String[] selectionArgs = {String.valueOf(mealID)};
        Cursor cursor = db.query("MEALS", projection, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Populate meal name
            String mealName = cursor.getString(cursor.getColumnIndexOrThrow("mealName"));
            mealNameTextView.setText(mealName);

            // Populate meal image
            byte[] mealImageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("mealImage"));
            if (mealImageBytes != null) {
                Bitmap mealImageBitmap = BitmapFactory.decodeByteArray(mealImageBytes, 0, mealImageBytes.length);
                mealImageView.setImageBitmap(mealImageBitmap);
            }

            // Populate steps
            String steps = cursor.getString(cursor.getColumnIndexOrThrow("steps"));
            displaySteps(steps);

            // Display reviews
            displayReviews(mealID);

            cursor.close();
        } else {
            Log.d("MealActivity", "No meal found with ID: " + mealID);
        }

        // Get number of reviews for this meal
        int reviewCount = getReviewCountForMeal(mealID);
        reviewCountTextView.setText(String.valueOf(reviewCount));

        // Close database connection
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
        String[] stepsArray = steps.split(";"); // Assuming steps are separated by semicolon
        for (String step : stepsArray) {
            TextView stepTextView = new TextView(this);
            stepTextView.setText(step);
            stepsContainer.addView(stepTextView);
        }
    }

    private void displayReviews(int mealID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query reviews for the meal along with user information
        String query = "SELECT r.content, u.username FROM REVIEWS r JOIN USERS u ON r.userID = u.userID WHERE r.mealID=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(mealID)});

        if (cursor != null && cursor.moveToFirst()) {
            LinearLayout reviewsContainer = findViewById(R.id.reviews_list);

            // Clear existing views
            reviewsContainer.removeAllViews();

            // Display each review along with the user
            do {
                String reviewContent = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));

                // Create a TextView to display the review content along with the username
                TextView reviewTextView = new TextView(this);
                reviewTextView.setText(username + ": " + reviewContent);
                reviewsContainer.addView(reviewTextView);
            } while (cursor.moveToNext());

            cursor.close();
        }
    }
}
