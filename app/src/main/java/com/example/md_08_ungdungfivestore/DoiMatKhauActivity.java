package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.ChangePasswordRequest;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoiMatKhauActivity extends AppCompatActivity {

    private EditText edtMatKhauCu, edtMatKhauMoi, edtXacNhanMatKhauMoi;
    private Button btnXacNhan;
    private ImageButton quayLaiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau);

        // Quan trọng: Khởi tạo ApiClient để lấy context (dùng cho TokenManager)
        ApiClient.init(this);

        // Ánh xạ view
        edtMatKhauCu = findViewById(R.id.edtMatKhauCu);
        edtMatKhauMoi = findViewById(R.id.edtMatKhauMoi);
        edtXacNhanMatKhauMoi = findViewById(R.id.edtXacNhanMatKhauMoi);
        btnXacNhan = findViewById(R.id.btnXacNhanDoiMatKhau);
        quayLaiBtn = findViewById(R.id.quayLaiBtn);

        quayLaiBtn.setOnClickListener(v -> finish());

        btnXacNhan.setOnClickListener(v -> {
            String oldPass = edtMatKhauCu.getText().toString().trim();
            String newPass = edtMatKhauMoi.getText().toString().trim();
            String confirmPass = edtXacNhanMatKhauMoi.getText().toString().trim();

            if (validateInput(oldPass, newPass, confirmPass)) {
                doiMatKhauAPI(oldPass, newPass);
            }
        });
    }

    private boolean validateInput(String oldPass, String newPass, String confirmPass) {
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPass.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới phải ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doiMatKhauAPI(String oldPass, String newPass) {
        // Lấy service từ ApiClient
        ApiService apiService = ApiClient.getAuthService();

        // Tạo object request
        ChangePasswordRequest request = new ChangePasswordRequest(oldPass, newPass);

        // Gọi API
        Call<ApiResponse> call = apiService.doiMatKhau(request);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                // Trong hàm onResponse
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(DoiMatKhauActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                        // --- THÊM ĐOẠN NÀY ĐỂ XÓA DỮ LIỆU ĐÃ NHẬP ---
                        edtMatKhauCu.setText("");
                        edtMatKhauMoi.setText("");
                        edtXacNhanMatKhauMoi.setText("");

                        // Bỏ focus (để con trỏ nhấp nháy không còn nằm ở ô nhập liệu)
                        edtMatKhauCu.clearFocus();
                        edtMatKhauMoi.clearFocus();
                        edtXacNhanMatKhauMoi.clearFocus();
                        // --------------------------------------------

                        // Nếu bạn muốn tự động quay về màn hình cũ thì giữ lại lệnh finish()
                        // Nếu muốn ở lại màn hình này để đổi tiếp thì XÓA lệnh finish() đi
                        finish();
                    } else {
                        String msg = response.body().getMessage();
                        Toast.makeText(DoiMatKhauActivity.this, msg != null ? msg : "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }


                @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(DoiMatKhauActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
