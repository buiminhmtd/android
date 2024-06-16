package com.example.mealreceiptapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;

    private DBHelper dbHelper;
    private ImageView avatarImageView;
    private RelativeLayout fieldRelativeLayout;
    private RelativeLayout feedsRelativeLayout;
    private ImageView bgImageView;
    private TextView usernameTextView;
    private TextView bioTextView;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameTextView = findViewById(R.id.username);
        bioTextView = findViewById(R.id.bio);
        dbHelper = new DBHelper(this);
        avatarImageView = findViewById(R.id.avatar);
        fieldRelativeLayout = findViewById(R.id.field);
        bgImageView = findViewById(R.id.bg_ek1);
        db = dbHelper.getWritableDatabase(); // Open database for writing

        int userID = getCurrentUserID();

        String username = getUsernameFromDatabase(userID);
        String selfDescription = getSelfDescriptionFromDatabase(userID);

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

        // Load and display the user's feeds
        loadFeeds();
        // Load and display the user's avatar
        loadAvatar(userID);
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
                loadAvatar(getCurrentUserID());
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
        // Save the image data to a file
        String imagePath = saveImageToFile(imageData);

        // Save the image path to the database
        ContentValues values = new ContentValues();
        values.put("profileImagePath", imagePath);
        db.update("USERS", values, "userID = ?", new String[]{String.valueOf(getCurrentUserID())});
    }

    private String saveImageToFile(byte[] imageData) {
        String imagePath = ""; // Initialize with empty string

        FileOutputStream fos = null;
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile = File.createTempFile("avatar", ".jpg", storageDir);
            fos = new FileOutputStream(imageFile);
            fos.write(imageData);
            fos.flush();
            imagePath = imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return imagePath;
    }

    private void loadFeeds() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("MEALS", new String[]{"mealID", "mealImage"}, null, null, null, null, null);
        int feedCount = cursor.getCount();
        TextView feedCountTextView = findViewById(R.id._4);
        feedCountTextView.setText("Số lượng: " + feedCount);

        // Clear previous feed views
        LinearLayout feedContainer = findViewById(R.id.feed_container);
        feedContainer.removeAllViews();

        // Indices of columns in the cursor
        int mealIDColumnIndex = cursor.getColumnIndex("mealID");
        int mealImageColumnIndex = cursor.getColumnIndex("mealImage");

        int marginBetweenImages = dpToPx(10); // Adjust spacing between images

        while (cursor.moveToNext()) {
            int mealID = cursor.getInt(mealIDColumnIndex);

            // Check if mealImageColumnIndex is valid
            if (mealImageColumnIndex != -1) {
                byte[] imageData = cursor.getBlob(mealImageColumnIndex);

                // Create new ImageView for each meal image
                ImageView feedImageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        dpToPx(160), // Width of each image view (adjust as needed)
                        dpToPx(172) // Height of each image view (adjust as needed)
                );

                // Set margins between images
                params.setMargins(marginBetweenImages, 0, 0, 0);

                feedImageView.setLayoutParams(params);
                feedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                // Load and set meal image if imageData is not null
                if (imageData != null && imageData.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    feedImageView.setImageBitmap(bitmap);
                } else {
                    // Handle case where imageData is null or empty
                    feedImageView.setImageResource(R.drawable._bg__discover_ek2_shape);
                }

                // Set OnClickListener for each feed ImageView
                final int finalMealID = mealID;
                feedImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Open MealActivity with the selected mealID
                        Intent mealIntent = new Intent(ProfileActivity.this, MealActivity.class);
                        mealIntent.putExtra("mealID", finalMealID);
                        startActivity(mealIntent);
                    }
                });

                // Add the ImageView to the feedContainer
                feedContainer.addView(feedImageView);
            } else {
                // Log an error or handle the case where mealImageColumnIndex is -1
                Log.e("loadFeeds", "mealImageColumnIndex is -1");
            }
        }

        cursor.close();
        db.close();
    }

    // Utility method to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }



    private void loadAvatar(int userID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"profileImage"};
        String selection = "userID=?";
        String[] selectionArgs = {String.valueOf(userID)};

        Cursor cursor = db.query("USERS", projection, selection, selectionArgs, null, null, null);

        // Find the ImageView for avatar
        ImageView avatarImageView = findViewById(R.id.avatar);

        if (cursor != null && cursor.moveToFirst()) {
            int profileImageColumnIndex = cursor.getColumnIndex("profileImage");

            if (profileImageColumnIndex != -1) {
                byte[] profileImageBytes = cursor.getBlob(profileImageColumnIndex);

                if (profileImageBytes != null && profileImageBytes.length > 0) {
                    // Decode byte array into Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(profileImageBytes, 0, profileImageBytes.length);
                    avatarImageView.setImageBitmap(bitmap);
                } else {
                    // Set default avatar image if profileImage is null or empty
                    avatarImageView.setImageResource(R.drawable.line_shape);
                }
            } else {
                // Handle case where profileImage column does not exist
                Log.e("loadAvatar", "profileImage column not found in USERS table");
                avatarImageView.setImageResource(R.drawable.line_shape);
            }
        } else {
            // Handle case where cursor is null or empty
            Log.e("loadAvatar", "Cursor is null or empty");
            avatarImageView.setImageResource(R.drawable.line_shape);
        }

        if (cursor != null) {
            cursor.close(); // Close cursor after use
        }
    }



    private String getUsernameFromDatabase(int userID) {
        String username = "";
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("USERS", new String[]{"fullname"}, "userID = ?",
                new String[]{String.valueOf(userID)}, null, null, null);

        // Kiểm tra cursor có dữ liệu không
        if (cursor != null && cursor.moveToFirst()) {
            // Lấy index của cột fullname
            int usernameColumnIndex = cursor.getColumnIndex("fullname");

            // Kiểm tra xem tên cột có tồn tại không
            if (usernameColumnIndex != -1) {
                // Lấy giá trị từ cột fullname
                username = cursor.getString(usernameColumnIndex);
            } else {
                // Xử lý trường hợp tên cột không tồn tại
                Log.e("ProfileActivity", "Column 'fullname' does not exist in the table USERS");
            }
        } else {
            // Xử lý trường hợp không có dữ liệu từ cursor
            Log.e("ProfileActivity", "No data found for userID: " + userID);
        }

        // Đóng cursor
        if (cursor != null) {
            cursor.close();
        }

        // Đóng database
        db.close();

        return username;
    }

    private String getSelfDescriptionFromDatabase(int userID) {
        String selfDescription = "";
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("USERS", new String[]{"selfDescription"}, "userID = ?",
                new String[]{String.valueOf(userID)}, null, null, null);

        if (cursor.moveToFirst()) {
            int selfDescriptionColumnIndex = cursor.getColumnIndex("selfDescription");
            if (selfDescriptionColumnIndex != -1) {
                selfDescription = cursor.getString(selfDescriptionColumnIndex);
            }
        }

        cursor.close();
        return selfDescription;
    }

    private int getCurrentUserID() {
        int userID = -1; // Default value, or any value that indicates no user ID is found
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("USERS", new String[]{"userID"}, null,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            int userIDColumnIndex = cursor.getColumnIndex("userID");
            if (userIDColumnIndex != -1) {
                userID = cursor.getInt(userIDColumnIndex);
            }
        }

        cursor.close();
        return userID;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database when no longer needed
        dbHelper.close();
    }
}
