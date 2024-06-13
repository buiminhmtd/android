package com.example.mealreceiptapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.net.Uri;
import java.io.IOException;
import android.content.ContentValues;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private DBHelper dbHelper;
    private ImageView avatarImageView;
    private RelativeLayout fieldRelativeLayout;
    private RelativeLayout feedsRelativeLayout;
    private ImageView bgImageView;
    private TextView usernameTextView;
    private TextView bioTextView;
    private SQLiteDatabase db;

    private List<Map<String, Object>> mealList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameTextView = findViewById(R.id.username);
        bioTextView = findViewById(R.id.bio);
        dbHelper = new DBHelper(this);
        avatarImageView = findViewById(R.id.avatar);
        fieldRelativeLayout = findViewById(R.id.field);
        feedsRelativeLayout = findViewById(R.id.feeds);
        bgImageView = findViewById(R.id.bg_ek1);
        db = dbHelper.getReadableDatabase();
        
        
        String username = getUsernameFromDatabase();
        String selfDescription = getSelfDescriptionFromDatabase();

        usernameTextView.setText(username);
        bioTextView.setText(selfDescription);

        // Set click listener for the avatar image view
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery to select an image
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK);
            }
        });

        // Set click listener for the field relative layout
        fieldRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ProfileEditingActivity
                Intent profileEditingIntent = new Intent(ProfileActivity.this, ProfileEditingActivity.class);
                startActivity(profileEditingIntent);
            }
        });
        
        // Set click listener for the bg image view
        bgImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CreateMealActivity
                Intent createMealIntent = new Intent(ProfileActivity.this, CreateMealActivity.class);
                startActivity(createMealIntent);
            }
        });

        // Set click listener for the "Home" button
        ImageView homeImageView = findViewById(R.id.union_ek5);
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start HomeActivity
                Intent homeIntent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });

        // Set click listener for the "Discover" button
        ImageView discoverImageView = findViewById(R.id.union_ek3);
        discoverImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start DiscoverActivity
                Intent discoverIntent = new Intent(ProfileActivity.this, SavedMealsActivity.class);
                startActivity(discoverIntent);
            }
        });

        // Set click listener for the "Notification" button
        ImageView notificationImageView = findViewById(R.id.union_ek2);
        notificationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start NotificationActivity
                Intent notificationIntent = new Intent(ProfileActivity.this, NotificationActivity.class);
                startActivity(notificationIntent);
            }
        });

        // Load and display the user's avatar
        loadAvatar();
        // Load and display the user's feeds
        loadFeeds();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                // Get the selected image URI
                final String imageUri = data.getDataString();
                // Convert the image URI to a byte array
                byte[] imageData = convertImageUriToByteArray(imageUri);
                // Save the image data to the database
                saveAvatar(imageData);
                // Load and display the updated avatar
                loadAvatar();
            }
        }
    }

    private byte[] convertImageUriToByteArray(String imageUri) {
        // Convert the image URI to a byte array
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(imageUri));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveAvatar(byte[] imageData) {
        // Save the image data to the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("profileImage", imageData);
        db.update("USERS", values, null, null);
        db.close();
    }

    private void loadAvatar() {
        // Load and display the user's avatar from the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("USERS", new String[]{"profileImage"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int avatarColumnIndex = cursor.getColumnIndex("profileImage");
            if (avatarColumnIndex != -1) {
                byte[] imageData = cursor.getBlob(avatarColumnIndex);
                if (imageData != null && imageData.length > 0) {
                    // User has an avatar image, decode and display it
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    avatarImageView.setImageBitmap(bitmap);
                } else {
                    // User does not have an avatar image, set the default image
                    avatarImageView.setImageResource(R.drawable.avatar_ek1);
                }
            }
        }
        cursor.close();
        db.close();
    }

    private void loadFeeds() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("MEALS", new String[]{"mealID", "mealImage"}, null, null, null, null, null);
        int feedCount = cursor.getCount();
        TextView feedCountTextView = findViewById(R.id._4);
        feedCountTextView.setText("Số lượng: " + feedCount);

        int feedIndex = 1;
        int mealImageColumnIndex = cursor.getColumnIndex("mealImage");
        int mealIDColumnIndex = cursor.getColumnIndex("mealID");
        while (cursor.moveToNext()) {
            // Check if mealImageColumnIndex and mealIDColumnIndex are valid
            if (mealImageColumnIndex != -1 && mealIDColumnIndex != -1) {
                byte[] imageData = cursor.getBlob(mealImageColumnIndex);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                ImageView feedImageView = findViewById(getResources().getIdentifier("feed_" + feedIndex, "id", getPackageName()));

                if (feedImageView != null) {
                    feedImageView.setImageBitmap(bitmap);

                    // Set OnClickListener for each feed ImageView
                    final int mealID = cursor.getInt(mealIDColumnIndex);
                    feedImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Open MealActivity with the selected mealID
                            Intent mealIntent = new Intent(ProfileActivity.this, MealActivity.class);
                            mealIntent.putExtra("mealID", mealID);
                            startActivity(mealIntent);
                        }
                    });

                    feedIndex++;
                } else {
                    // Log an error if the ImageView is not found
                    Log.e("ProfileActivity", "ImageView not found for feed_" + feedIndex);
                }
            }
        }
        cursor.close();
        db.close();
    }


    private String getUsernameFromDatabase() {
        String username = "";

        Cursor cursor = db.query("USERS", new String[]{"fullname"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int usernameColumnIndex = cursor.getColumnIndex("fullname");
            if (usernameColumnIndex != -1) {
                username = cursor.getString(usernameColumnIndex);
            }
        }
        cursor.close();

        return username;
    }

    private String getSelfDescriptionFromDatabase() {
        String selfDescription = "";

        Cursor cursor = db.query("USERS", new String[]{"selfDescription"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int selfDescriptionColumnIndex = cursor.getColumnIndex("selfDescription");
            if (selfDescriptionColumnIndex != -1) {
                selfDescription = cursor.getString(selfDescriptionColumnIndex);
            }
        }
        cursor.close();

        return selfDescription;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng cơ sở dữ liệu khi không cần thiết nữa
        db.close();
    }
}
