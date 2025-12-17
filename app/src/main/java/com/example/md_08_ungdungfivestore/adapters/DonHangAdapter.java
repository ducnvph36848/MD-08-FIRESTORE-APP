package com.example.md_08_ungdungfivestore.adapters;

import android.content.Context;
import android.content.Intent;
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

    public interface OrderActionListener {
        void onCancelOrder(Order order);
    }

    public interface ReorderListener {
        void onReorder(Order order);
    }

    public DonHangAdapter(Context context, List<Order> orderList, OrderActionListener listener,
            ReorderListener reorderListener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
        this.reorderListener = reorderListener;
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

        // Set Status
        String statusText = "";
        int statusColor = 0;
        switch (order.getStatus()) {
            case "pending":
                statusText = "Chờ xác nhận";
                statusColor = context.getResources().getColor(android.R.color.holo_orange_light);
                break;
            case "confirmed":
            case "processing":
            case "shipping":
                statusText = "Đang giao";
                statusColor = context.getResources().getColor(android.R.color.holo_blue_light);
                break;
            case "delivered":
                statusText = "Đã giao";
                statusColor = context.getResources().getColor(android.R.color.holo_green_light);
                break;
            case "cancelled":
                statusText = "Đã hủy";
                statusColor = context.getResources().getColor(android.R.color.holo_red_light);
                break;
            default:
                statusText = order.getStatus();
                statusColor = context.getResources().getColor(android.R.color.white);
        }
        holder.tvOrderStatus.setText(statusText);
        holder.tvOrderStatus.setTextColor(statusColor);

        // Set Product Info (First Item)
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            Order.OrderItem firstItem = order.getItems().get(0);

            // Handle Product Name
            if (firstItem.getProduct_id() != null) {
                holder.tvProductName.setText(firstItem.getProduct_id().getName());

                String imageUrl = firstItem.getProduct_id().getImage();
                if (imageUrl != null && !imageUrl.startsWith("http")) {
                    imageUrl = ApiClient.BASE_URL2 + imageUrl;
                }
                Glide.with(context).load(imageUrl).into(holder.imgProduct);
            } else {
                holder.tvProductName.setText(firstItem.getName() != null ? firstItem.getName() : "Sản phẩm");
                if (firstItem.getImage() != null) {
                    String imageUrl = firstItem.getImage();
                    if (!imageUrl.startsWith("http")) {
                        imageUrl =ApiClient.BASE_URL2 + imageUrl;
                    }
                    Glide.with(context).load(imageUrl).into(holder.imgProduct);
                }
            }

            int totalItems = 0;
            for (Order.OrderItem item : order.getItems()) {
                totalItems += item.getQuantity();
            }
            holder.tvProductCount.setText("x " + totalItems + " sản phẩm");
        }

        // Calculate Total Price from items
        double totalPrice = 0;
        if (order.getItems() != null) {
            for (Order.OrderItem item : order.getItems()) {
                totalPrice += item.getPrice() * item.getQuantity();
            }
        }
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvTotalPrice.setText(formatter.format(totalPrice) + " VND");

        // Handle Action Button
        if ("pending".equals(order.getStatus())) {
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("Hủy đơn");
            holder.btnAction
                    .setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_red_dark));
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null)
                    listener.onCancelOrder(order);
            });
        } else if ("delivered".equals(order.getStatus())) {
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("Mua lại");
            holder.btnAction.setBackgroundTintList(context.getResources().getColorStateList(R.color.ChuDe));
            holder.btnAction.setOnClickListener(v -> {
                if (reorderListener != null) {
                    reorderListener.onReorder(order);
                }
            });
        } else {
            holder.btnAction.setVisibility(View.GONE);
        }

        // Item Click -> Detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ManThongTinDonHang.class);
            intent.putExtra("orderId", order.get_id());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderStatus, tvProductName, tvProductCount, tvTotalPrice;
        ImageView imgProduct;
        Button btnAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCount = itemView.findViewById(R.id.tvProductCount);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}
