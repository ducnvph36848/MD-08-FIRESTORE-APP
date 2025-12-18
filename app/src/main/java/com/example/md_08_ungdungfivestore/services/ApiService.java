package com.example.md_08_ungdungfivestore.services;

// ✅ THÊM DÒNG NÀY ĐỂ SỬA LỖI
import com.example.md_08_ungdungfivestore.models.ApiResponse;

import com.example.md_08_ungdungfivestore.models.AuthResponse;
import com.example.md_08_ungdungfivestore.models.ChangePasswordRequest;
import com.example.md_08_ungdungfivestore.models.RegisterRequest;
import com.example.md_08_ungdungfivestore.models.RegisterResponse;
import com.example.md_08_ungdungfivestore.models.OtpRequest;
import com.example.md_08_ungdungfivestore.models.LoginRequest;
import com.example.md_08_ungdungfivestore.models.VNPayRequest;
import com.example.md_08_ungdungfivestore.models.VNPayResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Field;

public interface ApiService {

    // Register trả về RegisterResponse
    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    // Verify OTP trả về AuthResponse
    @POST("api/auth/verify-otp")
    Call<AuthResponse> verifyOtp(@Body OtpRequest request);

    // Login trả về AuthResponse
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    // Gửi OTP khi quên mật khẩu
    @POST("api/auth/forgot-password")
    Call<AuthResponse> forgotPassword(@Body OtpRequest request);

    // Đặt lại mật khẩu
    @POST("api/auth/reset-password")
    Call<AuthResponse> resetPassword(@Body OtpRequest request);

    // Tạo thanh toán VNPay
    @POST("api/vnpay/create-payment")
    Call<VNPayResponse> createVNPay(@Body VNPayRequest body);

    // Đổi mật khẩu (cần mật khẩu cũ và mật khẩu mới)
    @FormUrlEncoded
    @POST("api/users/change-password")
    Call<AuthResponse> changePassword(
            @Field("oldPassword") String oldPassword,
            @Field("newPassword") String newPassword
    );




    // --- API Đổi Mật Khẩu ---
    @POST("api/users/change-password")
    Call<ApiResponse> doiMatKhau(@Body ChangePasswordRequest request);

}
