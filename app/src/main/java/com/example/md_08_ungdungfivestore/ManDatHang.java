package com.example.md_08_ungdungfivestore;

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
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.OrderApiService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManDatHang extends AppCompatActivity {
    private EditText diaChiTxt, hoTenKhachHangTxt, soDienThoaiTxt;
    private TextView tongSoTienTxt, nutThanhToanTxt;
    private ImageButton quayLaiBtn;
    private RadioButton thanhToanRadioBtn;
    private RecyclerView danhSachMuaRecyclerView;

    private ArrayList<CartItem> selectedItems;
    private OrderApiService orderApiService;
    private double subtotal = 0;
    private double shippingFee = 30000;
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

        // Initialize API
        orderApiService = ApiClient.getClient().create(OrderApiService.class);

        // Get selected items from intent
        selectedItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selectedItems");

        if (selectedItems == null || selectedItems.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm nào", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup RecyclerView
        danhSachMuaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        GioHangAdapter adapter = new GioHangAdapter(this, selectedItems, null);
        adapter.setCheckoutMode(true);
        danhSachMuaRecyclerView.setAdapter(adapter);

        // Calculate totals
        calculateTotals();

        quayLaiBtn.setOnClickListener(v -> finish());

        nutThanhToanTxt.setOnClickListener(v -> placeOrder());
    }

    private void anhXa() {
        diaChiTxt = findViewById(R.id.diaChiTxt);
        hoTenKhachHangTxt = findViewById(R.id.hoTenKhachHangTxt);
        soDienThoaiTxt = findViewById(R.id.soDienThoaiTxt);
        tongSoTienTxt = findViewById(R.id.tongSoTienTxt);
        nutThanhToanTxt = findViewById(R.id.nutThanhToanTxt);
        quayLaiBtn = findViewById(R.id.quayLaiBtn);
        thanhToanRadioBtn = findViewById(R.id.thanhToanRadioBtn);
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

    private void placeOrder() {
        // Collect shipping info
        String name = hoTenKhachHangTxt.getText().toString().trim();
        String phone = soDienThoaiTxt.getText().toString().trim();
        String addressStr = diaChiTxt.getText().toString().trim();

        // Validate họ tên
        if (name.isEmpty()) {
            hoTenKhachHangTxt.setError("Vui lòng nhập họ tên");
            hoTenKhachHangTxt.requestFocus();
            return;
        }
        if (name.matches(".*\\d.*")) {
            hoTenKhachHangTxt.setError("Họ tên không được chứa số");
            hoTenKhachHangTxt.requestFocus();
            return;
        }

        // Validate số điện thoại
        if (phone.isEmpty()) {
            soDienThoaiTxt.setError("Vui lòng nhập số điện thoại");
            soDienThoaiTxt.requestFocus();
            return;
        }
        if (!phone.matches("^(0|\\+84)[0-9]{9,10}$")) {
            soDienThoaiTxt.setError("Số điện thoại không hợp lệ");
            soDienThoaiTxt.requestFocus();
            return;
        }

        // Validate địa chỉ
        if (addressStr.isEmpty()) {
            diaChiTxt.setError("Vui lòng nhập địa chỉ");
            diaChiTxt.requestFocus();
            return;
        }
        if (addressStr.length() < 5) {
            diaChiTxt.setError("Địa chỉ quá ngắn !");
            diaChiTxt.requestFocus();
            return;
        }

        // Validate phương thức thanh toán
        if (!thanhToanRadioBtn.isChecked()) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert CartItem to OrderItemRequest
        List<CreateOrderRequest.OrderItemRequest> orderItems = new ArrayList<>();
        for (CartItem cartItem : selectedItems) {
            CreateOrderRequest.OrderItemRequest orderItem = new CreateOrderRequest.OrderItemRequest(
                    cartItem.getProduct_id(),
                    cartItem.getName(),
                    cartItem.getImage(),
                    cartItem.getSize(),
                    cartItem.getColor(),
                    cartItem.getQuantity(),
                    cartItem.getPrice());
            orderItems.add(orderItem);
        }

        // Create address
        Address address = new Address();
        address.setFull_name(name);
        address.setPhone_number(phone);
        address.setStreet(addressStr);

        // Create order request
        CreateOrderRequest request = new CreateOrderRequest(
                orderItems,
                address,
                shippingFee,
                total);

        // Disable button to prevent double click
        nutThanhToanTxt.setEnabled(false);
        nutThanhToanTxt.setText("Đang xử lý...");

        // Call API
        orderApiService.createCashOrder(request).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                nutThanhToanTxt.setEnabled(true);
                nutThanhToanTxt.setText("Thanh toán");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ManDatHang.this,
                            "Đặt hàng thành công!",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(ManDatHang.this, "Lỗi đặt hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                nutThanhToanTxt.setEnabled(true);
                nutThanhToanTxt.setText("Thanh toán");
                Log.d("THANH", t.getMessage());
                Toast.makeText(ManDatHang.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}