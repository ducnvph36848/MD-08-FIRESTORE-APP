package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private WebView webView;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        webView = findViewById(R.id.webView);

        String paymentUrl = getIntent().getStringExtra("paymentUrl");
        orderId = getIntent().getStringExtra("orderId");

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    String url
            ) {
                if (url.contains("vnp_ResponseCode")) {
                    handleReturn(url);
                    return true;
                }
                return false;
            }
        });

        webView.loadUrl(paymentUrl);
    }

    private void handleReturn(String url) {
        Uri uri = Uri.parse(url);
        String responseCode = uri.getQueryParameter("vnp_ResponseCode");

        Intent intent = new Intent();
        intent.putExtra("orderId", orderId);
        intent.putExtra("responseCode", responseCode);
        setResult(RESULT_OK, intent);
        finish();
    }
}
