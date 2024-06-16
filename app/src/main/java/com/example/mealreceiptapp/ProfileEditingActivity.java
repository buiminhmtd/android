package com.example.mealreceiptapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ProfileEditingActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText motaEditText;
    private ImageView galleryImageView;
    private Uri selectedImageUri;
    private DBHelper dbHelper;

    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editing);

        dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();
        long userID = getCurrentUserID(db);

        usernameEditText = findViewById(R.id.username2);
        motaEditText = findViewById(R.id.Mota);
        galleryImageView = findViewById(R.id.vector); // Assuming this is the ImageView inside RelativeLayout android:id="@+id/gallery"


        galleryImageView.setOnClickListener(v -> onSelectImageFromGallery(galleryImageView));

        Button saveButton = findViewById(R.id.button);

        saveButton.setOnClickListener(v -> saveProfileChanges(userID));
    }

    private void onSelectImageFromGallery(ImageView imageView) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, imageView.getId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    if (inputStream != null) {
                        galleryImageView.setImageURI(selectedImageUri);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to open image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void saveProfileChanges(long userID) {
        String username = usernameEditText.getText().toString().trim();
        String mota = motaEditText.getText().toString().trim();

        if (username.isEmpty() || mota.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            byte[] imageBytes = uriToByteArray(selectedImageUri);
            byte[] compressedImageData = compressImage(imageBytes);
            if (imageBytes == null) {
                throw new IOException("Failed to read image data");
            }

            // Save profile changes to database
            boolean success = dbHelper.updateUserProfile(userID, username, mota, imageBytes);

            if (success) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileEditingActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] uriToByteArray(Uri uri) throws IOException {
        ContentResolver resolver = getContentResolver();
        InputStream inputStream = resolver.openInputStream(uri);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (inputStream != null) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            return outputStream.toByteArray();
        }
        return null;
    }
    private int getCurrentUserID(SQLiteDatabase db) {
        int userID = -1; // Default value, or any value that indicates no user ID is found

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
    private byte[] compressImage(byte[] imageData) {
        // Sử dụng thư viện nén ảnh (ví dụ: BitmapFactory hoặc thư viện nén khác)
        // Ví dụ sử dụng BitmapFactory để giảm kích thước ảnh
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream); // Nén ảnh JPEG với chất lượng 50%
        return outputStream.toByteArray();
    }
}
