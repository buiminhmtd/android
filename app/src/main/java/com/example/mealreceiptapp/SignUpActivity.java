package com.example.mealreceiptapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, fullNameEditText, descriptionEditText;
    private CheckBox showPasswordCheckBox;
    private DBHelper databaseHelper;
    private ImageView profileImageView;
    private Bitmap profileImageBitmap; // To store the profile image as Bitmap

    private static final int REQUEST_IMAGE_PICK = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.textviewtaikhoan);
        passwordEditText = findViewById(R.id.textviewmatkhau);
        confirmPasswordEditText = findViewById(R.id.textviewmatkhau_confirm);
        fullNameEditText = findViewById(R.id.textview_ten_day_du);
        descriptionEditText = findViewById(R.id.textview_mota);
        showPasswordCheckBox = findViewById(R.id.checkbox_show_password);
        Button startButton = findViewById(R.id.start_button);
        TextView signInTextView = findViewById(R.id.dacotaikhoan);
        profileImageView = findViewById(R.id.profile_image_view);
        databaseHelper = new DBHelper(this);

        // Toggle password visibility
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int inputType = isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            passwordEditText.setInputType(inputType);
            confirmPasswordEditText.setInputType(inputType);
            // Move cursor to end of text
            passwordEditText.setSelection(passwordEditText.length());
            confirmPasswordEditText.setSelection(confirmPasswordEditText.length());
        });

        // Start sign up process
        startButton.setOnClickListener(v -> signUp());

        // Navigate to sign in activity
        signInTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        // Select profile image
        profileImageView.setOnClickListener(v -> selectImage());
    }

    private void signUp() {
        String username = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String fullName = fullNameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullName.isEmpty()) {
            showAlert("Vui lòng điền đầy đủ thông tin");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Mật khẩu không khớp");
            return;
        }

        if (databaseHelper.checkUser(username, password)) {
            showAlert("Người dùng đã tồn tại");
            return;
        }

        // Use default profile image if profileImageBitmap is null
        Bitmap defaultImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.feed_1);
        if (profileImageBitmap == null) {
            profileImageBitmap = defaultImageBitmap;
        }

        // Add user to database with profile image
        long result = databaseHelper.addUser(username, password, fullName, convertBitmapToByteArray(profileImageBitmap), description);
        if (result != -1) {
            showAlert("Đăng ký thành công", true);
        } else {
            showAlert("Đăng ký thất bại");
        }
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void showAlert(String message, boolean isSuccess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (isSuccess) {
                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish(); // Close SignUpActivity after navigating to SignInActivity
                    }
                })
                .show();
    }

    private void showAlert(String message) {
        showAlert(message, false);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            try {
                profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                profileImageView.setImageBitmap(profileImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
