package com.example.md_08_ungdungfivestore.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.adapters.NotificationAdapter;
import com.example.md_08_ungdungfivestore.models.Notification;
import com.example.md_08_ungdungfivestore.models.NotificationResponse;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.NotificationApiService;
import com.example.md_08_ungdungfivestore.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThongBaoFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notifications = new ArrayList<>();

    // Handler polling cập nhật danh sách (nếu đang xem mà có tin mới tới)
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pollingRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recyclerNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        // 1. Đánh dấu tất cả là đã đọc ngay khi vào màn hình
        markAllRead();

        // 2. Tải danh sách và bắt đầu polling
        startPolling();

        return view;
    }

    private void startPolling() {
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                loadNotifications();
                handler.postDelayed(this, 5000); // 10 giây tải lại 1 lần
            }
        };
        handler.post(pollingRunnable);
    }

    private void loadNotifications() {
        TokenManager tokenManager = new TokenManager(getContext());
        if (tokenManager.getToken() == null) return;

        ApiClient.getNotificationService().getMyNotifications().enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Giả sử getter là getNotifications() hoặc getData() tùy Model của bạn
                    List<Notification> newNotifications = response.body().getNotifications();

                    if (newNotifications != null) {
                        notifications.clear();
                        notifications.addAll(newNotifications);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                Log.e("ThongBaoFragment", "Lỗi tải thông báo: " + t.getMessage());
            }
        });
    }

    private void markAllRead() {
        TokenManager tokenManager = new TokenManager(getContext());
        if (tokenManager.getToken() == null) return;

        // Gọi API đánh dấu đã đọc
        ApiClient.getNotificationService().markAllRead().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ThongBaoFragment", "Đã đánh dấu tất cả là đã đọc");
                    // Sau khi mark read xong, tải lại danh sách để cập nhật UI (nếu cần)
                    loadNotifications();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ThongBaoFragment", "Lỗi mark read: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && pollingRunnable != null) {
            handler.removeCallbacks(pollingRunnable);
        }
    }
}
