package com.example.md_08_ungdungfivestore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.XemChiTiet;
import com.example.md_08_ungdungfivestore.adapters.ProductAdapter;
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.models.WishlistItem;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.ProductApiService;
import com.example.md_08_ungdungfivestore.services.WishlistApiService;
import com.example.md_08_ungdungfivestore.services.BrandApiService;
import com.example.md_08_ungdungfivestore.services.CategoryApiService;
import com.example.md_08_ungdungfivestore.models.Brand;
import com.example.md_08_ungdungfivestore.models.Category;
import com.example.md_08_ungdungfivestore.adapters.BrandAdapter;
import com.example.md_08_ungdungfivestore.adapters.CategoryAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrangChuFragment extends Fragment {

    private EditText timKiemEditText;
    private RecyclerView rcvProducts;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private ProductApiService apiService;
    private WishlistApiService wishlistApiService;
    private BrandApiService brandApiService;
    private CategoryApiService categoryApiService;

    private RecyclerView rcvBrands;
    private RecyclerView rcvCategories;
    private BrandAdapter brandAdapter;
    private CategoryAdapter categoryAdapter;
    private List<Brand> brandList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private Set<String> wishlistProductIds = new HashSet<>(); // Track wishlist product IDs
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trangchu, container, false);
        wishlistApiService = ApiClient.getClient().create(WishlistApiService.class);

        timKiemEditText = view.findViewById(R.id.timKiemEditText);
        timKiemEditText.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), com.example.md_08_ungdungfivestore.TimKiem.class);
            startActivity(intent);
        });

        rcvProducts = view.findViewById(R.id.rcvProducts);
        rcvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Setup Brand RecyclerView
        rcvBrands = view.findViewById(R.id.rcvBrands);
        rcvBrands.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        brandAdapter = new BrandAdapter(requireContext(), brandList, brand -> {
            // Toast removed
            filterProducts(null, brand.getName());
        });
        rcvBrands.setAdapter(brandAdapter);

        // Setup Category RecyclerView
        rcvCategories = view.findViewById(R.id.rcvCategories);
        rcvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(requireContext(), categoryList, category -> {
             // Toast removed
             filterProducts(category.getName(), null);
        });
        rcvCategories.setAdapter(categoryAdapter);

        adapter = new ProductAdapter(requireContext(), productList, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                if (product != null) {
                    Intent intent = new Intent(requireContext(), XemChiTiet.class);
                    intent.putExtra("product", product);
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAddClick(Product product) {
                if (product != null) {
                    addToWishlistAPI(product.getId());
                }
            }

            @Override
            public void onDeleteClick(Product product) {
                // Not used in home page
            }
        });

        // HIDE delete button on home page
        adapter.setShowDeleteButton(false);

        rcvProducts.setAdapter(adapter);
        setupApiService();
        fetchWishlist(); // Fetch wishlist first
        fetchProducts();
        fetchBrands();
        fetchCategories();

        return view;
    }
    
    private void fetchWishlist() {
        // Only fetch wishlist if service is initialized
        if (wishlistApiService == null) {
            Log.w("WISHLIST", "WishlistApiService not initialized");
            return;
        }
        
        wishlistApiService.getWishlists().enqueue(new Callback<ApiResponse<List<WishlistItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<WishlistItem>>> call, Response<ApiResponse<List<WishlistItem>>> response) {
                if (!isAdded()) return; // Fragment not attached
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<WishlistItem> items = response.body().getData();
                    wishlistProductIds.clear();
                    if (items != null) {
                        for (WishlistItem item : items) {
                            // Use getProduct_id() which returns String
                            if (item.getProduct_id() != null) {
                                wishlistProductIds.add(item.getProduct_id());
                            }
                        }
                    }
                    // Update adapter with wishlist IDs
                    if (adapter != null) {
                        adapter.setWishlistIds(wishlistProductIds);
                    }
                    Log.d("WISHLIST", "Loaded " + wishlistProductIds.size() + " wishlist items");
                } else {
                    Log.w("WISHLIST", "Wishlist response not successful");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<WishlistItem>>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("WISHLIST", "Failed to fetch wishlist", t);
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
                        Toast.makeText(TrangChuFragment.this.getContext(), "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                        // Refresh wishlist to update UI
                        fetchWishlist();
                    } else {
                        Log.e("WISHLIST", "Server error: " + response.body().getMessage());
                        Toast.makeText(TrangChuFragment.this.getContext(), "Lỗi: " + response.body().getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("WISHLIST", "Error response: " + errorBody);
                    } catch (Exception e) {
                        Log.e("WISHLIST", "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<WishlistItem>> call, Throwable t) {
                Log.e("WISHLIST", "Network error: " + t.getMessage(), t);
            }
        });
    }
    private void setupApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ProductApiService.class);
        brandApiService = ApiClient.getBrandService();
        categoryApiService = ApiClient.getCategoryService();
    }

    private void fetchProducts() {
        Call<List<Product>> call = apiService.getAllProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!isAdded())
                    return;
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Không tải được sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (!isAdded())
                    return;
                Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchBrands() {
        brandApiService.getAllBrands().enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, Response<List<Brand>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    brandList.clear();
                    brandList.addAll(response.body());
                    brandAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                if (isAdded()) {
                    Log.e("HOME", "Failed to fetch brands", t);
                }
            }
        });
    }

    private void fetchCategories() {
        categoryApiService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                if (isAdded()) {
                    Log.e("HOME", "Failed to fetch categories", t);
                }
            }
        });
    }

    private void filterProducts(String category, String brand) {
        Call<List<Product>> call = apiService.getFilteredProducts(category, brand);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Không tìm thấy sản phẩm nào", Toast.LENGTH_SHORT).show();
                    productList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
