package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

/**
 * Generic API response wrapper
 * @param <T> Kiểu dữ liệu của data trong response
 */
public class ApiResponse<T> {

    // Bắt cả "success", "isSuccess", và "status" (nếu status=true)
    @SerializedName(value = "success", alternate = {"isSuccess", "status"})
    private boolean success;

    // Bắt cả "message", "msg", và "error"
    @SerializedName(value = "message", alternate = {"msg", "error", "description"})
    private String message;

    // QUAN TRỌNG: Bắt "data", nhưng nếu không có thì tìm "paymentUrl", "url", "result"
    // Điều này giúp lấy được link VNPay dù server đặt tên biến là gì.
    @SerializedName(value = "data", alternate = {"paymentUrl", "url", "result", "link"})
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // --- Getters & Setters ---

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // Hỗ trợ Log để debug xem data thực sự là gì
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
