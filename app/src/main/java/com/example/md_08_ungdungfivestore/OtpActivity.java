package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.models.AuthResponse;
import com.example.md_08_ungdungfivestore.models.OtpRequest;
import com.example.md_08_ungdungfivestore.models.RegisterRequest;
import com.example.md_08_ungdungfivestore.models.RegisterResponse;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    EditText edtOtp;
    TextView btnVerify, btnMa; // btnMa là nút gửi lại OTP
    String email, fullName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // Nhận dữ liệu từ màn đăng ký
        email = getIntent().getStringExtra("email");
        fullName = getIntent().getStringExtra("full_name");
        password = getIntent().getStringExtra("password");

        edtOtp = findViewById(R.id.edtOtp);
        btnVerify = findViewById(R.id.btnVerify);
        btnMa = findViewById(R.id.btnMa); // nút gửi lại OTP

        // ====== XỬ LÝ NÚT QUAY LẠI ======
        ImageButton btnQuayLai = findViewById(R.id.quayLaiBtn);
        btnQuayLai.setOnClickListener(v -> {
            Intent intent = new Intent(OtpActivity.this, ManDangKy.class);
            startActivity(intent);
            finish();
        });

        // ====== XỬ LÝ NÚT XÁC MINH OTP ======
        btnVerify.setOnClickListener(v -> {
            String otp = edtOtp.getText().toString().trim();
            if (otp.isEmpty()) {
                Toast.makeText(this, "Nhập mã OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            
            boolean isForgot = getIntent().getBooleanExtra("isForgotPassword", false);
            if (isForgot) {
                 Intent intent = new Intent(OtpActivity.this, MatKhauMoi.class);
                 intent.putExtra("email", email);
                 intent.putExtra("otp", otp);
                 startActivity(intent);
                 finish();
            } else {
                verifyOtp(email, otp, fullName, password);
            }
        });

        // ====== XỬ LÝ NÚT GỬI LẠI OTP ======
        btnMa.setOnClickListener(v -> {
            if(email != null && fullName != null && password != null){
                taoOtp(email, fullName, password);
            } else {
                Toast.makeText(this, "Không có dữ liệu người dùng để gửi OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm xác thực OTP
    private void verifyOtp(String email, String otp, String fullName, String password) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        OtpRequest request = new OtpRequest(email, otp, fullName, password);

        Call<AuthResponse> call = apiService.verifyOtp(request);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OtpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OtpActivity.this, DangNhap.class));
                        finish();
                } else {
                    Toast.makeText(OtpActivity.this, "Xác thực OTP thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm gọi API tạo OTP (copy từ màn đăng ký)
    // Hàm gọi API tạo OTP (copy từ màn đăng ký)
    private void taoOtp(String email, String fullName, String password){
        if (getIntent().getBooleanExtra("isForgotPassword", false)) {
            // Resend for Forgot Password
            com.example.md_08_ungdungfivestore.models.OtpRequest request = new com.example.md_08_ungdungfivestore.models.OtpRequest(email, "", "", "");
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            apiService.forgotPassword(request).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful()) {
                         Toast.makeText(OtpActivity.this, "Đã gửi lại OTP", Toast.LENGTH_SHORT).show();
                    } else {
                         Toast.makeText(OtpActivity.this, "Gửi lại thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(OtpActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        RegisterRequest request = new RegisterRequest(fullName, email, password);

        Call<RegisterResponse> call = apiService.register(request);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    Toast.makeText(OtpActivity.this, "OTP đã được gửi. Kiểm tra console để lấy mã", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OtpActivity.this, "Gửi lại OTP thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
