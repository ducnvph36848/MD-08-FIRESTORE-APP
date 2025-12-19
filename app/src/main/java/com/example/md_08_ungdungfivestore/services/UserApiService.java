package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.AddToCartRequest;
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.ChangePasswordRequest;
import com.example.md_08_ungdungfivestore.models.UpdateProfileRequest;
import com.example.md_08_ungdungfivestore.models.UserProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * API Service cho User/Profile
 */
public interface UserApiService {

    /**
     * Lấy thông tin user hiện tại
     */
    @GET("api/users/me")
    Call<ApiResponse<UserProfile>> getCurrentUser();

    /**
     * Cập nhật thông tin profile
     */
    @PUT("api/users/update-profile")
    Call<ApiResponse<UserProfile>> updateProfile(@Body UpdateProfileRequest request);

    /**
     * Đổi mật khẩu
     */
    @PUT("api/users/change-password")
    Call<ApiResponse<Void>> changePassword(@Body ChangePasswordRequest request);

    /**
     * Đăng xuất (optional - có thể chỉ xóa token ở client)
     */
    @POST("api/auth/logout")
    Call<ApiResponse<Void>> logout();
    // mua lại
    @POST("api/cart/add") // Endpoint của bạn
    Call<ApiResponse<Object>> addToCart(@Body AddToCartRequest request);
}
