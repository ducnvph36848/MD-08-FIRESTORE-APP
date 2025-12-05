package com.example.md_08_ungdungfivestore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.md_08_ungdungfivestore.ManDatHang;
import com.example.md_08_ungdungfivestore.ManDonHang;
import com.example.md_08_ungdungfivestore.R;

public class TrangCaNhanFragment extends Fragment {


    private LinearLayout btnDonHang, btnTheNganHang, btnThongTinCaNhan, btnLienHe, btnDangXuat;

    public TrangCaNhanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trang_ca_nhan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        anhXa(view);
        setupListeners();
    }

    private void anhXa(View view) {

        btnDonHang = view.findViewById(R.id.btnDonHang);
        btnTheNganHang = view.findViewById(R.id.btnTheNganHang);
        btnThongTinCaNhan = view.findViewById(R.id.btnThongTinCaNhan);
        btnLienHe = view.findViewById(R.id.btnLienHe);
        btnDangXuat = view.findViewById(R.id.btnDangXuat);
    }

    private void setupListeners() {

        // 1. Chuyển sang màn Đơn hàng
        btnDonHang.setOnClickListener(
                v -> {
                    Log.d("A","START");
                    this.getContext().startActivity(new Intent(this.getContext(), ManDonHang.class));
                });
        // 2. Thẻ ngân hàng (chưa làm)
        btnTheNganHang.setOnClickListener(v -> Toast
                .makeText(getContext(), "Tính năng Thẻ ngân hàng đang phát triển", Toast.LENGTH_SHORT).show());

        // 3. Mở trang cá nhân
        btnThongTinCaNhan.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.example.md_08_ungdungfivestore.ManThongTinCaNhan.class);
            startActivity(intent);
        });

        // 4. Mở màn liên hệ
        btnLienHe.setOnClickListener(
                v -> Toast.makeText(getContext(), "Tính năng Liên hệ đang phát triển", Toast.LENGTH_SHORT).show());
        // 5. Đăng xuất
        btnDangXuat.setOnClickListener(v -> {
            // Clear token
            com.example.md_08_ungdungfivestore.utils.TokenManager tokenManager = new com.example.md_08_ungdungfivestore.utils.TokenManager(
                    getContext());
            tokenManager.clearToken();

            // Navigate to Login
            android.content.Intent intent = new android.content.Intent(getContext(),
                    com.example.md_08_ungdungfivestore.DangNhap.class);
            intent.setFlags(
                    android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        });
    }
}