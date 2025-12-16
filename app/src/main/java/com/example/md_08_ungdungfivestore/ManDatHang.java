package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.adapters.GioHangAdapter;
import com.example.md_08_ungdungfivestore.models.Address;
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.CartItem;
import com.example.md_08_ungdungfivestore.models.CreateOrderRequest;
import com.example.md_08_ungdungfivestore.models.Order;
import com.example.md_08_ungdungfivestore.models.UserProfile;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.OrderApiService;
import com.example.md_08_ungdungfivestore.services.UserApiService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManDatHang extends AppCompatActivity {

    // UI
    private EditText diaChiTxt, hoTenKhachHangTxt, soDienThoaiTxt;
    private TextView tongSoTienTxt, nutThanhToanTxt;
    private ImageButton quayLaiBtn;
    private RadioButton thanhToanRadioBtn, thanhToanTruocRadioBtn;
    private RecyclerView danhSachMuaRecyclerView;

    // Data
    private ArrayList<CartItem> selectedItems;
    private OrderApiService orderApiService;
    private UserApiService userApiService;

    private double subtotal = 0;
    private final double shippingFee = 30000;
    private double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_dat_hang);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();

        orderApiService = ApiClient.getClient().create(OrderApiService.class);
        userApiService = ApiClient.getClient().create(UserApiService.class);

        // Load user profile to auto-fill
        loadUserProfile();

        selectedItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selectedItems");
        if (selectedItems == null || selectedItems.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm nào", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        danhSachMuaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        GioHangAdapter adapter = new GioHangAdapter(this, selectedItems, null);
        adapter.setCheckoutMode(true);
        danhSachMuaRecyclerView.setAdapter(adapter);

        calculateTotals();

        quayLaiBtn.setOnClickListener(v -> finish());

        nutThanhToanTxt.setOnClickListener(v -> {
            if (thanhToanTruocRadioBtn.isChecked()) {
                thanhToanVnPay();
            } else {
                placeOrder(); // COD
            }
        });
    }

    private void anhXa() {
        diaChiTxt = findViewById(R.id.diaChiTxt);
        hoTenKhachHangTxt = findViewById(R.id.hoTenKhachHangTxt);
        soDienThoaiTxt = findViewById(R.id.soDienThoaiTxt);
        tongSoTienTxt = findViewById(R.id.tongSoTienTxt);
        nutThanhToanTxt = findViewById(R.id.nutThanhToanTxt);
        quayLaiBtn = findViewById(R.id.quayLaiBtn);
        thanhToanRadioBtn = findViewById(R.id.thanhToanRadioBtn);
        thanhToanTruocRadioBtn = findViewById(R.id.thanhToanTruocRadioBtn);
        danhSachMuaRecyclerView = findViewById(R.id.danhSachMuaRecyclerView);
    }

    private void calculateTotals() {
        subtotal = 0;
        for (CartItem item : selectedItems) {
            subtotal += item.getSubtotal();
        }
        total = subtotal + shippingFee;

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tongSoTienTxt.setText(formatter.format(total) + " VND");
    }
    
    private void loadUserProfile() {
        userApiService.getCurrentUser().enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    UserProfile user = response.body().getData();
                    if (user != null) {
                        // Tự động điền thông tin nếu có
                        if (user.getFull_name() != null && !user.getFull_name().isEmpty()) {
                            hoTenKhachHangTxt.setText(user.getFull_name());
                        }
                        if (user.getPhone_number() != null && !user.getPhone_number().isEmpty()) {
                            soDienThoaiTxt.setText(user.getPhone_number());
                        }
                        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                            diaChiTxt.setText(user.getAddress());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                // Không hiển thị lỗi, chỉ log
                Log.e("ManDatHang", "Failed to load user profile: " + t.getMessage());
            }
        });
    }

    // ===================== COD =====================
    private void placeOrder() {
        if (!validateThongTinNguoiNhan()) return;

        List<CreateOrderRequest.OrderItemRequest> orderItems = new ArrayList<>();
        for (CartItem cartItem : selectedItems) {
            orderItems.add(new CreateOrderRequest.OrderItemRequest(
                    cartItem.getProduct_id(),
                    cartItem.getName(),
                    cartItem.getImage(),
                    cartItem.getSize(),
                    cartItem.getColor(),
                    cartItem.getQuantity(),
                    cartItem.getPrice()
            ));
        }

        Address address = new Address();
        address.setFull_name(hoTenKhachHangTxt.getText().toString().trim());
        address.setPhone_number(soDienThoaiTxt.getText().toString().trim());
        address.setStreet(diaChiTxt.getText().toString().trim());

        CreateOrderRequest request = new CreateOrderRequest(
                orderItems,
                address,
                shippingFee,
                total
        );

        nutThanhToanTxt.setEnabled(false);
        nutThanhToanTxt.setText("Đang xử lý...");

        orderApiService.createCashOrder(request).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                nutThanhToanTxt.setEnabled(true);
                nutThanhToanTxt.setText("Thanh toán");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ManDatHang.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    // Thông báo đặt hàng thành công để refresh danh sách sản phẩm
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ManDatHang.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                nutThanhToanTxt.setEnabled(true);
                nutThanhToanTxt.setText("Thanh toán");
                Toast.makeText(ManDatHang.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===================== VNPAY =====================
    private void thanhToanVnPay() {
        if (!validateThongTinNguoiNhan()) return;

        nutThanhToanTxt.setEnabled(false);
        nutThanhToanTxt.setText("Đang chuyển VNPAY...");

        Map<String, Integer> body = new HashMap<>();
        body.put("amount", (int) total);

        orderApiService.createVnPayPayment(body).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                nutThanhToanTxt.setEnabled(true);
                nutThanhToanTxt.setText("Thanh toán");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String paymentUrl = response.body().getData();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl)));
                } else {
                    Toast.makeText(ManDatHang.this, "Không tạo được thanh toán VNPAY", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                nutThanhToanTxt.setEnabled(true);
                nutThanhToanTxt.setText("Thanh toán");
                Log.e("VNPAY", t.getMessage());
                Toast.makeText(ManDatHang.this, "Lỗi kết nối VNPAY", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===================== VALIDATE =====================
    private boolean validateThongTinNguoiNhan() {
        String name = hoTenKhachHangTxt.getText().toString().trim();
        String phone = soDienThoaiTxt.getText().toString().trim();
        String address = diaChiTxt.getText().toString().trim();

        if (name.isEmpty() || name.matches(".*\\d.*")) {
            hoTenKhachHangTxt.setError("Họ tên không hợp lệ");
            return false;
        }

        if (!phone.matches("^(0|\\+84)[0-9]{9,10}$")) {
            soDienThoaiTxt.setError("Số điện thoại không hợp lệ");
            return false;
        }

        if (address.length() < 5) {
            diaChiTxt.setError("Địa chỉ quá ngắn");
            return false;
        }
        return true;
    }
}
