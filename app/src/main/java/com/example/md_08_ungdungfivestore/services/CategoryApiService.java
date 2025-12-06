package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.Category;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryApiService {
    @GET("api/categories")
    Call<List<Category>> getAllCategories();
}
