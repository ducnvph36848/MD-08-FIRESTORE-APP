package com.example.md_08_ungdungfivestore;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.Order;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.OrderApiService;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManThongTinDonHang extends AppCompatActivity {
    private TextView thoiGianDuKienTextView, tenNguoiNhanTextView, diaChiTextView;
    private TextView tongTienSanPhamTextView, tenSanPhamTextView;
    private TextView huyDonHangButton, lienHeShopButton;
    private ImageView anhDonHangImageView;
    private ImageButton quayLaiBtn;

    private OrderApiService orderApiService;
    private String orderId;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_thong_tin_don_hang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();

        orderId = getIntent().getStringExtra("orderId");
        if (orderId == null) {
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderApiService = ApiClient.getClient().create(OrderApiService.class);
        loadOrderDetails();

        quayLaiBtn.setOnClickListener(v -> finish());

        lienHeShopButton.setOnClickListener(v -> {
            Intent lienHeIntent = new Intent(ManThongTinDonHang.this, ManLienHe.class);
            startActivity(lienHeIntent);
        });

        huyDonHangButton.setOnClickListener(v -> showCancelConfirmation());
    }

    private void anhXa() {
        thoiGianDuKienTextView = findViewById(R.id.thoiGianDuKienTextView);
        tenNguoiNhanTextView = findViewById(R.id.tenNguoiNhanTextView);
        diaChiTextView = findViewById(R.id.diaChiTextView);
        tongTienSanPhamTextView = findViewById(R.id.tongTienSanPhamTextView);
        huyDonHangButton = findViewById(R.id.huyDonHangButton);
        lienHeShopButton = findViewById(R.id.lienHeShopButton);
        quayLaiBtn = findViewById(R.id.quayLaiBtn);
        tenSanPhamTextView = findViewById(R.id.tenSanPhamTextView);
        anhDonHangImageView = findViewById(R.id.anhDonHangImageView);
    }

    private void loadOrderDetails() {
        orderApiService.getOrderById(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentOrder = response.body().getData();
                    updateUI();
                } else {
                    Toast.makeText(ManThongTinDonHang.this, "Lỗi tải thông tin đơn hàng", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                Toast.makeText(ManThongTinDonHang.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateUI() {
        if (currentOrder == null)
            return;

        // Address
        if (currentOrder.getAddress() != null) {
            tenNguoiNhanTextView.setText(
                    currentOrder.getAddress().getFull_name() + " | " + currentOrder.getAddress().getPhone_number());
            diaChiTextView.setText(currentOrder.getAddress().getStreet());
        }

        // Total
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tongTienSanPhamTextView.setText(formatter.format(currentOrder.getTotal_amount()) + " VND");

        // Status & Buttons
        if ("pending".equals(currentOrder.getStatus())) {
            huyDonHangButton.setVisibility(View.VISIBLE);
            thoiGianDuKienTextView.setText("Đang chờ xác nhận");
        } else if ("cancelled".equals(currentOrder.getStatus())) {
            huyDonHangButton.setVisibility(View.GONE);
            thoiGianDuKienTextView.setText("Đã hủy");
        } else if ("delivered".equals(currentOrder.getStatus())) {
            huyDonHangButton.setVisibility(View.GONE);
            thoiGianDuKienTextView.setText("Đã giao hàng");
        } else {
            huyDonHangButton.setVisibility(View.GONE);
            thoiGianDuKienTextView.setText("Đang giao hàng");
        }

        // Items - Show first item details
        if (currentOrder.getItems() != null && !currentOrder.getItems().isEmpty()) {
            Order.OrderItem firstItem = currentOrder.getItems().get(0);

            if (firstItem.getProduct_id() != null) {
                tenSanPhamTextView.setText(firstItem.getProduct_id().getName() + " (x" + firstItem.getQuantity() + ")");
                String imageUrl = firstItem.getProduct_id().getImage();
                if (imageUrl != null && !imageUrl.startsWith("http")) {
                    imageUrl = ApiClient.BASE_URL2 + imageUrl;
                }
                Glide.with(this).load(imageUrl).into(anhDonHangImageView);
            } else {
                tenSanPhamTextView.setText(firstItem.getName() + " (x" + firstItem.getQuantity() + ")");
            }
        }
    }

    private void showCancelConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc muốn hủy đơn hàng này?")
                .setPositiveButton("Hủy đơn", (dialog, which) -> cancelOrder())
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void cancelOrder() {
        orderApiService.cancelOrder(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ManThongTinDonHang.this, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to list
                } else {
                    Toast.makeText(ManThongTinDonHang.this, "Không thể hủy đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                Toast.makeText(ManThongTinDonHang.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}