package com.example.quanlyphongtro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.example.quanlyphongtro.R;
import com.example.quanlyphongtro.model.ModelNguoiThue;

public class NguoiThueAdapter extends RecyclerView.Adapter<NguoiThueAdapter.NguoiThueViewHolder> {

    private Context context;
    private List<ModelNguoiThue> listNguoiThue;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ModelNguoiThue nguoiThue);
    }

    public NguoiThueAdapter(Context context, List<ModelNguoiThue> listNguoiThue, OnItemClickListener listener) {
        this.context = context;
        this.listNguoiThue = listNguoiThue;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NguoiThueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nguoi_thue, parent, false);
        return new NguoiThueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NguoiThueViewHolder holder, int position) {
        ModelNguoiThue item = listNguoiThue.get(position);
        if (item == null) return;

        holder.tvHoTen.setText("Họ tên: " + item.getHoTen());
        holder.tvSdt.setText("SĐT: " + item.getSoDienThoai());
        holder.tvPhong.setText("Phòng: " + item.getPhongThue());
        
        String ngayVao = item.getNgayChuyenVao() != null && !item.getNgayChuyenVao().isEmpty() ? item.getNgayChuyenVao() : "Chưa cập nhật";
        holder.tvNgayVao.setText("Chuyển vào: " + ngayVao);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNguoiThue != null ? listNguoiThue.size() : 0;
    }

    public static class NguoiThueViewHolder extends RecyclerView.ViewHolder {
        TextView tvHoTen, tvSdt, tvPhong, tvNgayVao;

        public NguoiThueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHoTen = itemView.findViewById(R.id.tvItemHoTen);
            tvSdt = itemView.findViewById(R.id.tvItemSdt);
            tvPhong = itemView.findViewById(R.id.tvItemPhong);
            tvNgayVao = itemView.findViewById(R.id.tvItemNgayVao);
        }
    }
}
