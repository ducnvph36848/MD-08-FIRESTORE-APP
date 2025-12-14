package com.example.md_08_ungdungfivestore.models;

public class VNPayRequest {
    private String order_id;
    private long total;
    private String user_id;

    public VNPayRequest(String order_id, long total, String user_id) {
        this.order_id = order_id;
        this.total = total;
        this.user_id = user_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public long getTotal() {
        return total;
    }

    public String getUser_id() {
        return user_id;
    }
}

