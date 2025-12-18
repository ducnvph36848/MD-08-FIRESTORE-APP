package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.md_08_ungdungfivestore.models.AuthResponse;
import com.example.md_08_ungdungfivestore.models.OtpRequest;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatKhauMoi extends AppCompatActivity {

    private TextInputEditText edtMatKhauMoi;
    private TextView nutXacNhanMatKhauMoiTextView;
    private ImageButton btnBackMatKhauMoi;
    private String email, otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mat_khau_moi);

        // Xử lý EdgeToEdge cho giao diện tràn viền
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Ánh xạ View
        edtMatKhauMoi = findViewById(R.id.edtMatKhauMoi);
        nutXacNhanMatKhauMoiTextView = findViewById(R.id.nutXacNhanMatKhauMoiTextView);
        btnBackMatKhauMoi = findViewById(R.id.btnBackMatKhauMoi);

        // 2. Nhận và kiểm tra dữ liệu từ màn hình trước
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        otp = intent.getStringExtra("otp");

        // Nếu không có email hoặc otp (truy cập trái phép hoặc lỗi), đóng màn hình
        if (email == null || otp == null) {
            Toast.makeText(this, "Lỗi: Thiếu thông tin xác thực!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 3. Sự kiện click
        nutXacNhanMatKhauMoiTextView.setOnClickListener(v -> handleResetPassword());
        btnBackMatKhauMoi.setOnClickListener(v -> finish());
    }

    private void handleResetPassword() {
        String newPassword = edtMatKhauMoi.getText().toString().trim();

        // ===== Validate dữ liệu =====
        if (newPassword.isEmpty()) {
            edtMatKhauMoi.setError("Vui lòng nhập mật khẩu mới");
            edtMatKhauMoi.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            edtMatKhauMoi.setError("Mật khẩu phải từ 6 ký tự trở lên");
            edtMatKhauMoi.requestFocus();
            return;
        }

        // Regex: Ít nhất 1 chữ cái và 1 số
        if (!newPassword.matches(".*[A-Za-z].*") || !newPassword.matches(".*\\d.*")) {
            edtMatKhauMoi.setError("Mật khẩu phải bao gồm cả chữ và số");
            edtMatKhauMoi.requestFocus();
            return;
        }

        // ===== Bắt đầu gọi API =====
        setLoading(true); // Khóa nút bấm

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Tạo request (Lưu ý: tham số thứ 3 là oldPassword, ở đây để rỗng "")
        OtpRequest request = new OtpRequest(email, otp, "", newPassword);

        apiService.resetPassword(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoading(false); // Mở lại nút bấm

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        // Thành công
                        Toast.makeText(MatKhauMoi.this, "Đổi mật khẩu thành công! Hãy đăng nhập lại.", Toast.LENGTH_SHORT).show();

                        // Chuyển về trang Đăng Nhập và xóa lịch sử Back Stack
                        Intent intent = new Intent(MatKhauMoi.this, DangNhap.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Server trả về 200 nhưng logic thất bại (ví dụ: OTP hết hạn)
                        String msg = response.body().getMessage();
                        Toast.makeText(MatKhauMoi.this, msg != null ? msg : "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Lỗi từ server (404, 500...)
                    Toast.makeText(MatKhauMoi.this, "Lỗi hệ thống: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(MatKhauMoi.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm phụ trợ để khóa/mở nút bấm
    private void setLoading(boolean isLoading) {
        if (nutXacNhanMatKhauMoiTextView != null) {
            nutXacNhanMatKhauMoiTextView.setEnabled(!isLoading);
            if (isLoading) {
                nutXacNhanMatKhauMoiTextView.setText("Đang xử lý...");
                nutXacNhanMatKhauMoiTextView.setAlpha(0.7f); // Làm mờ nút
            } else {
                nutXacNhanMatKhauMoiTextView.setText("Xác nhận");
                nutXacNhanMatKhauMoiTextView.setAlpha(1.0f);
            }
        }
    }
}
