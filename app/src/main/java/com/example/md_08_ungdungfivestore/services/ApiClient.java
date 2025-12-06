package com.example.md_08_ungdungfivestore.services;

import android.content.Context;

import com.example.md_08_ungdungfivestore.utils.TokenManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ApiClient {
    public static final String BASE_URL = "http://192.168.1.120:5001/";
    private static Retrofit retrofit;
    private static Context appContext;

    /**
     * Khởi tạo ApiClient với context (gọi 1 lần trong Application hoặc
     * MainActivity)
     */
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    /**
     * Lấy Retrofit instance với authentication interceptor
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Interceptor để tự động thêm JWT token vào header
     */
    private static class AuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            // Nếu chưa init context, thực hiện request bình thường
            if (appContext == null) {
                return chain.proceed(originalRequest);
            }

            // Lấy token từ TokenManager
            TokenManager tokenManager = new TokenManager(appContext);
            String token = tokenManager.getToken();

            // Nếu có token, thêm vào header
            if (token != null && !token.isEmpty()) {
                Request newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }

            return chain.proceed(originalRequest);
        }
    }

    public static ProductApiService getProductService() {
        return getClient().create(ProductApiService.class);
    }
}
