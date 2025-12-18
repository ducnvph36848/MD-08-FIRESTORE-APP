package com.example.md_08_ungdungfivestore.examples;

import android.content.Context;
import android.util.Log;

import com.example.md_08_ungdungfivestore.models.*;
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.services.*;
import com.example.md_08_ungdungfivestore.utils.OrderStatus;
import com.example.md_08_ungdungfivestore.utils.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Ví dụ sử dụng các API services
 * 
 * LƯU Ý: File này chỉ để tham khảo, không chạy trực tiếp
 * Copy các đoạn code vào Activity/Fragment của bạn để sử dụng
 */
public class ApiUsageExamples {

    private static final String TAG = "ApiUsageExamples";

    /**
     * BƯỚC 1: Khởi tạo ApiClient trong Application hoặc MainActivity
     */
    public void initializeApiClient(Context context) {
        // Gọi trong onCreate() của Application hoặc MainActivity
        ApiClient.init(context);
    }

    /**
     * BƯỚC 2: Lưu token sau khi login thành công
     */
    public void saveTokenAfterLogin(Context context, String token) {
        TokenManager tokenManager = new TokenManager(context);
        tokenManager.saveToken(token);
    }

    // ========================================
    // CART API EXAMPLES
    // ========================================

    /**
     * Lấy giỏ hàng
     */
    public void getCart() {
        CartApiService service = ApiClient.getClient().create(CartApiService.class);

        service.getCart().enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Cart> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Cart cart = apiResponse.getData();
                        Log.d(TAG, "Cart items: " + cart.getItems().size());
                        Log.d(TAG, "Total: " + cart.getTotal_amount());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public void addToCart(String productId, String name, String image,
            String size, String color, int quantity, double price) {
        CartApiService service = ApiClient.getClient().create(CartApiService.class);

        AddToCartRequest request = new AddToCartRequest(
                productId, name, image, size, color, quantity, price);

        service.addToCart(request).enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Added to cart successfully");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    /**
     * Cập nhật số lượng sản phẩm
     */
    public void updateCartItemQuantity(String cartItemId, int newQuantity) {
        CartApiService service = ApiClient.getClient().create(CartApiService.class);

        UpdateCartRequest request = new UpdateCartRequest(newQuantity);

        service.updateCartItem(cartItemId, request).enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Cart updated");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    // ========================================
    // ORDER API EXAMPLES
    // ========================================

    /**
     * Tạo đơn hàng COD
     */
    public void createCashOrder(List<CreateOrderRequest.OrderItemRequest> items,
            Address address, double shippingFee, double totalAmount) {
        OrderApiService service = ApiClient.getClient().create(OrderApiService.class);

        CreateOrderRequest request = new CreateOrderRequest(items, address, shippingFee, totalAmount);

        service.createCashOrder(request).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body().getData();
                    Log.d(TAG, "Order created: " + order.get_id());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    /**
     * Lấy danh sách đơn hàng theo trạng thái
     */
    public void getOrdersByStatus(String status) {
        OrderApiService service = ApiClient.getClient().create(OrderApiService.class);

        service.getMyOrdersByStatus(status).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body().getData();
                    Log.d(TAG, "Orders with status " + status + ": " + orders.size());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    /**
     * Hủy đơn hàng
     */
    public void cancelOrder(String orderId) {
        OrderApiService service = ApiClient.getClient().create(OrderApiService.class);

        service.cancelOrder(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Order cancelled");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    // ========================================
    // USER API EXAMPLES
    // ========================================

    /**
     * Lấy thông tin user
     */
    public void getCurrentUser() {
        UserApiService service = ApiClient.getClient().create(UserApiService.class);

        service.getCurrentUser().enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile user = response.body().getData();
                    Log.d(TAG, "User: " + user.getFull_name());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    /**
     * Cập nhật profile
     */
    public void updateProfile(String fullName, String phone, String dob, String gender, String avatar) {
        UserApiService service = ApiClient.getClient().create(UserApiService.class);

        UpdateProfileRequest request = new UpdateProfileRequest(fullName, phone, dob, gender, avatar);

        service.updateProfile(request).enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Profile updated");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    /**
     * Đăng xuất
     */
    public void logout(Context context) {
        // Option 1: Chỉ xóa token ở client (recommended)
        TokenManager tokenManager = new TokenManager(context);
        tokenManager.clearToken();

        // Option 2: Gọi API logout (nếu server có blacklist token)
        UserApiService service = ApiClient.getClient().create(UserApiService.class);
        service.logout().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                tokenManager.clearToken();
                Log.d(TAG, "Logged out");
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    // ========================================
    // ADDRESS API EXAMPLES
    // ========================================

    /**
     * Lấy danh sách địa chỉ
     */
    public void getAddresses() {
        AddressApiService service = ApiClient.getClient().create(AddressApiService.class);

        service.getAddresses().enqueue(new Callback<ApiResponse<List<Address>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Address>>> call,
                    Response<ApiResponse<List<Address>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Address> addresses = response.body().getData();
                    Log.d(TAG, "Addresses: " + addresses.size());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Address>>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    // ========================================
    // CONTACT API EXAMPLES
    // ========================================

    /**
     * Tạo cuộc trò chuyện mới với admin
     */
    public void createConversation(String adminId, String message) {
        ContactApiService service = ApiClient.getClient().create(ContactApiService.class);

        CreateConversationRequest request = new CreateConversationRequest(adminId, message);

        service.createConversation(request).enqueue(new Callback<ApiResponse<Conversation>>() {
            @Override
            public void onResponse(Call<ApiResponse<Conversation>> call, Response<ApiResponse<Conversation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Conversation conversation = response.body().getData();
                    Log.d(TAG, "Conversation created: " + conversation.get_id());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Conversation>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    /**
     * Gửi tin nhắn
     */
    public void sendMessage(String conversationId, String content) {
        ContactApiService service = ApiClient.getClient().create(ContactApiService.class);

        SendMessageRequest request = new SendMessageRequest(content, "text");

        service.sendMessage(conversationId, request).enqueue(new Callback<ApiResponse<ChatMessage>>() {
            @Override
            public void onResponse(Call<ApiResponse<ChatMessage>> call, Response<ApiResponse<ChatMessage>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Message sent");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ChatMessage>> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    // ========================================
    // UTILITY EXAMPLES
    // ========================================

    /**
     * Sử dụng OrderStatus utility
     */
    public void useOrderStatus(Order order) {
        String status = order.getStatus();

        // Lấy text hiển thị
        String statusText = OrderStatus.getStatusText(status);

        // Kiểm tra có thể hủy không
        boolean canCancel = OrderStatus.canCancel(status);

        // Lấy màu cho status
        int color = OrderStatus.getStatusColor(status);

        Log.d(TAG, "Status: " + statusText + ", Can cancel: " + canCancel);
    }
}
