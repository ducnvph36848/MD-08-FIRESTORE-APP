package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CaiDatActivity extends AppCompatActivity {

    private ImageButton quayLaiBtn;
    private LinearLayout donHangLayout, theNganHangLayout, thongTinCaNhanLayout, lienHeLayout, dangXuatLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cai_dat); // XML bạn gửi

        // 1. Ánh xạ view
        quayLaiBtn = findViewById(R.id.quayLaiBtn);
        donHangLayout = findViewById(R.id.donHangLayout);
        theNganHangLayout = findViewById(R.id.theNganHangLayout);
        thongTinCaNhanLayout = findViewById(R.id.thongTinCaNhanLayout);
        lienHeLayout = findViewById(R.id.lienHeLayout);
        dangXuatLayout = findViewById(R.id.dangXuatLayout);

        // 2. Nút quay lại
        quayLaiBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // 3. Chuyển sang màn Đơn hàng
        donHangLayout.setOnClickListener(v -> {
            Intent i = new Intent(CaiDatActivity.this, ManDonHang.class);
            startActivity(i);
        });

        // 4. Đổi Mật Khẩu
        theNganHangLayout.setOnClickListener(v ->{
            Intent i = new Intent(CaiDatActivity.this,DoiMatKhauActivity.class);
            startActivity(i);
        });

        // 5. Mở trang cá nhân
        thongTinCaNhanLayout.setOnClickListener(v -> {
            Intent i = new Intent(CaiDatActivity.this, ManThongTinCaNhan.class);
            startActivity(i);
        });

        // 6. Mở màn liên hệ
        lienHeLayout.setOnClickListener(v -> {
            Intent i = new Intent(CaiDatActivity.this, ManLienHe.class);
            startActivity(i);
        });

        // 7. Đăng xuất (giả lập)
        dangXuatLayout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    // Giả lập đăng xuất
                    Toast.makeText(this, "Đăng xuất thành công (giả lập)", Toast.LENGTH_SHORT).show();
                    // Quay lại màn hình đăng nhập
                    Intent i = new Intent(this, DangNhap.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                })
                .setNegativeButton("Không", null)
                .show();
    }
}
