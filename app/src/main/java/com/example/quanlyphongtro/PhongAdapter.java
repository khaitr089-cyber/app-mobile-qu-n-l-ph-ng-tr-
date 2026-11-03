package com.example.quanlyphongtro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PhongAdapter extends RecyclerView.Adapter<PhongAdapter.PhongViewHolder> {

    private Context context;
    private List<ModelPhong> listPhong;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ModelPhong phong);
    }

    public PhongAdapter(Context context, List<ModelPhong> listPhong, OnItemClickListener listener) {
        this.context = context;
        this.listPhong = listPhong;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_phong, parent, false);
        return new PhongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhongViewHolder holder, int position) {
        ModelPhong phong = listPhong.get(position);
        if (phong == null) return;

        holder.tvTenPhong.setText("Phòng " + phong.getMaPhong() + " - " + phong.getTenPhong());
        holder.tvGiaThue.setText("Giá: " + phong.getGiaThue() + " đ");
        holder.tvTrangThai.setText(" " + phong.getTrangThai() + " ");

        if (phong.getTrangThai().toLowerCase().contains("trống")) {
            holder.tvTrangThai.setBackgroundResource(R.drawable.badge_green);
        } else {
            holder.tvTrangThai.setBackgroundResource(R.drawable.badge_purple);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(phong);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPhong != null ? listPhong.size() : 0;
    }

    public static class PhongViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenPhong, tvGiaThue, tvTrangThai;

        public PhongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenPhong = itemView.findViewById(R.id.tvTenPhong);
            tvGiaThue = itemView.findViewById(R.id.tvGiaThue);
            tvTrangThai = itemView.findViewById(R.id.tvTrangThai);
        }
    }
}
