package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

public class ReviewRequest {
    @SerializedName("order_id")
    private String orderId;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    public ReviewRequest(String orderId, String productId, int rating, String comment) {
        this.orderId = orderId;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
    }
}
