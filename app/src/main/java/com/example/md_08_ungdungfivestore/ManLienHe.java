package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.fragments.TrangCaNhanFragment;

public class ManLienHe extends AppCompatActivity {

    private EditText edtHoTen, edtSDT, edtEmail, edtDiaChi, edtNoiDung;
    private Button btnGui;
    private TextView tvPhone, tvEmail, tvAddress;

    // Thông tin liên hệ hiển thị dưới cùng
    private static final String PHONE_NUMBER = "0909999888";
    private static final String EMAIL_TO     = "lienhe@quancaphe.com";
    private static final String ADDRESS_TXT  = "123 Lê Lợi, Quận 1, TP.HCM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lien_he); // Đổi đúng tên file layout XML của bạn

        // Ánh xạ view
        ImageButton btnBack = findViewById(R.id.btnBack);
        edtHoTen   = findViewById(R.id.edtHoTen);
        edtSDT     = findViewById(R.id.edtSDT);
        edtEmail   = findViewById(R.id.edtEmail);
        edtDiaChi  = findViewById(R.id.edtDiaChi);
        edtNoiDung = findViewById(R.id.edtNoiDung);
        btnGui     = findViewById(R.id.btnGui);

        tvPhone   = findViewById(R.id.tvPhone);
        tvEmail   = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);

        // NÚT QUAY LẠI -> về ManCaiDat
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ManLienHe.this, TrangCaNhanFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            // (tuỳ chọn) hiệu ứng chuyển trang nếu có file anim
            // overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish(); // đóng trang Liên hệ
        });

        // NÚT GỬI (chưa cần backend)
        btnGui.setOnClickListener(v -> {
            if (!validate()) return;
            Toast.makeText(this, "Gửi liên hệ thành công! Cảm ơn bạn.", Toast.LENGTH_SHORT).show();
            clearForm();
        });

        // Chạm để gọi điện
        tvPhone.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + PHONE_NUMBER));
            startActivity(callIntent);
        });

        // Chạm để mở email
        tvEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + EMAIL_TO));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Liên hệ từ ứng dụng");
            emailIntent.putExtra(Intent.EXTRA_TEXT,
                    "Họ tên: " + edtHoTen.getText().toString().trim() + "\n" +
                            "SĐT: " + edtSDT.getText().toString().trim() + "\n" +
                            "Email: " + edtEmail.getText().toString().trim() + "\n" +
                            "Địa chỉ: " + edtDiaChi.getText().toString().trim() + "\n" +
                            "Nội dung: " + edtNoiDung.getText().toString().trim());
            startActivity(Intent.createChooser(emailIntent, "Chọn ứng dụng Email"));
        });

        // Chạm để mở Google Maps
        tvAddress.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(ADDRESS_TXT));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });
    }

    // ====== Helpers ======
    private boolean validate() {
        String hoTen = edtHoTen.getText().toString().trim();
        String sdt   = edtSDT.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String nd    = edtNoiDung.getText().toString().trim();

        if (hoTen.isEmpty()) {
            edtHoTen.setError("Vui lòng nhập họ tên");
            edtHoTen.requestFocus();
            return false;
        }

        if (sdt.isEmpty() || !sdt.matches("^(0|\\+84)[0-9]{9,10}$")) {
            edtSDT.setError("Số điện thoại không hợp lệ");
            edtSDT.requestFocus();
            return false;
        }

        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return false;
        }

        if (nd.length() < 10) {
            edtNoiDung.setError("Nội dung tối thiểu 10 ký tự");
            edtNoiDung.requestFocus();
            return false;
        }

        return true;
    }

    private void clearForm() {
        edtHoTen.setText("");
        edtSDT.setText("");
        edtEmail.setText("");
        edtDiaChi.setText("");
        edtNoiDung.setText("");
        edtHoTen.clearFocus();
        edtNoiDung.clearFocus();
    }
}
