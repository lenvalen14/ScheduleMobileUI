package com.example.schedulemedical.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.model.Specialty;
import com.example.schedulemedical.utils.SpecialtyIconUtil;

import java.util.List;

public class SpecialtyAdapter extends RecyclerView.Adapter<SpecialtyAdapter.SpecialtyViewHolder> {
    private Context context;
    private List<Specialty> specialties;
    private int selectedPosition = -1;
    private OnSpecialtySelectedListener listener;
    
    public interface OnSpecialtySelectedListener {
        void onSpecialtySelected(Specialty specialty, int position);
    }
    
    public SpecialtyAdapter(Context context, List<Specialty> specialties) {
        this.context = context;
        this.specialties = specialties;
    }
    
    public void setOnSpecialtySelectedListener(OnSpecialtySelectedListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public SpecialtyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_specialty, parent, false);
        return new SpecialtyViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SpecialtyViewHolder holder, int position) {
        Specialty specialty = specialties.get(position);
        
        holder.tvSpecialtyName.setText(specialty.getName());
        
        if (specialty.getDescription() != null && !specialty.getDescription().isEmpty()) {
            holder.tvSpecialtyDescription.setText(specialty.getDescription());
            holder.tvSpecialtyDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvSpecialtyDescription.setVisibility(View.GONE);
        }
        
        holder.tvDoctorCount.setText(specialty.getDoctorCountText());

        // Log tên chuyên khoa gốc và tên chuẩn hóa
        Log.d("SpecialtyAdapter", "Specialty name raw: '" + specialty.getName() + "', normalized: '" + (specialty.getName() != null ? specialty.getName().trim().toLowerCase() : "") + "'");

        // Map icon và background: luôn trim và toLowerCase tên chuyên khoa
        String name = specialty.getName() != null ? specialty.getName().trim().toLowerCase() : "";
        int iconResId = SpecialtyIconUtil.getIconResId(name);
        int bgResId = SpecialtyIconUtil.getBackgroundResId(name);
        ImageView ivIcon = holder.itemView.findViewById(R.id.ivSpecialtyIcon);
        if (ivIcon != null) {
            ivIcon.setImageResource(iconResId);
            View flIconBg = (View) ivIcon.getParent();
            if (flIconBg != null) {
                flIconBg.setBackgroundResource(bgResId);
            }
        } else {
            holder.itemView.setBackgroundResource(bgResId);
        }

        // Handle selection
        boolean isSelected = position == selectedPosition;
        holder.itemView.setSelected(isSelected);
        holder.iconSelected.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            if (previousPosition != -1) {
                notifyItemChanged(previousPosition);
            }
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onSpecialtySelected(specialty, selectedPosition);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return specialties != null ? specialties.size() : 0;
    }
    
    public Specialty getSelectedSpecialty() {
        if (selectedPosition >= 0 && selectedPosition < specialties.size()) {
            return specialties.get(selectedPosition);
        }
        return null;
    }
    
    public void updateSpecialties(List<Specialty> newSpecialties) {
        this.specialties = newSpecialties;
        this.selectedPosition = -1; // Reset selection
        notifyDataSetChanged();
    }
    
    static class SpecialtyViewHolder extends RecyclerView.ViewHolder {
        TextView tvSpecialtyName, tvSpecialtyDescription, tvDoctorCount;
        ImageView iconSelected;
        
        public SpecialtyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSpecialtyName = itemView.findViewById(R.id.tvSpecialtyName);
            tvSpecialtyDescription = itemView.findViewById(R.id.tvSpecialtyDescription);
            tvDoctorCount = itemView.findViewById(R.id.tvDoctorCount);
            iconSelected = itemView.findViewById(R.id.iconSelected);
        }
    }
} 