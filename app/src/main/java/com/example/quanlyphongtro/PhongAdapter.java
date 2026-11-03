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

    public PhongAdapter(Context context, List<ModelPhong> listPhong) {
        this.context = context;
        this.listPhong = listPhong;
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

        // Đổi màu badge tùy theo trạng thái
        if (phong.getTrangThai().toLowerCase().contains("trống")) {
            holder.tvTrangThai.setBackgroundResource(R.drawable.badge_green);
        } else {
            holder.tvTrangThai.setBackgroundResource(R.drawable.badge_purple);
        }
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
