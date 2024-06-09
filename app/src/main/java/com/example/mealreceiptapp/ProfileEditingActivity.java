package com.example.mealreceiptapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class ProfileEditingActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 123;
    private static final String USER_ID = "user_id";

    private RelativeLayout galleryLayout;
    private EditText usernameEditText;
    private EditText motaEditText;
    private Button saveButton;

    private DBHelper dbHelper;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editing);

        // Initialize views
        galleryLayout = findViewById(R.id.gallery);
        usernameEditText = findViewById(R.id.username2);
        motaEditText = findViewById(R.id.Mota);
        saveButton = findViewById(R.id.button);

        // Get the user ID from previous activity
        userID = getIntent().getIntExtra(USER_ID, -1);

        // Initialize the DBHelper
        dbHelper = new DBHelper(this);

        // Set click listener for selecting image from gallery
        galleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Set click listener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        // Load user profile data
        loadUserProfile();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    private void loadUserProfile() {
        // Get the user profile data from the database based on the user ID
        Map<String, Object> user = dbHelper.getUserByID(userID);
        if (user != null) {
            String username = (String) user.get("username");
            String mota = (String) user.get("mota");

            // Set the username and mota in the EditText fields
            usernameEditText.setText(username);
            motaEditText.setText(mota);
        }
    }

    private void saveProfile() {
        String username = usernameEditText.getText().toString().trim();
        String mota = motaEditText.getText().toString().trim();

        // Validate input fields
        if (username.isEmpty() || mota.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the user profile in the database
        boolean success = dbHelper.updateUser(userID, username, mota);
        if (success) {
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();

            // Redirect to ProfileActivity
            Intent intent = new Intent(ProfileEditingActivity.this, ProfileActivity.class);
            intent.putExtra(USER_ID, userID);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();

            // Convert the selected image to byte array
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageBytes = stream.toByteArray();

            // Save the selected image to the database
            dbHelper.updateUserImage(userID, imageBytes);
        }
    }
}
