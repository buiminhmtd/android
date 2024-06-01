package com.example.mealreceiptapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mealreceiptapp.DBHelper;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private CheckBox showPasswordCheckBox;
    private DBHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.textviewtaikhoan);
        passwordEditText = findViewById(R.id.textviewmatkhau);
        confirmPasswordEditText = findViewById(R.id.textviewmatkhau_confirm);
        showPasswordCheckBox = findViewById(R.id.checkbox_show_password);
        Button startButton = findViewById(R.id.start_button);
        TextView signInTextView = findViewById(R.id.dacotaikhoan);
        databaseHelper = new DBHelper(this);

        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confirmPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordEditText.setSelection(passwordEditText.length());
            confirmPasswordEditText.setSelection(confirmPasswordEditText.length());
        });

        startButton.setOnClickListener(v -> signUp());

        signInTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    private void signUp() {
        String username = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
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

        if (databaseHelper.addUser(username, password)) {
            showAlert("Đăng ký thành công", true);
        } else {
            showAlert("Đăng ký thất bại");
        }
    }

    private void showAlert(String message) {
        showAlert(message, false);
    }

    private void showAlert(String message, boolean isSuccess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (isSuccess) {
                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                    }
                })
                .show();
    }
}
