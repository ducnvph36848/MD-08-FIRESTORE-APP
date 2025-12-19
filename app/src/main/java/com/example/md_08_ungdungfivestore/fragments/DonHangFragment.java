package com.example.md_08_ungdungfivestore.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.ManDanhGiaSanPham;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.adapters.DonHangAdapter;
import com.example.md_08_ungdungfivestore.models.AddToCartRequest;
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.Order;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.OrderApiService;
import com.example.md_08_ungdungfivestore.services.UserApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonHangFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private String status;
    private RecyclerView recyclerView;
    private DonHangAdapter adapter;
    private List<Order> orderList = new ArrayList<>();

    // Services
    private OrderApiService orderApiService;
    private UserApiService userApiService;

    // --- BIẾN CHO TỰ ĐỘNG CẬP NHẬT ---
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;
    private final int REFRESH_INTERVAL = 5000;
    // ---------------------------------

    public DonHangFragment() {
    }

    public static DonHangFragment newInstance(String status) {
        DonHangFragment fragment = new DonHangFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_don_hang, container, false);

        recyclerView = view.findViewById(R.id.rcvDonHang);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cập nhật Adapter: Thêm callback handleRate
        adapter = new DonHangAdapter(
                getContext(),
                orderList,
                this::showCancelConfirmation,
                this::handleReorder,
                this::handleRate // Thêm hàm xử lý đánh giá
        );
        recyclerView.setAdapter(adapter);

        orderApiService = ApiClient.getClient().create(OrderApiService.class);
        userApiService = ApiClient.getClient().create(UserApiService.class);

        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadOrders();
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        };

        loadOrders();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoRefresh();
    }

    private void startAutoRefresh() {
        stopAutoRefresh();
        handler.post(refreshRunnable);
    }

    private void stopAutoRefresh() {
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }

    private void loadOrders() {
        orderApiService.getMyOrders().enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<Order> newOrderList = new ArrayList<>();
                    if (response.body().getData() != null) {
                        for (Order order : response.body().getData()) {
                            if (shouldShowOrder(order)) {
                                newOrderList.add(order);
                            }
                        }
                    }
                    orderList.clear();
                    orderList.addAll(newOrderList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                Log.e("DonHangFragment", "Lỗi tải đơn hàng: " + t.getMessage());
            }
        });
    }

    private boolean shouldShowOrder(Order order) {
        if (status == null || status.isEmpty()) return true;
        String s = order.getStatus();
        if (s == null) return false;

        switch (status) {
            case "pending":
                return s.equals("pending") || s.equals("confirmed") || s.equals("processing") || s.equals("packing");
            case "shipping":
                return s.equals("shipping") || s.equals("on_delivery");
            case "delivered":
                return s.equals("delivered");
            case "cancelled":
                return s.equals("cancelled");
            default:
                return s.equals(status);
        }
    }

    // --- HỦY ĐƠN ---
    private void showCancelConfirmation(Order order) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc muốn hủy đơn hàng này?")
                .setPositiveButton("Hủy đơn", (dialog, which) -> cancelOrder(order.get_id()))
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void cancelOrder(String orderId) {
        orderApiService.cancelOrder(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (getContext() != null && response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                    loadOrders();
                } else {
                    Toast.makeText(getContext(), "Không thể hủy đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- ĐÁNH GIÁ (RATE) - MỚI THÊM ---
    private void handleRate(Order order) {
        if (getContext() == null) return;

        // Chuyển sang màn hình đánh giá
        Intent intent = new Intent(getContext(), ManDanhGiaSanPham.class);

        // Truyền ID đơn hàng hoặc List sản phẩm sang màn hình đánh giá
        intent.putExtra("orderId", order.get_id());

        // Nếu cần truyền list sản phẩm (dưới dạng Serializable hoặc Parcelable)
        // intent.putExtra("orderItems", (Serializable) order.getItems());

        startActivity(intent);
    }

    // --- MUA LẠI (REORDER) ---
    private void handleReorder(Order order) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Mua lại")
                .setMessage("Thêm sản phẩm trong đơn này vào giỏ hàng?")
                .setPositiveButton("Thêm vào giỏ", (dialog, which) -> addOrderItemsToCart(order))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void addOrderItemsToCart(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            Toast.makeText(getContext(), "Đơn hàng không có sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Order.OrderItem> items = order.getItems();
        int totalItems = items.size();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger completedCount = new AtomicInteger(0);

        Toast.makeText(getContext(), "Đang xử lý " + totalItems + " sản phẩm...", Toast.LENGTH_SHORT).show();

        for (Order.OrderItem item : items) {
            String productId = null;
            if (item.getProduct_id() != null) {
                try {
                    productId = item.getProduct_id().getId();
                } catch (Exception e) {
                    Log.e("REORDER_DEBUG", "Không gọi được getId(): " + e.getMessage());
                }
            } else {
                Log.e("REORDER_DEBUG", "Product Object null cho item: " + item.getName());
            }

            if (productId == null || productId.isEmpty()) {
                Log.e("REORDER_DEBUG", "SKIP ITEM: ID bị null. Tên: " + item.getName());
                completedCount.incrementAndGet();
                failCount.incrementAndGet();
                checkReorderComplete(totalItems, completedCount.get(), successCount.get());
                continue;
            }

            final String finalProductId = productId;
            Log.d("REORDER_DEBUG", "Đang thêm sản phẩm ID: " + finalProductId);

            AddToCartRequest request = new AddToCartRequest(
                    finalProductId,
                    item.getName(),
                    item.getImage(),
                    item.getSize(),
                    item.getColor(),
                    item.getQuantity(),
                    item.getPrice()
            );

            userApiService.addToCart(request).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    completedCount.incrementAndGet();
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        successCount.incrementAndGet();
                        Log.d("REORDER_DEBUG", "Thêm thành công: " + finalProductId);
                    } else {
                        failCount.incrementAndGet();
                        Log.e("REORDER_DEBUG", "Thất bại: " + finalProductId + " Code: " + response.code());
                        try {
                            if(response.errorBody() != null) {
                                String err = response.errorBody().string();
                                Log.e("REORDER_DEBUG", "Error Body: " + err);
                            }
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                    checkReorderComplete(totalItems, completedCount.get(), successCount.get());
                }

                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    completedCount.incrementAndGet();
                    failCount.incrementAndGet();
                    Log.e("REORDER_DEBUG", "Lỗi mạng: " + t.getMessage());
                    checkReorderComplete(totalItems, completedCount.get(), successCount.get());
                }
            });
        }
    }

    private void checkReorderComplete(int total, int completed, int success) {
        if (completed == total) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (success > 0) {
                        String msg = "Đã thêm " + success + " sản phẩm vào giỏ hàng";
                        if (total > success) {
                            msg += " (" + (total - success) + " lỗi)";
                        }
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Thêm thất bại.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
