package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.NotificationResponse;
import com.example.md_08_ungdungfivestore.models.UnreadCountResponse; // Import model mới

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface NotificationApiService {

    // Lấy danh sách thông báo
    @GET("api/notifications")
    Call<NotificationResponse> getMyNotifications();

    // Đánh dấu tất cả là đã đọc
    @PUT("api/notifications/mark-read")
    Call<Void> markAllRead();

    // Đếm số lượng thông báo chưa đọc (ĐÃ SỬA KIỂU TRẢ VỀ)
    @GET("api/notifications/unread-count")
    Call<UnreadCountResponse> getUnreadCount();
}
