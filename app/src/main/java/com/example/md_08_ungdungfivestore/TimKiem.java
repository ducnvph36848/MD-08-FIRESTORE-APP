package com.example.md_08_ungdungfivestore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.adapters.ProductAdapter;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.services.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimKiem extends AppCompatActivity {

    private EditText edtSearch;
    private ImageButton btnBackSearch, btnSearchAction;
    private RecyclerView rcvProducts;
    private ProductAdapter productAdapter;
    private GridLayout gridSuggest;
    private LinearLayout layoutTextSuggest;
    private TextView tvTitleSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tim_kiem);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        setupRecycler();
        setupListeners();
    }

    private void initView() {
        edtSearch = findViewById(R.id.edtSearch);
        btnBackSearch = findViewById(R.id.btnBackSearch);
        btnSearchAction = findViewById(R.id.btnSearchAction);
        rcvProducts = findViewById(R.id.rcvProducts);
        gridSuggest = findViewById(R.id.gridSuggest);
        layoutTextSuggest = findViewById(R.id.layoutTextSuggest);
        tvTitleSuggestion = findViewById(R.id.tvTitleSuggestion);
    }

    private void setupRecycler() {
        rcvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        
        // Pass OnItemClickListener
        productAdapter = new ProductAdapter(this, new ArrayList<>(), new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                // Navigate to Detail
                Intent intent = new Intent(TimKiem.this, XemChiTiet.class);
                intent.putExtra("product", product); // Put object directly if Serializable/Parcelable
                startActivity(intent);
            }

            @Override
            public void onAddClick(Product product) {
                // Optional: Implement add to cart logic directly from search
                Toast.makeText(TimKiem.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(Product product) {
                // Not used in Search
            }
        });
        productAdapter.setShowDeleteButton(false); // Search screen doesn't need delete button
        rcvProducts.setAdapter(productAdapter);
    }

    private void setupListeners() {
        // Nút back
        btnBackSearch.setOnClickListener(v -> finish());

        // Sự kiện bàn phím Search
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(edtSearch.getText().toString());
                return true;
            }
            return false;
        });

        // Sự kiện click vào nút Search mới
        btnSearchAction.setOnClickListener(v -> performSearch(edtSearch.getText().toString()));
        
        // Setup Grid Suggestion Clicks
        if (gridSuggest != null) {
            for (int i = 0; i < gridSuggest.getChildCount(); i++) {
                View child = gridSuggest.getChildAt(i);
                if (child instanceof Button) {
                    child.setOnClickListener(this::onSuggestClick);
                }
            }
        }

        // Setup Text Suggestion Clicks
        if (layoutTextSuggest != null) {
             for (int i = 0; i < layoutTextSuggest.getChildCount(); i++) {
                View child = layoutTextSuggest.getChildAt(i);
                if (child instanceof TextView) {
                    child.setOnClickListener(this::onSuggestClick);
                }
            }
        }
    }

    private void performSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;
        
        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

        ApiClient.getProductService().searchProducts(keyword).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    updateList(products);
                    
                    if (products.isEmpty()) {
                        Toast.makeText(TimKiem.this, "Không tìm thấy sản phẩm nào", Toast.LENGTH_SHORT).show();
                        if (tvTitleSuggestion != null) tvTitleSuggestion.setText("Không tìm thấy sản phẩm nào");
                    } else {
                        // Ẩn gợi ý và đổi tiêu đề
                        if (gridSuggest != null) gridSuggest.setVisibility(View.GONE);
                        if (layoutTextSuggest != null) layoutTextSuggest.setVisibility(View.GONE);
                        if (tvTitleSuggestion != null) tvTitleSuggestion.setText("Sản phẩm phù hợp với từ khóa của bạn");
                    }
                } else {
                    Toast.makeText(TimKiem.this, "Lỗi tìm kiếm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(TimKiem.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TimKiem", "Error: " + t.getMessage());
            }
        });
    }

    private void updateList(List<Product> list) {
         productAdapter = new ProductAdapter(this, list, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                 Intent intent = new Intent(TimKiem.this, XemChiTiet.class);
                 intent.putExtra("idProduct", product.getId());
                 startActivity(intent);
            }

            @Override
            public void onAddClick(Product product) {
                 Toast.makeText(TimKiem.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(Product product) {
            }
        });
        productAdapter.setShowDeleteButton(false);
        rcvProducts.setAdapter(productAdapter);
    }

    public void onSuggestClick(View view) {
        if (view instanceof TextView) { // Button is subdomain of TextView
            String text = ((TextView) view).getText().toString();
            edtSearch.setText(text);
            performSearch(text);
        }
    }
}