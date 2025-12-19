package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.UpdateProfileRequest;
import com.example.md_08_ungdungfivestore.models.UserProfile;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.UserApiService;
import com.example.md_08_ungdungfivestore.utils.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManThongTinCaNhan extends AppCompatActivity {

    private ImageButton quayLaiBtn;
    private ImageView imgAvatar;
    private TextInputEditText edtName, edtEmail, edtPhone, edtAddress;
    private Button btnSave;

    private UserApiService userApiService;
    private UserProfile currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_ca_nhan);

        // Initialize API Service
        userApiService = ApiClient.getClient().create(UserApiService.class);

        anhXa();
        setupListeners();
        loadUserInfo();
    }

    private void anhXa() {
        quayLaiBtn = findViewById(R.id.quayLaiBtn);
        imgAvatar = findViewById(R.id.imgAvatar);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupListeners() {
        quayLaiBtn.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveUserInfo());
    }

    private void loadUserInfo() {
        userApiService.getCurrentUser().enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                Log.d("RES",response.toString());
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentUser = response.body().getData();
                    fillData(currentUser);
                } else {
                    Toast.makeText(ManThongTinCaNhan.this, "Không thể tải thông tin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                Toast.makeText(ManThongTinCaNhan.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillData(UserProfile user) {
        if (user == null) return;

        edtName.setText(user.getFull_name());
        edtEmail.setText(user.getEmail());
        edtPhone.setText(user.getPhone_number());
        edtAddress.setText(user.getAddress());

        if (user.getAvatar_url() != null && !user.getAvatar_url().isEmpty()) {
            String avatarUrl = user.getAvatar_url();
             if (!avatarUrl.startsWith("http")) {
                if (!avatarUrl.startsWith("/"))
                    avatarUrl = "/" + avatarUrl;
                avatarUrl = ApiClient.BASE_URL.replace("/api/", "") + avatarUrl;
            }
            Glide.with(this).load(avatarUrl).error(R.drawable.avatar_img).into(imgAvatar);
        }
    }

    private void saveUserInfo() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        // Validate họ tên
        if (name.isEmpty()) {
            edtName.setError("Vui lòng nhập họ tên");
            return;
        }

        // Validate email
        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            return;
        }

        // Validate số điện thoại
        if (phone.isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            return;
        } else if (!phone.matches("^(0[0-9]{9})$")) {
            // Ví dụ: kiểm tra số bắt đầu bằng 0 và có 10 chữ số
            edtPhone.setError("Số điện thoại không hợp lệ bắt đầu bằng 0 và có 10 chữ số");
            return;
        }

        // Validate địa chỉ
        if (address.isEmpty()) {
            edtAddress.setError("Vui lòng nhập địa chỉ");
            return;
        }

        // Giữ thông tin cũ nếu không thay đổi
        String dob = currentUser != null ? currentUser.getDate_of_birth() : "";
        String gender = currentUser != null ? currentUser.getGender() : "0";
        String avatar = currentUser != null ? currentUser.getAvatar_url() : "";

        UpdateProfileRequest request = new UpdateProfileRequest(
                name,
                phone,
                dob,
                gender,
                avatar,
                address
        );

        userApiService.updateProfile(request).enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ManThongTinCaNhan.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    if (response.body().getData() != null) {
                        currentUser = response.body().getData();
                    }
                    finish();
                } else {
                    Toast.makeText(ManThongTinCaNhan.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                Toast.makeText(ManThongTinCaNhan.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
