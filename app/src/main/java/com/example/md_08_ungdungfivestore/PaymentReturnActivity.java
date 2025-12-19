package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.OrderApiService;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentReturnActivity extends AppCompatActivity {

    private OrderApiService orderApiService;
    private TextView tvResult, tvMessage, tvOrderId;
    private ImageView imgStatus;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_return);

        // Ánh xạ View (tùy chỉnh theo layout XML của bạn)
        tvResult = findViewById(R.id.tvResult); // Text hiển thị "Thành công" / "Thất bại"
        tvMessage = findViewById(R.id.tvMessage); // Text hiển thị chi tiết lỗi
        tvOrderId = findViewById(R.id.tvOrderId);
        imgStatus = findViewById(R.id.imgStatus); // Ảnh tick xanh hoặc dấu x đỏ
        btnBack = findViewById(R.id.btnBack);

        orderApiService = ApiClient.getOrderService();

        // Lấy URI từ intent (Deep Link)
        Uri uri = getIntent().getData();
        if (uri != null) {
            Log.d("VNPayCallback", "Full URI: " + uri.toString());

            // 1. Thu thập TẤT CẢ tham số từ VNPay trả về vào 1 Map
            Map<String, String> vnpParams = new HashMap<>();
            for (String key : uri.getQueryParameterNames()) {
                String value = uri.getQueryParameter(key);
                vnpParams.put(key, value);
                Log.d("VNPayParam", key + " = " + value);
            }

            // Lấy orderId custom mà backend đã gắn vào returnUrl (nếu có)
            String orderId = uri.getQueryParameter("orderId");
            if(orderId != null) {
                tvOrderId.setText("Mã đơn: " + orderId);
            }

            // 2. Gọi Backend để verify (QUAN TRỌNG: Phải verify checksum trên server)
            verifyPaymentBackend(vnpParams);
        } else {
            handleFailure("Không nhận được dữ liệu trả về từ VNPay");
        }

        btnBack.setOnClickListener(v -> {
            // Quay về màn hình chính hoặc màn hình lịch sử đơn hàng
            Intent intent = new Intent(PaymentReturnActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void verifyPaymentBackend(Map<String, String> vnpParams) {
        // Lưu ý: Bạn cần thêm method verifyPayment(@QueryMap Map<String, String> params) vào OrderApiService
        orderApiService.verifyPayment(vnpParams).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();

                    // Kiểm tra field success từ backend trả về
                    if (apiResponse.isSuccess()) {
                        handleSuccess("Thanh toán thành công!");
                    } else {
                        handleFailure("Thanh toán thất bại: " + apiResponse.getMessage());
                    }
                } else {
                    handleFailure("Lỗi xác thực thanh toán với Server.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e("VerifyPayment", "Error: " + t.getMessage());
                handleFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void handleSuccess(String message) {
        tvResult.setText("THANH TOÁN THÀNH CÔNG");
        tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        tvMessage.setText(message);
        // imgStatus.setImageResource(R.drawable.ic_check_circle); // Set icon thành công
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void handleFailure(String message) {
        tvResult.setText("THANH TOÁN THẤT BẠI");
        tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        tvMessage.setText(message);
        // imgStatus.setImageResource(R.drawable.ic_error); // Set icon thất bại
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}