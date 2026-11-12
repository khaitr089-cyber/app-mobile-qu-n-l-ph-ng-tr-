package com.example.quanlyphongtro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

import com.example.quanlyphongtro.R;
import com.example.quanlyphongtro.model.ModelDienNuoc;

public class DienNuocAdapter extends RecyclerView.Adapter<DienNuocAdapter.DienNuocViewHolder> {

    private Context context;
    private List<ModelDienNuoc> danhSachDienNuoc;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ModelDienNuoc item);
    }

    public DienNuocAdapter(Context context, List<ModelDienNuoc> danhSachDienNuoc, OnItemClickListener listener) {
        this.context = context;
        this.danhSachDienNuoc = danhSachDienNuoc;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DienNuocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dien_nuoc, parent, false);
        return new DienNuocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DienNuocViewHolder holder, int position) {
        ModelDienNuoc item = danhSachDienNuoc.get(position);
        if (item == null) return;

        holder.tvItemPhong.setText("Phòng: " + item.getMaPhong());
        holder.tvItemThangGhi.setText(item.getThangGhi());
        holder.tvItemSoDien.setText("Điện cũ/mới: " + item.getDienCu() + " / " + item.getDienMoi() + " kWh");
        holder.tvItemSoNuoc.setText("Nước cũ/mới: " + item.getNuocCu() + " / " + item.getNuocMoi() + " m3");
        holder.tvItemDichVuKhac.setText(String.format(Locale.getDefault(), "Tổng tiền: %,.0f VNĐ", item.getTongTien()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhSachDienNuoc != null ? danhSachDienNuoc.size() : 0;
    }

    public static class DienNuocViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemPhong, tvItemThangGhi, tvItemSoDien, tvItemSoNuoc, tvItemDichVuKhac;

        public DienNuocViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemPhong = itemView.findViewById(R.id.tvItemPhong);
            tvItemThangGhi = itemView.findViewById(R.id.tvItemThangGhi);
            tvItemSoDien = itemView.findViewById(R.id.tvItemSoDien);
            tvItemSoNuoc = itemView.findViewById(R.id.tvItemSoNuoc);
            tvItemDichVuKhac = itemView.findViewById(R.id.tvItemDichVuKhac);
        }
    }
}
