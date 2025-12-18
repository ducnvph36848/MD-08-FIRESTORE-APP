package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request body cho đổi mật khẩu
 */
public class ChangePasswordRequest {

    // @SerializedName giúp định nghĩa chính xác tên key trong JSON gửi đi.
    // Nếu server của bạn yêu cầu "old_password" (gạch dưới), hãy giữ nguyên như bên dưới.
    // Nếu server yêu cầu "oldPassword" (viết liền), hãy sửa lại string trong ngoặc.
    @SerializedName("oldPassword")
    private String oldPassword;

    @SerializedName("newPassword")
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    // Getters and Setters
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
