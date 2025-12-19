package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Request body cho tạo đơn hàng (COD hoặc VNPay)
 */
public class CreateOrderRequest {

    // Danh sách sản phẩm
    @SerializedName("items")
    private List<OrderItemRequest> items;

    @SerializedName("shipping_address")
    private Address address;

    @SerializedName("shipping_fee")
    private double shipping_fee;

    @SerializedName("total_amount")
    private double total_amount;

    @SerializedName("cart_item_ids")
    private List<String> cart_item_ids;

    // --- THÊM TRƯỜNG MỚI ---
    // Tên key JSON phải khớp với server (ví dụ: "payment_method" hoặc "paymentMethod")
    @SerializedName("payment_method")
    private String payment_method;

    public CreateOrderRequest() {
    }

    // Constructor cũ (giữ lại để tương thích nếu cần, mặc định là COD)
    public CreateOrderRequest(List<OrderItemRequest> items, Address address,
                              double shipping_fee, double total_amount) {
        this(items, address, shipping_fee, total_amount, null, "COD");
    }

    // Constructor mở rộng (thêm payment_method)
    public CreateOrderRequest(List<OrderItemRequest> items, Address address,
                              double shipping_fee, double total_amount,
                              List<String> cart_item_ids, String payment_method) {

        this.items = items;
        this.address = address;
        this.shipping_fee = shipping_fee;
        this.total_amount = total_amount;
        this.cart_item_ids = cart_item_ids;
        this.payment_method = payment_method;
    }

    // Constructor dùng trong ManDatHang (đơn giản hóa)
    public CreateOrderRequest(List<OrderItemRequest> items, Address address,
                              double shipping_fee, double total_amount, String payment_method) {
        this.items = items;
        this.address = address;
        this.shipping_fee = shipping_fee;
        this.total_amount = total_amount;
        this.payment_method = payment_method;
    }

    // --- Getters and Setters ---

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public List<String> getCart_item_ids() {
        return cart_item_ids;
    }

    public void setCart_item_ids(List<String> cart_item_ids) {
        this.cart_item_ids = cart_item_ids;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public double getShipping_fee() {
        return shipping_fee;
    }

    public void setShipping_fee(double shipping_fee) {
        this.shipping_fee = shipping_fee;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    /**
     * Inner class cho OrderItemRequest
     */
    public static class OrderItemRequest {
        @SerializedName("product_id")
        private String product_id;

        @SerializedName("name")
        private String name;

        @SerializedName("image")
        private String image;

        @SerializedName("size")
        private String size;

        @SerializedName("color")
        private String color;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("price")
        private double price;

        public OrderItemRequest() {
        }

        public OrderItemRequest(String product_id, String name, String image,
                                String size, String color, int quantity, double price) {
            this.product_id = product_id;
            this.name = name;
            this.image = image;
            this.size = size;
            this.color = color;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters and Setters
        public String getProduct_id() { return product_id; }
        public void setProduct_id(String product_id) { this.product_id = product_id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }

        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }
}
