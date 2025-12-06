package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GuiMaXacNhan extends AppCompatActivity {


    private TextView nutXacNhanTextView;
    private android.widget.EditText edtEmailForgot; // Use full class name or add import
    private android.widget.ImageButton btnBackMaXacNhan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gui_ma_xac_nhan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtEmailForgot = findViewById(R.id.edtEmailForgot);
        nutXacNhanTextView = findViewById(R.id.nutXacNhanTextView);
        btnBackMaXacNhan = findViewById(R.id.btnBackMaXacNhan);

        nutXacNhanTextView.setOnClickListener(v -> handleSendOtp());
        btnBackMaXacNhan.setOnClickListener(v -> finish());
    }

    private void handleSendOtp() {
        String email = edtEmailForgot.getText().toString().trim();
        if (email.isEmpty()) {
            android.widget.Toast.makeText(this, "Vui lòng nhập email", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        com.example.md_08_ungdungfivestore.services.ApiService apiService = 
            com.example.md_08_ungdungfivestore.services.ApiClient.getClient().create(com.example.md_08_ungdungfivestore.services.ApiService.class);
        
        // Ensure OtpRequest class has constructor or setter for email
        // We use OtpRequest(email, otp, ...) but here only email matters.
        // Assuming OtpRequest has a constructor that works or we can set email.
        // If OtpRequest is strictly for verification (email+otp), we might need to check if we can reuse it
        // or just pass email to a param if generic.
        // Let's assume OtpRequest matches what we defined in backend (req.body.email)
        com.example.md_08_ungdungfivestore.models.OtpRequest request = new com.example.md_08_ungdungfivestore.models.OtpRequest(email, "", "", ""); 

        apiService.forgotPassword(request).enqueue(new retrofit2.Callback<com.example.md_08_ungdungfivestore.models.AuthResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.md_08_ungdungfivestore.models.AuthResponse> call, retrofit2.Response<com.example.md_08_ungdungfivestore.models.AuthResponse> response) {
                if (response.isSuccessful()) {
                    android.widget.Toast.makeText(GuiMaXacNhan.this, "Mã OTP đã được gửi", android.widget.Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GuiMaXacNhan.this, OtpActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("isForgotPassword", true);
                    startActivity(intent);
                    finish();
                } else {
                    android.widget.Toast.makeText(GuiMaXacNhan.this, "Gửi thất bại: " + response.message(), android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.md_08_ungdungfivestore.models.AuthResponse> call, Throwable t) {
                android.widget.Toast.makeText(GuiMaXacNhan.this, "Lỗi mạng", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}