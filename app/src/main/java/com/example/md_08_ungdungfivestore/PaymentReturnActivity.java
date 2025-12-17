package com.example.md_08_ungdungfivestore;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.OrderApiService;
import com.example.md_08_ungdungfivestore.models.ApiResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentReturnActivity extends AppCompatActivity {

    private OrderApiService orderApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_return);

        orderApiService = ApiClient.getOrderService();

        // Lấy URI từ intent
        Uri uri = getIntent().getData();
        if (uri != null) {
            // Lấy các query param từ VNPay
            String vnp_ResponseCode = uri.getQueryParameter("vnp_ResponseCode");
            String vnp_TxnRef = uri.getQueryParameter("vnp_TxnRef"); // Mã tham chiếu giao dịch
            String orderId = uri.getQueryParameter("orderId"); // Nếu bạn truyền orderId qua txnRef

            Log.d("VNPayCallback", "ResponseCode: " + vnp_ResponseCode + ", TxnRef: " + vnp_TxnRef);

            // Gọi backend để verify và cập nhật trạng thái đơn hàng
            verifyPaymentBackend(orderId);
        }
    }

    private void verifyPaymentBackend(String orderId) {
        if (orderId == null) {
            Toast.makeText(this, "Không xác định được đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        orderApiService.checkPaymentStatus(orderId).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getData();
                    Toast.makeText(PaymentReturnActivity.this,
                            "Trạng thái đơn hàng: " + status, Toast.LENGTH_LONG).show();
                    finish(); // quay lại màn hình trước
                } else {
                    Toast.makeText(PaymentReturnActivity.this,
                            "Không lấy được trạng thái thanh toán", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(PaymentReturnActivity.this,
                        "Lỗi kết nối server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
