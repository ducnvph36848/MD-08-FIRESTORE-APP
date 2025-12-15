package com.example.md_08_ungdungfivestore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.CartItem;
import com.example.md_08_ungdungfivestore.services.ApiClient;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private CartListener listener;
    private Set<String> selectedItemIds = new HashSet<>();
    private boolean isCheckoutMode = false;

    public interface CartListener {
        void onQuantityChanged(CartItem item, int newQuantity);

        void onRemoveItem(CartItem item);

        void onItemSelected(CartItem item, boolean isSelected);

        void onDeleteClick(CartItem item);
    }

    public GioHangAdapter(Context context, List<CartItem> cartItems, CartListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    public Set<String> getSelectedItemIds() {
        return selectedItemIds;
    }

    public void clearSelection() {
        selectedItemIds.clear();
        notifyDataSetChanged();
    }

    public void setCheckoutMode(boolean isCheckoutMode) {
        this.isCheckoutMode = isCheckoutMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Set product info
        holder.tvProductName.setText(item.getName());
        holder.tvProductSize.setText("Size: " + item.getSize());

        // Format price
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvProductPrice.setText(formatter.format(item.getPrice()) + " VND");

        // Set quantity
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Set checkbox state
        holder.cbSelectItem.setChecked(selectedItemIds.contains(item.get_id()));

        // Load image
        // Load image with base URL check
        String imageUrl = item.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http")) {
                if (!imageUrl.startsWith("/"))
                    imageUrl = "/" + imageUrl;
                imageUrl = ApiClient.BASE_URL2+ imageUrl;
            }
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_launcher_background);
        }

        // Checkbox listener
        holder.cbSelectItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onItemSelected(item, isChecked);
            }
        });

        // Delete button listener
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(item);
            }
        });

        // Increase quantity
        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            if (listener != null) {
                listener.onQuantityChanged(item, newQuantity);
            }
        });

        // Decrease quantity
        holder.btnDecrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity > 0 && listener != null) {
                listener.onQuantityChanged(item, newQuantity);
            }
        });

        // Checkout Mode Logic
        if (isCheckoutMode) {
            holder.cbSelectItem.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnIncrease.setVisibility(View.GONE);
            holder.btnDecrease.setVisibility(View.GONE);
        } else {
            holder.cbSelectItem.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnIncrease.setVisibility(View.VISIBLE);
            holder.btnDecrease.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductSize, tvProductPrice;
        TextView tvQuantity;
        TextView btnIncrease, btnDecrease, btnDelete;
        CheckBox cbSelectItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            // Match với item_cart.xml IDs
            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);
            ivProductImage = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductSize = itemView.findViewById(R.id.tvSize);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
