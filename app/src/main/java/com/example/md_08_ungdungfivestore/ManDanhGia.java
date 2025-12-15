package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.models.Comment;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.CommentApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManDanhGia extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgProduct;
    private TextView tvProductName;
    private RatingBar ratingBar;
    private EditText edtComment;
    private Button btnSubmit;

    private String productId;
    private String productName;
    private String productImage;
    private CommentApiService commentApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_danh_gia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();

        // Get data from intent
        productId = getIntent().getStringExtra("productId");
        productName = getIntent().getStringExtra("productName");
        productImage = getIntent().getStringExtra("productImage");

        if (productId == null) {
            Toast.makeText(this, "Lỗi sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup UI
        tvProductName.setText(productName);
        if (productImage != null && !productImage.startsWith("http")) {
            productImage = ApiClient.BASE_URL2 + productImage;
        }
        Glide.with(this).load(productImage).into(imgProduct);

        commentApiService = ApiClient.getClient().create(CommentApiService.class);

        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> submitReview());
    }

    private void anhXa() {
        btnBack = findViewById(R.id.btnBack);
        imgProduct = findViewById(R.id.imgProduct);
        tvProductName = findViewById(R.id.tvProductName);
        ratingBar = findViewById(R.id.ratingBar);
        edtComment = findViewById(R.id.edtComment);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void submitReview() {
        String content = edtComment.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        Comment comment = new Comment();
        comment.setProductId(productId);
        comment.setUserId("CURRENT_USER_ID"); // Backend should handle extracting user ID from token
        comment.setContent(content);
        comment.setRating((int) rating);

        commentApiService.addComment(comment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManDanhGia.this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ManDanhGia.this, "Lỗi gửi đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(ManDanhGia.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
