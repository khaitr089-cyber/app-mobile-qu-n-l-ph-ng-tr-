package com.example.quanlyphongtro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

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

        holder.tvHoTen.setText(item.getHoTen());
        holder.tvSdt.setText("SĐT: " + item.getSoDienThoai());
        holder.tvCccd.setText("CCCD: " + item.getCccd());
        holder.tvPhongThue.setText("Phòng thuê: " + item.getPhongThue());
        holder.tvQueQuan.setText("Quê quán: " + item.getQueQuan());

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
        TextView tvHoTen, tvSdt, tvCccd, tvPhongThue, tvQueQuan;

        public NguoiThueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHoTen = itemView.findViewById(R.id.tvHoTen);
            tvSdt = itemView.findViewById(R.id.tvSdt);
            tvCccd = itemView.findViewById(R.id.tvCccd);
            tvPhongThue = itemView.findViewById(R.id.tvPhongThue);
            tvQueQuan = itemView.findViewById(R.id.tvQueQuan);
        }
    }
}
