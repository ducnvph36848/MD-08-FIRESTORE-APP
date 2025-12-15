package com.example.md_08_ungdungfivestore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.services.ApiClient;

 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnItemClickListener listener;
    private boolean showDeleteButton = true; // Default to true for wishlist
    private Set<String> wishlistIds = new HashSet<>(); // Track wishlist product IDs

    public ProductAdapter(Context context, List<Product> productList, OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Product product); // Click vào ảnh hoặc cả item

        void onAddClick(Product product); // Click vào nút thêm

        void onDeleteClick(Product product); // Click vào nút xóa (wishlist)
    }

    public void setShowDeleteButton(boolean show) {
        this.showDeleteButton = show;
        notifyDataSetChanged();
    }

    // Update wishlist IDs
    public void setWishlistIds(Set<String> wishlistIds) {
        this.wishlistIds = wishlistIds != null ? wishlistIds : new HashSet<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.tvName.setText(p.getName());
        holder.tvPrice.setText(String.format("%.0f VND", p.getPrice()));

        // Load ảnh với Glide, check URL đầy đủ
        if (p.getImage() != null && !p.getImage().isEmpty()) {
            String imageUrl = p.getImage();
            if (!imageUrl.startsWith("http")) {
                if (!imageUrl.startsWith("/"))
                    imageUrl = "/" + imageUrl;
                imageUrl = ApiClient.BASE_URL2 + imageUrl;
            }
            Glide.with(context).load(imageUrl).error(R.drawable.ic_kids1).into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_kids1);
        }

        // Click cả item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onItemClick(p);
        });

        // Click trực tiếp vào ảnh cũng mở chi tiết
        holder.imgProduct.setOnClickListener(v -> {
            if (listener != null)
                listener.onItemClick(p);
        });

        // Click nút thêm vào yêu thích (heart icon)
        boolean isInWishlist = wishlistIds.contains(p.getId());
        if (isInWishlist) {
            // Tô vàng nếu đã có trong wishlist
            holder.btnAdd.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_light));
        } else {
            // Màu mặc định (xám hoặc trắng)
            holder.btnAdd.setColorFilter(context.getResources().getColor(android.R.color.darker_gray));
        }
        
        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null)
                listener.onAddClick(p);
        });

        // Click nút xóa (wishlist)
        if (showDeleteButton) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null)
                    listener.onDeleteClick(p);
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice;
        ImageButton btnAdd, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
