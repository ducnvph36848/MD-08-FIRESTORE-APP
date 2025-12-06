package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.Brand;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BrandApiService {
    @GET("api/brands")
    Call<List<Brand>> getAllBrands();
}
