package com.example.schedulemedical.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;
import com.example.schedulemedical.utils.SpecialtyIconUtil;

import java.util.ArrayList;
import java.util.List;

public class SpecialtyAdapterView extends RecyclerView.Adapter<SpecialtyAdapterView.SpecialtyViewHolder> {

    private List<SpecialtyResponse> specialties = new ArrayList<>();

    public void setSpecialties(List<SpecialtyResponse> specialties) {
        this.specialties = specialties;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SpecialtyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_specialty_view, parent, false);
        return new SpecialtyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialtyViewHolder holder, int position) {
        SpecialtyResponse item = specialties.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDescription.setText(item.getDescription());

        // Set icon và background động
        int iconResId = SpecialtyIconUtil.getIconResId(item.getName());
        int bgResId = SpecialtyIconUtil.getBackgroundResId(item.getName());
        holder.ivSpecialtyIcon.setImageResource(iconResId);
        holder.flIconBg.setBackgroundResource(bgResId);

        // Button click
        holder.btnBookNow.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Đặt lịch cho " + item.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return specialties.size();
    }

    static class SpecialtyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        Button btnBookNow;
        ImageView ivSpecialtyIcon;
        FrameLayout flIconBg;

        public SpecialtyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSpecialtyName);
            tvDescription = itemView.findViewById(R.id.tvSpecialtyDescription);
            btnBookNow = itemView.findViewById(R.id.btnBookNow);
            ivSpecialtyIcon = itemView.findViewById(R.id.ivSpecialtyIcon);
            flIconBg = itemView.findViewById(R.id.flIconBg);
        }
    }
}
