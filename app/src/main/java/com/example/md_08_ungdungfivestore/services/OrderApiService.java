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

/**
 * API Service cho Đơn hàng
 */
public interface OrderApiService {

    /**
     * Đặt hàng thanh toán tiền mặt (COD)
     */
    @POST("api/orders/cash-order")
    Call<ApiResponse<Order>> createCashOrder(@Body CreateOrderRequest request);

    /**
     * Đặt hàng thanh toán VNPay
     * body: { "order_id": "123", "total": 100000, "orderInfo": "Thông tin đơn hàng", "ipAddr": "127.0.0.1" }
     */

    // Định nghĩa đã sửa lại (chính xác)

    @POST("api/vnpay/create-payment")
    Call<ApiResponse<String>> createVnPayPayment(@Body Map<String, Object> body);

    /**
     * Lấy danh sách đơn hàng của user (tất cả trạng thái)
     */
    @GET("api/orders/my-orders")
    Call<ApiResponse<List<Order>>> getMyOrders();

    /**
     * Lọc đơn hàng theo trạng thái
     * @param status pending, confirmed, processing, shipping, delivered, cancelled
     */
    @GET("api/orders/my-orders")
    Call<ApiResponse<List<Order>>> getMyOrdersByStatus(@Query("status") String status);

    /**
     * Lấy chi tiết đơn hàng theo ID
     */
    @GET("api/orders/{id}")
    Call<ApiResponse<Order>> getOrderById(@Path("id") String orderId);

    /**
     * Hủy đơn hàng (chỉ khi status = pending)
     */
    @PUT("api/orders/{id}/cancel")
    Call<ApiResponse<Order>> cancelOrder(@Path("id") String orderId);

    /**
     * Kiểm tra trạng thái thanh toán VNPay theo orderId
     */
    @GET("api/payment/{orderId}/status")
    Call<ApiResponse<String>> checkPaymentStatus(@Path("orderId") String orderId);
}
