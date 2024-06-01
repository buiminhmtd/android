package com.example.mealreceiptapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mealreceiptapp.DBHelper;
import android.widget.TextView;

public class SignInActivity extends AppCompatActivity {

    private EditText passwordEditText;
    private EditText taiKhoanEditText;
    private CheckBox showPasswordCheckBox;
    private DBHelper DBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Ánh xạ các thành phần giao diện
        passwordEditText = findViewById(R.id.textviewmatkhau);
        showPasswordCheckBox = findViewById(R.id.checkbox_show_password);
        taiKhoanEditText = findViewById(R.id.textviewtaikhoan);
        Button startButton = findViewById(R.id.start_button);
        TextView signUpTextView = findViewById(R.id.chuacotaikhoan);
        DBHelper = new DBHelper(this);

        // Thiết lập sự kiện cho CheckBox hiển thị mật khẩu
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordEditText.setSelection(passwordEditText.length());
        });

        // Đặt lắng nghe sự kiện onClick cho nút bắt đầu
        startButton.setOnClickListener(v -> signIn());

        // Đặt lắng nghe sự kiện onClick cho TextView đăng ký
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    // Phương thức đăng nhập
    private void signIn() {
        String username = taiKhoanEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Thông báo", "Vui lòng điền tài khoản và mật khẩu");
            return;
        }

        boolean result = DBHelper.checkUser(username, password);
        if (result) {
            showAlert("Thông báo", "Đăng nhập thành công");
            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Kết thúc SignInActivity để người dùng không thể quay lại màn hình đăng nhập
        } else {
            showAlert("Thông báo", "Đăng nhập thất bại");
        }
    }

    // Phương thức hiển thị hộp thoại thông báo
    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
