package com.example.mealreceiptapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.aware.DiscoverySession;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.content.DialogInterface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mealreceiptapp.DBHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private RelativeLayout fieldLayout;
    private ImageView avatarImageView;
    private RelativeLayout profilePageLayout;
    private TextView usernameTextView;
    private RelativeLayout feedsLayout;
    private ImageView bgImageView;
    private ImageView homeImageView;
    private ImageView discoverImageView;
    private ImageView notificationImageView;

    private int selectedFeedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        fieldLayout = findViewById(R.id.field);
        avatarImageView = findViewById(R.id.avatar);
        profilePageLayout = findViewById(R.id.page_profile_page_ek1);
        usernameTextView = findViewById(R.id.username);
        feedsLayout = findViewById(R.id.feeds);
        bgImageView = findViewById(R.id.bg_ek1);
        homeImageView = findViewById(R.id.union_ek5);
        discoverImageView = findViewById(R.id.union_ek3);
        notificationImageView = findViewById(R.id.union_ek2);

        // Set click listeners
        fieldLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileEditingActivity();
            }
        });

        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        bgImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateMealActivity();
            }
        });

        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeActivity();
            }
        });

        discoverImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDiscoverActivity();
            }
        });

        notificationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotificationActivity();
            }
        });

        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Query and display feeds
        displayFeeds();
    }

    private void openProfileEditingActivity() {
        Intent intent = new Intent(this, ProfileEditingActivity.class);
        startActivity(intent);
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                avatarImageView.setImageBitmap(bitmap);
                saveImageToDatabase(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Handle OK button click
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void saveImageToDatabase(Bitmap bitmap) {
        // Convert the bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Save the byte array to SQLite database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("image", byteArray);
        long result = db.insert("profile", null, values);

        if (result != -1) {
            showAlert("Đã sửa ảnh");
        } else {
            showAlert("Không thể sửa ảnh");
        }
    }

    private void openCreateMealActivity() {
        Intent intent = new Intent(this, CreateMealActivity.class);
        startActivity(intent);
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void openDiscoverActivity() {
        Intent intent = new Intent(this, SavedMealsActivity.class);
        startActivity(intent);
    }

    private void openNotificationActivity() {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    private void displayFeeds() {
        // Query feeds from database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM feeds", null);

        // Get the column index for "id"
        int feedIdColumnIndex = cursor.getColumnIndex("id");

        // Iterate over the cursor and display each feed as an ImageView
        while (cursor.moveToNext()) {
            if (feedIdColumnIndex != -1) {
                int feedId = cursor.getInt(feedIdColumnIndex);
                // Get other feed data from the cursor

                ImageView feedImageView = new ImageView(this);
                // Set attributes for the feedImageView
                // ...

                // Set click listener for the feedImageView
                feedImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openMealActivity(feedId);
                    }
                });

                // Add the feedImageView to the feedsLayout
                feedsLayout.addView(feedImageView);
            } else {
                Log.d("ProfileActivity", "Column 'id' not found in the cursor.");
            }
        }

        cursor.close();

        // Update the count of feeds
        int feedCount = feedsLayout.getChildCount();
        TextView feedCountTextView = findViewById(R.id._4);
        feedCountTextView.setText("Số lượng: " + feedCount);
    }

    private void openMealActivity(int feedId) {
        // Pass the selected feedId to the MealActivity
        Intent intent = new Intent(this, MealActivity.class);
        intent.putExtra("feedId", feedId);
        startActivity(intent);
    }
}
