package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        webView = findViewById(R.id.webView);

        // FIX: Receive the URL passed from the previous activity
        String paymentUrl = getIntent().getStringExtra("paymentUrl");

        // Validate the URL before loading
        if (paymentUrl == null || paymentUrl.isEmpty()) {
            Toast.makeText(this, "Invalid Payment URL", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no URL found
            return;
        }

        // Cấu hình WebView
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true); // Bắt buộc cho VNPay
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            // Hỗ trợ Android < 24
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(url);
            }

            // Hỗ trợ Android >= 24
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleUrl(request.getUrl().toString());
            }

            private boolean handleUrl(String url) {
                Log.d("PaymentWebView", "Navigating to: " + url);

                // ✅ BẮT DEEP LINK RETURN TỪ VNPAY (fivestore://app)
                if (url.startsWith("fivestore://app")) {
                    try {
                        // Mở PaymentReturnActivity thông qua Intent Filter
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        finish(); // Đóng màn hình WebView này lại để user không back lại được
                        return true;
                    } catch (Exception e) {
                        Log.e("PaymentActivity", "Lỗi mở Deep Link: " + e.getMessage());
                        Toast.makeText(PaymentActivity.this, "Không tìm thấy ứng dụng xử lý kết quả", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

                // Các link khác (ngân hàng, OTP...) để WebView tự load
                return false;
            }
        });

        webView.loadUrl(paymentUrl);
    }

    // Xử lý nút Back: Nếu WebView có lịch sử (ví dụ user vào trang chọn thẻ -> nhập OTP), back sẽ quay lại trang trước
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
