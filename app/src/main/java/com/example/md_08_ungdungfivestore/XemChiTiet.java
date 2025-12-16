package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import com.example.md_08_ungdungfivestore.ManDatHang;
import com.example.md_08_ungdungfivestore.adapters.ImagePagerAdapter;
import com.example.md_08_ungdungfivestore.fragments.SelectOptionsBottomSheetFragment;
import com.example.md_08_ungdungfivestore.models.AddToCartRequest;
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.Cart;
import com.example.md_08_ungdungfivestore.models.CartItem;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.models.WishlistItem;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.CartApiService;
import com.example.md_08_ungdungfivestore.services.ProductApiService;
import com.example.md_08_ungdungfivestore.services.WishlistApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XemChiTiet extends AppCompatActivity {

    private ViewPager2 viewPagerImages;
    private ImageButton btnBack, btnFavorite;
    private TextView tvName, tvPrice, tvDesc, btnOrderNow, btnAddToCart;

    private Product product;
    private List<String> imageUrls = new ArrayList<>();

    private boolean isFavorite = false;
    private WishlistApiService wishlistApiService;
    private CartApiService cartApiService;
    private ProductApiService productApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_chi_tiet);

        anhXa();

        // Lấy Product từ Intent
        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName.setText(product.getName());
        tvPrice.setText(String.format("%.0f VND", product.getPrice()));

        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            StringBuilder desc = new StringBuilder();
            for (Product.Description d : product.getDescription()) {
                desc.append(d.getField()).append(": ").append(d.getValue()).append("\n");
            }
            tvDesc.setText(desc.toString());
        } else {
            tvDesc.setText("Không có mô tả chi tiết.");
        }

        // Ảnh sản phẩm
        imageUrls.clear();
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            imageUrls.add(product.getImage());
        }
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            imageUrls.addAll(product.getImages());
        }

        setupViewPager();
        setupBackButton();

        // Khởi tạo API services
        wishlistApiService = ApiClient.getClient().create(WishlistApiService.class);
        cartApiService = ApiClient.getClient().create(CartApiService.class);
        productApiService = ApiClient.getClient().create(ProductApiService.class);

        // ✅ LOG: Check product ID
        Log.d("XemChiTiet", "Product ID: " + (product != null ? product.getId() : "NULL"));
        Log.d("XemChiTiet", "Product Name: " + (product != null ? product.getName() : "NULL"));

        // Check wishlist status từ server
        checkWishlistStatus();
        setupFavoriteButton();

        // Mở BottomSheet khi nhấn 2 nút
        btnOrderNow.setOnClickListener(v -> openSelectOptionsBottomSheet(true));
        btnAddToCart.setOnClickListener(v -> openSelectOptionsBottomSheet(false));
    }

    private void anhXa() {
        viewPagerImages = findViewById(R.id.viewPagerImages);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);

        tvName = findViewById(R.id.tvProductName);
        tvPrice = findViewById(R.id.tvProductPrice);
        tvDesc = findViewById(R.id.tvProductDesc);

        btnOrderNow = findViewById(R.id.btnOrderNow);
        btnAddToCart = findViewById(R.id.btnAddToCart);
    }

    private void setupViewPager() {
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, imageUrls);
        viewPagerImages.setAdapter(adapter);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void checkWishlistStatus() {
        wishlistApiService.checkWishlist(product.getId()).enqueue(new Callback<ApiResponse<Map<String, Boolean>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Boolean>>> call,
                    Response<ApiResponse<Map<String, Boolean>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Map<String, Boolean> data = response.body().getData();
                    isFavorite = data != null && data.getOrDefault("inWishlist", false);
                    btnFavorite.setImageResource(isFavorite ? R.drawable.heart_filled : R.drawable.heart_empty);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Boolean>>> call, Throwable t) {
                // Ignore error, default to not favorite
            }
        });
    }

    private void setupFavoriteButton() {
        btnFavorite.setOnClickListener(v -> {
            btnFavorite.animate()
                    .scaleX(1.3f).scaleY(1.3f).setDuration(120)
                    .withEndAction(() -> btnFavorite.animate().scaleX(1f).scaleY(1f).setDuration(120).start()).start();

            if (!isFavorite) {
                addToWishlistAPI(product.getId());
            } else {
                removeFromWishlistAPI(product.getId());
            }
        });
    }

    private void addToWishlistAPI(String productId) {
        Log.d("WISHLIST", "=== ADD TO WISHLIST ===");
        Log.d("WISHLIST", "Product ID: " + productId);

        Map<String, String> body = new HashMap<>();
        body.put("productId", productId);

        Log.d("WISHLIST", "Request body: " + body.toString());

        wishlistApiService.addToWishlist(body).enqueue(new Callback<ApiResponse<WishlistItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<WishlistItem>> call, Response<ApiResponse<WishlistItem>> response) {
                Log.d("WISHLIST", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("WISHLIST", "Success: " + response.body().isSuccess());
                    Log.d("WISHLIST", "Message: " + response.body().getMessage());

                    if (response.body().isSuccess()) {
                        isFavorite = true;
                        btnFavorite.setImageResource(R.drawable.heart_filled);
                        Toast.makeText(XemChiTiet.this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("WISHLIST", "Server error: " + response.body().getMessage());
                        Toast.makeText(XemChiTiet.this, "Lỗi: " + response.body().getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("WISHLIST", "Error response: " + errorBody);
                        Toast.makeText(XemChiTiet.this, "Lỗi HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("WISHLIST", "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<WishlistItem>> call, Throwable t) {
                Log.e("WISHLIST", "Network error: " + t.getMessage(), t);
                Toast.makeText(XemChiTiet.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromWishlistAPI(String productId) {
        wishlistApiService.removeFromWishlist(productId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    isFavorite = false;
                    btnFavorite.setImageResource(R.drawable.heart_empty);
                    Toast.makeText(XemChiTiet.this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(XemChiTiet.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openSelectOptionsBottomSheet(boolean isBuyNow) {
        if (product == null)
            return;

        SelectOptionsBottomSheetFragment bottomSheet = new SelectOptionsBottomSheetFragment(product,
                (size, color, quantity) -> {
                    if (isBuyNow) {
                        // Navigate to ManDatHang
                        CartItem item = new CartItem(
                                product, // Use product object
                                product.getName(),
                                product.getImage(),
                                size,
                                color,
                                quantity,
                                product.getPrice());

                        // Set ID manually if needed, though ManDatHang uses product_id from item
                        item.setProduct_id(product);

                        ArrayList<CartItem> selectedItems = new ArrayList<>();
                        selectedItems.add(item);

                        Intent intent = new Intent(XemChiTiet.this, ManDatHang.class);
                        intent.putExtra("selectedItems", selectedItems);
                        startActivity(intent);
                    } else {
                        // Add to cart
                        addToCartAPI(product, size, color, quantity);
                    }
                });

        bottomSheet.setBuyNow(isBuyNow);
        bottomSheet.show(getSupportFragmentManager(), "SelectOptionsBottomSheet");
    }

    private void addToCartAPI(Product product, String size, String color, int quantity) {
        AddToCartRequest request = new AddToCartRequest(
                product.getId(),
                product.getName(),
                product.getImage(),
                size,
                color,
                quantity,
                product.getPrice());

        cartApiService.addToCart(request).enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(XemChiTiet.this,
                            "Đã thêm vào giỏ hàng: Size " + size + ", Màu " + color + ", SL: " + quantity,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(XemChiTiet.this, "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
                Log.d("CART", t.getMessage());
                Toast.makeText(XemChiTiet.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh product data when returning from order screen
        if (product != null && product.getId() != null) {
            refreshProductData();
        }
    }
    
    private void refreshProductData() {
        productApiService.getProductById(product.getId()).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    product = response.body();
                    // Cập nhật UI với dữ liệu mới
                    tvName.setText(product.getName());
                    tvPrice.setText(String.format("%.0f VND", product.getPrice()));
                    
                    // Cập nhật mô tả
                    if (product.getDescription() != null && !product.getDescription().isEmpty()) {
                        StringBuilder desc = new StringBuilder();
                        for (Product.Description d : product.getDescription()) {
                            desc.append(d.getField()).append(": ").append(d.getValue()).append("\n");
                        }
                        tvDesc.setText(desc.toString());
                    }
                    
                    Log.d("XemChiTiet", "Product refreshed. New quantity: " + product.getQuantity());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("XemChiTiet", "Failed to refresh product: " + t.getMessage());
            }
        });
    }
}
