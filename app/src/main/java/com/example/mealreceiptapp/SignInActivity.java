package com.example.mealreceiptapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mealreceiptapp.R;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khai báo và ánh xạ nút bắt đầu
        Button startButton = findViewById(R.id.start_button);

        // Đặt lắng nghe sự kiện onClick cho nút bắt đầu
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ EditText tài khoản và mật khẩu
                EditText taiKhoanEditText = findViewById(R.id.textviewtaikhoan);
                EditText matKhauEditText = findViewById(R.id.textviewmatkhau);
                String taiKhoan = taiKhoanEditText.getText().toString();
                String matKhau = matKhauEditText.getText().toString();

                // Kiểm tra xem liệu tài khoản và mật khẩu có được nhập hay không
                if (taiKhoan.isEmpty() || matKhau.isEmpty()) {
                    // Hiển thị Toast yêu cầu điền tài khoản và mật khẩu
                    Toast.makeText(getApplicationContext(), "Vui lòng điền tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
                    return; // Kết thúc phương thức onClick
                }

                // Nếu tài khoản và mật khẩu được điền, tiếp tục xử lý đăng nhập
                // Có thể thêm mã xử lý đăng nhập ở đây
            }
        });
    }
}
