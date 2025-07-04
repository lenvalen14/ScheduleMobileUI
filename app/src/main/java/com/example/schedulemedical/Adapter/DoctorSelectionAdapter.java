package com.example.schedulemedical.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.model.Doctor;

import java.util.List;

public class DoctorSelectionAdapter extends RecyclerView.Adapter<DoctorSelectionAdapter.DoctorViewHolder> {
    private Context context;
    private List<Doctor> doctors;
    private int selectedPosition = -1;
    private OnDoctorSelectedListener listener;
    
    public interface OnDoctorSelectedListener {
        void onDoctorSelected(Doctor doctor, int position);
    }
    
    public DoctorSelectionAdapter(Context context, List<Doctor> doctors) {
        this.context = context;
        this.doctors = doctors;
    }
    
    public void setOnDoctorSelectedListener(OnDoctorSelectedListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor_selection, parent, false);
        return new DoctorViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        
        holder.tvDoctorName.setText(doctor.getFullName());
        holder.tvSpecialty.setText(doctor.getSpecialtyName());
        holder.tvHospital.setText(doctor.getHospitalName());
        holder.tvRating.setText(doctor.getRatingText());
        holder.tvExperience.setText(doctor.getExperienceText());
        
        // Set bio if available
        if (doctor.getBio() != null && !doctor.getBio().isEmpty()) {
            holder.tvBio.setText(doctor.getBio());
            holder.tvBio.setVisibility(View.VISIBLE);
        } else {
            holder.tvBio.setVisibility(View.GONE);
        }
        
        // Handle selection
        boolean isSelected = position == selectedPosition;
        holder.itemView.setSelected(isSelected);
        holder.iconSelected.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            
            // Notify previous selected item
            if (previousPosition != -1) {
                notifyItemChanged(previousPosition);
            }
            
            // Notify current selected item
            notifyItemChanged(selectedPosition);
            
            if (listener != null) {
                listener.onDoctorSelected(doctor, selectedPosition);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return doctors != null ? doctors.size() : 0;
    }
    
    public Doctor getSelectedDoctor() {
        if (selectedPosition >= 0 && selectedPosition < doctors.size()) {
            return doctors.get(selectedPosition);
        }
        return null;
    }
    
    public void updateDoctors(List<Doctor> newDoctors) {
        this.doctors = newDoctors;
        this.selectedPosition = -1; // Reset selection
        notifyDataSetChanged();
    }
    
    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDoctor, iconSelected;
        TextView tvDoctorName, tvSpecialty, tvHospital, tvRating, tvExperience, tvBio;
        
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDoctor = itemView.findViewById(R.id.imgDoctor);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvHospital = itemView.findViewById(R.id.tvHospital);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvExperience = itemView.findViewById(R.id.tvExperience);
            tvBio = itemView.findViewById(R.id.tvBio);
            iconSelected = itemView.findViewById(R.id.iconSelected);
        }
    }
} 