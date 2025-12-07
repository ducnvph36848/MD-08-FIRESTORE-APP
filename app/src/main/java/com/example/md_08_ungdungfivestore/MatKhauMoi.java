package com.example.md_08_ungdungfivestore;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MatKhauMoi extends AppCompatActivity {

    private com.google.android.material.textfield.TextInputEditText edtMatKhauMoi;
    private android.widget.TextView nutXacNhanMatKhauMoiTextView;
    private android.widget.ImageButton btnBackMatKhauMoi;
    private String email, otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mat_khau_moi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtMatKhauMoi = findViewById(R.id.edtMatKhauMoi);
        nutXacNhanMatKhauMoiTextView = findViewById(R.id.nutXacNhanMatKhauMoiTextView);
        btnBackMatKhauMoi = findViewById(R.id.btnBackMatKhauMoi);

        email = getIntent().getStringExtra("email");
        otp = getIntent().getStringExtra("otp");

        nutXacNhanMatKhauMoiTextView.setOnClickListener(v -> handleResetPassword());
        btnBackMatKhauMoi.setOnClickListener(v -> finish());
    }

    private void handleResetPassword() {
        String newPassword = edtMatKhauMoi.getText().toString().trim();

        // ===== Kiểm tra rỗng =====
        if (newPassword.isEmpty()) {
            edtMatKhauMoi.setError("Vui lòng nhập mật khẩu mới");
            edtMatKhauMoi.requestFocus();
            return;
        }

        // ===== Kiểm tra độ dài =====
        if (newPassword.length() < 6) {
            edtMatKhauMoi.setError("Mật khẩu phải từ 6 ký tự trở lên");
            edtMatKhauMoi.requestFocus();
            return;
        }

        // ===== Kiểm tra chữ và số =====
        if (!newPassword.matches(".*[A-Za-z].*") || !newPassword.matches(".*\\d.*")) {
            edtMatKhauMoi.setError("Mật khẩu phải bao gồm cả chữ và số");
            edtMatKhauMoi.requestFocus();
            return;
        }

        // ===== (Tuỳ chọn) Kiểm tra ký tự đặc biệt =====
//        if (!newPassword.matches(".*[!@#$%^&*()_+=<>?].*")) {
//            edtMatKhauMoi.setError("Mật khẩu nên có ít nhất 1 ký tự đặc biệt");
//            edtMatKhauMoi.requestFocus();
//            return;
//        }

        // Nếu hợp lệ, gọi API reset password
        com.example.md_08_ungdungfivestore.services.ApiService apiService =
                com.example.md_08_ungdungfivestore.services.ApiClient.getClient().create(com.example.md_08_ungdungfivestore.services.ApiService.class);

        com.example.md_08_ungdungfivestore.models.OtpRequest request =
                new com.example.md_08_ungdungfivestore.models.OtpRequest(email, otp, "", newPassword);

        apiService.resetPassword(request).enqueue(new retrofit2.Callback<com.example.md_08_ungdungfivestore.models.AuthResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.md_08_ungdungfivestore.models.AuthResponse> call,
                                   retrofit2.Response<com.example.md_08_ungdungfivestore.models.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    android.widget.Toast.makeText(MatKhauMoi.this, "Đổi mật khẩu thành công", android.widget.Toast.LENGTH_SHORT).show();
                    android.content.Intent intent = new android.content.Intent(MatKhauMoi.this, DangNhap.class);
                    intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    android.widget.Toast.makeText(MatKhauMoi.this, "Đổi mật khẩu thất bại", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.md_08_ungdungfivestore.models.AuthResponse> call, Throwable t) {
                android.widget.Toast.makeText(MatKhauMoi.this, "Lỗi mạng: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

}