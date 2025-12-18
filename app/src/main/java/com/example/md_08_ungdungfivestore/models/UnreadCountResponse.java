package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

public class UnreadCountResponse {

    // QUAN TRỌNG: Sử dụng alternate để bắt dính mọi trường hợp tên key có thể xảy ra
    // Gson sẽ thử tìm "count", nếu không thấy sẽ tìm "data", "unread", "unreadCount", "result"...
    // Cái nào có giá trị thì nó sẽ lấy.
    @SerializedName(value = "count", alternate = {"data", "unread", "unreadCount", "result", "total"})
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
