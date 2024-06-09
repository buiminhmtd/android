package com.example.mealreceiptapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.net.Uri;
import java.io.IOException;
import android.content.ContentValues;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private DBHelper dbHelper;
    private ImageView avatarImageView;
    private RelativeLayout fieldRelativeLayout;
    private RelativeLayout feedsRelativeLayout;
    private ImageView bgImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DBHelper(this);
        avatarImageView = findViewById(R.id.avatar);
        fieldRelativeLayout = findViewById(R.id.field);
        feedsRelativeLayout = findViewById(R.id.feeds);
        bgImageView = findViewById(R.id.bg_ek1);

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

        // Set click listener for the feeds relative layout
        feedsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MealActivity
                // You need to provide the meal ID to the MealActivity
                int mealID = 1; // Replace with the appropriate meal ID
                Intent mealIntent = new Intent(ProfileActivity.this, MealActivity.class);
                mealIntent.putExtra("mealID", mealID);
                startActivity(mealIntent);
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
        values.put("avatar", imageData);
        db.update("USERS", values, null, null);
        db.close();
    }

    private void loadAvatar() {
        // Load and display the user's avatar from the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("USERS", new String[]{"avatar"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int avatarColumnIndex = cursor.getColumnIndex("avatar");
            if (avatarColumnIndex != -1) {
                byte[] imageData = cursor.getBlob(avatarColumnIndex);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                avatarImageView.setImageBitmap(bitmap);
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
        while (cursor.moveToNext()) {
            if (mealImageColumnIndex != -1) {
                byte[] imageData = cursor.getBlob(mealImageColumnIndex);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                ImageView feedImageView = findViewById(getResources().getIdentifier("feed_" + feedIndex, "id", getPackageName()));
                feedImageView.setImageBitmap(bitmap);
                feedIndex++;
            }
        }
        cursor.close();
        db.close();
    }
}
