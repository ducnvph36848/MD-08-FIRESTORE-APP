package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MatKhauSai extends AppCompatActivity {

    private ImageButton BtnQuayLaiSMK;
    private EditText edtEmail, edtMatKhau;
    private ImageView imgShowHidePassword;
    private TextView nutDangNhapSaiMKTextView, tvRegister, tvReset;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mat_khau_sai);

        initUI();

        //  Nút quay lại
        BtnQuayLaiSMK.setOnClickListener(v -> finish());

        //  Toggle show/hide password
        imgShowHidePassword.setOnClickListener(v -> togglePassword());

        //  Nhấn Sign In → quay về màn đăng nhập
        nutDangNhapSaiMKTextView.setOnClickListener(v -> {
            Intent i = new Intent(MatKhauSai.this, DangNhap.class);
            startActivity(i);
            finish();
        });

        // ✅ Register → sang màn đăng ký
        tvRegister.setOnClickListener(v -> {
            Intent i = new Intent(MatKhauSai.this, ManDangKy.class);
            startActivity(i);
        });

        // ✅ Reset password → sang màn gửi mã
        tvReset.setOnClickListener(v -> {
            Intent i = new Intent(MatKhauSai.this, GuiMaXacNhan.class);
            startActivity(i);
        });
    }

    private void initUI() {
        BtnQuayLaiSMK = findViewById(R.id.BtnQuayLaiSMK);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.matKhauDangNhapSaiMKTextInputEditText);
        imgShowHidePassword = findViewById(R.id.imgShowHidePassword);
        nutDangNhapSaiMKTextView = findViewById(R.id.nutDangNhapSaiMKTextView);
        tvRegister = findViewById(R.id.tvRegister);
        tvReset = findViewById(R.id.tvReset);
    }

    private void togglePassword() {
        if (isPasswordVisible) {
            edtMatKhau.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
            );
            imgShowHidePassword.setImageResource(R.drawable.mat); // mắt đóng
        } else {
            edtMatKhau.setInputType(
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
            imgShowHidePassword.setImageResource(R.drawable.mat); // mắt mở
        }

        edtMatKhau.setSelection(edtMatKhau.length());
        isPasswordVisible = !isPasswordVisible;
    }
}
