package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.models.AuthResponse;
import com.example.md_08_ungdungfivestore.models.LoginRequest;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.ApiService;
import com.example.md_08_ungdungfivestore.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DangNhap extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private TextView btnLogin, tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        edtEmail = findViewById(R.id.edtEmailDangNhap);
        edtPassword = findViewById(R.id.matKhauDangNhapTextInputEditText);
        btnLogin = findViewById(R.id.nutDangnhapvSignInTextView);
        tvRegister = findViewById(R.id.tvRegisterDangNhap);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty()) {
                edtEmail.setError("Vui lòng nhập email");
                edtEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                edtPassword.setError("Vui lòng nhập mật khẩu");
                edtPassword.requestFocus();
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                edtEmail.setError("Email không hợp lệ gồm có ( @,. )");
                edtEmail.requestFocus();
                return;
            }

            if (password.length() < 6) {
                edtPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
                edtPassword.requestFocus();
                return;
            }

            if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
                edtPassword.setError("Mật khẩu phải bao gồm cả chữ và số");
                edtPassword.requestFocus();
                return;
            }

            loginUser(email, password);
        });

        tvRegister.setOnClickListener(v -> startActivity(new Intent(DangNhap.this, ManDangKy.class)));

        findViewById(R.id.tvResetDangNhap).setOnClickListener(v ->
                startActivity(new Intent(DangNhap.this, GuiMaXacNhan.class))
        );
    }

    private void loginUser(String email, String password) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        LoginRequest request = new LoginRequest(email, password);

        Call<AuthResponse> call = apiService.login(request);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                Log.d("DangNhap", "Response code: " + response.code());

                if (response.code() == 200 && response.body() != null) {
                    AuthResponse auth = response.body();
                    Log.d("DangNhap", "Success: " + auth.isSuccess() + ", Message: " + auth.getMessage());

                    TokenManager tokenManager = new TokenManager(DangNhap.this);

                    // Sau khi login thành công
                    if (auth.isSuccess()) {
                        tokenManager.saveToken(auth.getToken());
                        if (auth.getUser() != null && auth.getUser().getId() != null) {
                            tokenManager.saveUserId(auth.getUser().getId());
                        }

                        Toast.makeText(DangNhap.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        // Chuyển sang MainActivity
                        Intent intent = new Intent(DangNhap.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(DangNhap.this, auth.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("DangNhap", "Response thất bại: " + response.code());
                    Toast.makeText(DangNhap.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("DangNhap", "Lỗi network: " + t.getMessage());
                Toast.makeText(DangNhap.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
