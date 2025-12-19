package com.example.md_08_ungdungfivestore.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.ManThongTinDonHang;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.Order;
import com.example.md_08_ungdungfivestore.services.ApiClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OrderActionListener listener;
    private ReorderListener reorderListener;
    private RateListener rateListener; // Thêm listener cho đánh giá

    // Interface cho hành động Hủy
    public interface OrderActionListener {
        void onCancelOrder(Order order);
    }

    // Interface cho hành động Mua lại
    public interface ReorderListener {
        void onReorder(Order order);
    }

    // Interface MỚI cho hành động Đánh giá
    public interface RateListener {
        void onRateOrder(Order order);
    }

    public DonHangAdapter(Context context, List<Order> orderList,
                          OrderActionListener listener,
                          ReorderListener reorderListener,
                          RateListener rateListener) { // Thêm tham số vào constructor
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
        this.reorderListener = reorderListener;
        this.rateListener = rateListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_don_hang, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // --- 1. XỬ LÝ TRẠNG THÁI ĐƠN HÀNG (6 BƯỚC) ---
        String statusText = "";
        int statusColor = 0;

        // Kiểm tra null để tránh crash
        String currentStatus = order.getStatus() != null ? order.getStatus() : "";

        switch (currentStatus) {
            case "pending":
                statusText = "Chờ xác nhận";
                statusColor = context.getResources().getColor(android.R.color.holo_orange_light);
                break;

            case "confirmed":
                statusText = "Đã xác nhận";
                statusColor = Color.parseColor("#4CAF50"); // Xanh lá
                break;

            case "processing":
                statusText = "Đang xử lý";
                statusColor = context.getResources().getColor(android.R.color.holo_blue_light);
                break;

            case "packing":
                statusText = "Đang đóng gói";
                statusColor = Color.parseColor("#9C27B0"); // Tím
                break;

            case "shipping":
            case "on_delivery":
                statusText = "Đang giao hàng";
                statusColor = context.getResources().getColor(android.R.color.holo_blue_dark);
                break;

            case "delivered":
                statusText = "Đã giao";
                statusColor = context.getResources().getColor(android.R.color.holo_green_dark);
                break;

            case "cancelled":
                statusText = "Đã hủy";
                statusColor = context.getResources().getColor(android.R.color.holo_red_light);
                break;

            default:
                statusText = currentStatus;
                statusColor = context.getResources().getColor(android.R.color.black);
        }

        holder.tvOrderStatus.setText(statusText);
        holder.tvOrderStatus.setTextColor(statusColor);


        // --- 2. HIỂN THỊ THÔNG TIN SẢN PHẨM ---
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            Order.OrderItem firstItem = order.getItems().get(0);

            // Xử lý tên sản phẩm và hình ảnh
            if (firstItem.getProduct_id() != null) {
                // Trường hợp Product là Object
                holder.tvProductName.setText(firstItem.getProduct_id().getName());

                String imageUrl = firstItem.getProduct_id().getImage();
                if (imageUrl != null && !imageUrl.startsWith("http")) {
                    imageUrl = ApiClient.BASE_URL2 + imageUrl;
                }
                Glide.with(context).load(imageUrl).into(holder.imgProduct);
            } else {
                // Trường hợp fallback hoặc Product chỉ là ID string nhưng có lưu name/image ở item
                holder.tvProductName.setText(firstItem.getName() != null ? firstItem.getName() : "Sản phẩm");
                if (firstItem.getImage() != null) {
                    String imageUrl = firstItem.getImage();
                    if (!imageUrl.startsWith("http")) {
                        imageUrl = ApiClient.BASE_URL2 + imageUrl;
                    }
                    Glide.with(context).load(imageUrl).into(holder.imgProduct);
                }
            }

            // Tính tổng số lượng
            int totalItems = 0;
            for (Order.OrderItem item : order.getItems()) {
                totalItems += item.getQuantity();
            }
            holder.tvProductCount.setText("x " + totalItems + " sản phẩm");
        }

        // --- 3. HIỂN THỊ TỔNG TIỀN ---
        double totalPrice = 0;
        // Nếu API có trả về total_amount thì dùng luôn, nếu không thì tính tay
        if (order.getTotal_amount() > 0) {
            totalPrice = order.getTotal_amount();
        } else if (order.getItems() != null) {
            for (Order.OrderItem item : order.getItems()) {
                totalPrice += item.getPrice() * item.getQuantity();
            }
        }

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvTotalPrice.setText(formatter.format(totalPrice) + " VND");


        // --- 4. XỬ LÝ NÚT BẤM (Hủy đơn/Mua lại/Đánh giá) ---

        // Mặc định ẩn hết
        holder.btnAction.setVisibility(View.GONE);
        holder.btnRate.setVisibility(View.GONE);

        if ("pending".equals(currentStatus)) {
            // Trạng thái Chờ xác nhận -> Hiện nút HỦY
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("Hủy đơn");
            holder.btnAction.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_red_dark));
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) listener.onCancelOrder(order);
            });
        }
        else if ("delivered".equals(currentStatus)) {
            // Trạng thái Đã giao -> Hiện nút MUA LẠI và ĐÁNH GIÁ

            // Nút Mua lại
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("Mua lại");
            // R.color.ChuDe phải có trong colors.xml, nếu lỗi hãy thay bằng Color.parseColor("#FF5722")
            holder.btnAction.setBackgroundTintList(context.getResources().getColorStateList(R.color.ChuDe));
            holder.btnAction.setOnClickListener(v -> {
                if (reorderListener != null) reorderListener.onReorder(order);
            });

            // Nút Đánh giá (chỉ khi đã giao hàng)
            holder.btnRate.setVisibility(View.VISIBLE);
            holder.btnRate.setText("Đánh giá");
            // Có thể set màu khác cho nút đánh giá nếu muốn
            // holder.btnRate.setBackgroundTintList(...);
            holder.btnRate.setOnClickListener(v -> {
                if (rateListener != null) rateListener.onRateOrder(order);
            });
        }
        else if ("cancelled".equals(currentStatus)) {
            // Trạng thái Đã hủy -> Hiện nút MUA LẠI
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("Mua lại");
            holder.btnAction.setBackgroundTintList(context.getResources().getColorStateList(R.color.ChuDe));
            holder.btnAction.setOnClickListener(v -> {
                if (reorderListener != null) reorderListener.onReorder(order);
            });
        }

        // --- 5. SỰ KIỆN CLICK VÀO ITEM ĐỂ XEM CHI TIẾT ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ManThongTinDonHang.class);
            intent.putExtra("orderId", order.get_id());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderStatus, tvProductName, tvProductCount, tvTotalPrice;
        ImageView imgProduct;
        Button btnAction;
        Button btnRate; // Thêm nút đánh giá

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCount = itemView.findViewById(R.id.tvProductCount);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnAction = itemView.findViewById(R.id.btnAction);

            // Đảm bảo bạn đã thêm Button này trong layout item_don_hang.xml với id là btnRate
            btnRate = itemView.findViewById(R.id.btnRate);
        }
    }
}
