package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.AuthResponse;
import com.example.md_08_ungdungfivestore.models.RegisterRequest;
import com.example.md_08_ungdungfivestore.models.RegisterResponse;
import com.example.md_08_ungdungfivestore.models.OtpRequest;
import com.example.md_08_ungdungfivestore.models.LoginRequest;
import com.example.md_08_ungdungfivestore.models.VNPayRequest;
import com.example.md_08_ungdungfivestore.models.VNPayResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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

    //


    @POST("api/vnpay/create-payment")
        Call<VNPayResponse> createVNPay(@Body VNPayRequest body);


}
