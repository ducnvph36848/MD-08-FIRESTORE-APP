package com.example.md_08_ungdungfivestore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.Notification;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification item = notifications.get(position);

        holder.title.setText(item.getTitle());

        // Lấy nội dung gốc và dịch sang tiếng Việt trước khi hiển thị
        String originalMessage = item.getMessage();
        String translatedMessage = translateStatus(originalMessage);
        holder.message.setText(translatedMessage);

        // Hiển thị thời gian (bạn có thể format lại ngày tháng ở đây nếu muốn đẹp hơn)
        holder.time.setText(item.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    // Hàm hỗ trợ dịch các trạng thái đơn hàng từ tiếng Anh sang tiếng Việt
    private String translateStatus(String message) {
        if (message == null) return "";

        // Dịch các từ khóa trạng thái thường gặp trong E-commerce
        return message
                .replace("pending", "chờ xác nhận")
                .replace("confirmed", "đã xác nhận")
                .replace("processing", "đang xử lý")
                .replace("packing", "đang đóng gói")
                .replace("shipping", "đang vận chuyển")
                .replace("shipped", "đã giao cho ĐVVC")
                .replace("delivered", "giao hàng thành công")
                .replace("cancelled", "đã hủy")
                .replace("returned", "trả hàng/hoàn tiền")
                .replace("paid", "đã thanh toán")
                .replace("unpaid", "chưa thanh toán")
                .replace("refunded", "đã hoàn tiền");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            message = itemView.findViewById(R.id.txtMessage);
            time = itemView.findViewById(R.id.txtTime);
        }
    }
}
