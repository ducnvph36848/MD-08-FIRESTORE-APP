package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.md_08_ungdungfivestore.fragments.GioHangFragment;
import com.example.md_08_ungdungfivestore.fragments.TrangCaNhanFragment;
import com.example.md_08_ungdungfivestore.fragments.TrangChuFragment;
import com.example.md_08_ungdungfivestore.fragments.YeuThichFragment;
import com.example.md_08_ungdungfivestore.fragments.ThongBaoFragment;
import com.example.md_08_ungdungfivestore.models.UnreadCountResponse; // Import model mới
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.utils.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    FrameLayout layout;
    BottomNavigationView menu;
    Toolbar toolbar;
    TextView tieuDe;
    ImageView iconBell;

    // Khai báo biến để chạy kiểm tra thông báo ngầm
    private Handler notificationHandler = new Handler(Looper.getMainLooper());
    private Runnable notificationRunnable;
    private final int POLLING_INTERVAL = 5000; // 5 giây kiểm tra 1 lần

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // QUAN TRỌNG: Khởi tạo ApiClient
        ApiClient.init(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();

        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            taiFragment(new TrangChuFragment());
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        menu.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navTrangChu) {
                taiFragment(new TrangChuFragment());
                tieuDe.setText("Trang Chủ");
            } else if (item.getItemId() == R.id.navYeuThich) {
                taiFragment(new YeuThichFragment());
                tieuDe.setText("Yêu thích");
            } else if (item.getItemId() == R.id.navGioHang) {
                taiFragment(new GioHangFragment());
                tieuDe.setText("Giỏ hàng");
            } else if (item.getItemId() == R.id.navNguoiDung) {
                taiFragment(new TrangCaNhanFragment());
                tieuDe.setText("Người dùng");
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Khi quay lại app, bắt đầu chạy kiểm tra thông báo
        startNotificationPolling();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Khi thoát app hoặc tắt màn hình, dừng kiểm tra để tiết kiệm pin
        stopNotificationPolling();
    }

    private void anhXa() {
        layout = findViewById(R.id.trangChuFrameLayout);
        menu = findViewById(R.id.menuTrangChuBottom);
        toolbar = findViewById(R.id.toolBarTrangChu);
        tieuDe = findViewById(R.id.tieuDeTextView);
        iconBell = findViewById(R.id.iconBell);

        findViewById(R.id.iconUser).setOnClickListener(v -> {
            startActivity(new Intent(this, ManThongTinCaNhan.class));
        });

        // Click icon thông báo
        iconBell.setOnClickListener(v -> {
            // Khi bấm vào xem, reset màu chuông ngay lập tức
            updateBellColor(false);
            taiFragment(new ThongBaoFragment());
            tieuDe.setText("Thông báo");
        });
    }

    private void taiFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.trangChuFrameLayout, fragment)
                .commit();
    }

    // --- LOGIC KIỂM TRA THÔNG BÁO ---

    private void startNotificationPolling() {
        stopNotificationPolling();
        notificationRunnable = new Runnable() {
            @Override
            public void run() {
                checkUnreadNotifications();
                // Lặp lại sau 5 giây
                notificationHandler.postDelayed(this, POLLING_INTERVAL);
            }
        };
        notificationHandler.post(notificationRunnable);
    }

    private void stopNotificationPolling() {
        if (notificationHandler != null && notificationRunnable != null) {
            notificationHandler.removeCallbacks(notificationRunnable);
        }
    }

    private void checkUnreadNotifications() {
        TokenManager tokenManager = new TokenManager(this);
        if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
            return;
        }

        // ĐÃ SỬA: Dùng UnreadCountResponse thay vì Integer
        ApiClient.getNotificationService().getUnreadCount().enqueue(new Callback<UnreadCountResponse>() {
            @Override
            public void onResponse(Call<UnreadCountResponse> call, Response<UnreadCountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Lấy số lượng từ Model
                    int unreadCount = response.body().getCount();
                    Log.d("NOTI_DEBUG", "Số lượng chưa đọc: " + unreadCount);

                    updateBellColor(unreadCount > 0);
                }
            }

            @Override
            public void onFailure(Call<UnreadCountResponse> call, Throwable t) {
                Log.e("MainActivity", "Lỗi check thông báo: " + t.getMessage());
            }
        });
    }

    private void updateBellColor(boolean hasUnread) {
        if (iconBell == null) return;

        if (hasUnread) {
            // Đổi màu cam (đảm bảo orange_color có trong colors.xml)
            iconBell.setColorFilter(
                    ContextCompat.getColor(this, R.color.orange_color),
                    PorterDuff.Mode.SRC_IN
            );
        } else {
            // Xóa màu (trở về màu gốc)
            iconBell.clearColorFilter();
        }
    }
}
