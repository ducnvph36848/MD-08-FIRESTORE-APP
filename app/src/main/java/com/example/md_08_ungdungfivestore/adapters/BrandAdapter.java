package com.example.md_08_ungdungfivestore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.Brand;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private Context context;
    private List<Brand> brandList;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Brand brand);
    }

    public BrandAdapter(Context context, List<Brand> brandList, OnItemClickListener listener) {
        this.context = context;
        this.brandList = brandList;
        this.listener = listener;
    }

    public void setBrandList(List<Brand> brandList) {
        this.brandList = brandList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_filter_chip, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brand brand = brandList.get(position);
        holder.tvName.setText(brand.getName());

        if (selectedPosition == position) {
            ((androidx.cardview.widget.CardView)holder.itemView).setCardBackgroundColor(android.graphics.Color.parseColor("#E91E63")); // Selected color
        } else {
            ((androidx.cardview.widget.CardView)holder.itemView).setCardBackgroundColor(android.graphics.Color.parseColor("#2A2A2A")); // Default color
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
            if (listener != null) {
                listener.onItemClick(brand);
            }
        });
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    public static class BrandViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
