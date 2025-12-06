package com.example.md_08_ungdungfivestore.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.adapters.ProductAdapter;
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.models.WishlistItem;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.WishlistApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YeuThichFragment extends Fragment {
    private RecyclerView danhSachYeuThichRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> wishlistProducts = new ArrayList<>();
    private WishlistApiService wishlistApiService;

    public YeuThichFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_man_yeu_thich, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        anhXa(view);
        setupRecyclerView();

        // Initialize API
        wishlistApiService = ApiClient.getClient().create(WishlistApiService.class);

        // Load wishlist
        loadWishlist();
    }

    private void anhXa(View view) {
        danhSachYeuThichRecyclerView = view.findViewById(R.id.danhSachYeuThichRecyclerView);
    }

    private void setupRecyclerView() {
        danhSachYeuThichRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productAdapter = new ProductAdapter(getContext(), wishlistProducts, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                // Navigate to product detail
                Intent intent = new Intent(getContext(), com.example.md_08_ungdungfivestore.XemChiTiet.class);
                intent.putExtra("product", product);
                startActivity(intent);
            }

            @Override
            public void onAddClick(Product product) {
                // Add to cart logic
                Toast.makeText(getContext(), "Thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(Product product) {
                showDeleteConfirmation(product);
            }
        });

        // Show delete button in wishlist
        productAdapter.setShowDeleteButton(true);

        danhSachYeuThichRecyclerView.setAdapter(productAdapter);
    }

    private void loadWishlist() {
        wishlistApiService.getWishlists().enqueue(new Callback<ApiResponse<List<WishlistItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<WishlistItem>>> call,
                    Response<ApiResponse<List<WishlistItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<WishlistItem>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<WishlistItem> wishlistItems = apiResponse.getData();

                        wishlistProducts.clear();
                        for (WishlistItem item : wishlistItems) {
                            if (item.getProduct() != null) {
                                wishlistProducts.add(item.getProduct());
                            }
                        }

                        productAdapter.notifyDataSetChanged();
                        updateUI();
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách yêu thích", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<WishlistItem>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (wishlistProducts.isEmpty()) {
            danhSachYeuThichRecyclerView.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Danh sách yêu thích trống", Toast.LENGTH_SHORT).show();
        } else {
            danhSachYeuThichRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteConfirmation(Product product) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa khỏi yêu thích")
                .setMessage("Bạn có chắc muốn xóa \"" + product.getName() + "\" khỏi danh sách yêu thích?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    removeFromWishlist(product.getId());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void removeFromWishlist(String productId) {
        wishlistApiService.removeFromWishlist(productId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                    loadWishlist(); // Reload wishlist
                } else {
                    Toast.makeText(getContext(), "Không thể xóa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload wishlist when fragment becomes visible
        loadWishlist();
    }
}
