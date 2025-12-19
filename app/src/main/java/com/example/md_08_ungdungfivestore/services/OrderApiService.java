package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.CreateOrderRequest;
import com.example.md_08_ungdungfivestore.models.Order;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * API Service cho Đơn hàng
 */
public interface OrderApiService {

    /**
     * ===================== COD =====================
     * Đặt hàng thanh toán tiền mặt
     */
    @POST("api/orders/cash-order")
    Call<ApiResponse<Order>> createCashOrder(
            @Body CreateOrderRequest request
    );

    /**
     * ===================== VNPAY =====================
     * Tạo URL thanh toán VNPay
     * body:
     * {
     * "order_id": "ORDER_123",
     * "total": 100000,
     * "orderInfo": "Thanh toán đơn hàng",
     * "ipAddr": "127.0.0.1"
     * }
     */
    @POST("api/vnpay/create-payment")
    Call<ApiResponse<String>> createVnPayPayment(
            @Body Map<String, Object> body
    );

    /**
     * Xác thực kết quả thanh toán từ VNPay (Return URL)
     * Mobile App sẽ gửi toàn bộ params nhận được từ Deep Link lên đây để Server verify checksum
     */
    @GET("api/vnpay/verify")
    Call<ApiResponse<Object>> verifyPayment(
            @QueryMap Map<String, String> params
    );

    /**
     * Kiểm tra trạng thái thanh toán VNPay
     * PAID | PENDING | FAILED
     */
    @GET("api/orders/{orderId}/payment-status")
    Call<ApiResponse<String>> checkPaymentStatus(
            @Path("orderId") String orderId
    );

    /**
     * ===================== ORDERS =====================
     * Lấy danh sách đơn hàng của user
     */
    @GET("api/orders/my-orders")
    Call<ApiResponse<List<Order>>> getMyOrders();

    /**
     * Lọc đơn hàng theo trạng thái
     */
    @GET("api/orders/my-orders")
    Call<ApiResponse<List<Order>>> getMyOrdersByStatus(
            @Query("status") String status
    );

    /**
     * Lấy chi tiết đơn hàng
     */
    @GET("api/orders/{id}")
    Call<ApiResponse<Order>> getOrderById(
            @Path("id") String orderId
    );

    /**
     * Hủy đơn hàng
     */
    @PUT("api/orders/{id}/cancel")
    Call<ApiResponse<Order>> cancelOrder(
            @Path("id") String orderId
    );

    @POST("api/orders/create-vnpay-payment") // <-- Nhờ backend tạo API endpoint này
    Call<ApiResponse<String>> createVnPayOrder(@Body CreateOrderRequest request);
}