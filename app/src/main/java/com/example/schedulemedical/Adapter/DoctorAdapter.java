package com.example.schedulemedical.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
// import com.example.schedulemedical.ui.doctor.DoctorProfileActivity; // Commented out - activity not created yet

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {
    private Context context;
    private List<DoctorResponse> doctorList;
    private List<DoctorResponse> filteredList;
    private OnDoctorClickListener onDoctorClickListener;
    
    public interface OnDoctorClickListener {
        void onDoctorClick(DoctorResponse doctor);
        void onBookAppointmentClick(DoctorResponse doctor);
    }
    
    public DoctorAdapter(Context context, List<DoctorResponse> doctorList) {
        this.context = context;
        this.doctorList = doctorList != null ? doctorList : new ArrayList<>();
        this.filteredList = new ArrayList<>(this.doctorList);
    }
    
    public void setOnDoctorClickListener(OnDoctorClickListener listener) {
        this.onDoctorClickListener = listener;
    }
    
    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        DoctorResponse doctor = filteredList.get(position);
        holder.bind(doctor);
    }
    
    @Override
    public int getItemCount() {
        return filteredList.size();
    }
    
    public void updateDoctors(List<DoctorResponse> newDoctors) {
        this.doctorList.clear();
        if (newDoctors != null) {
            this.doctorList.addAll(newDoctors);
        }
        this.filteredList.clear();
        this.filteredList.addAll(this.doctorList);
        notifyDataSetChanged();
    }
    
    public void filter(String query) {
        filteredList.clear();
        
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(doctorList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            
            for (DoctorResponse doctor : doctorList) {
                boolean matches = false;
                
                // Search in doctor name
                if (doctor.getFullName() != null && 
                    doctor.getFullName().toLowerCase().contains(lowerCaseQuery)) {
                    matches = true;
                }
                
                // Search in specialty
                if (!matches && doctor.getSpecialtyName() != null && 
                    doctor.getSpecialtyName().toLowerCase().contains(lowerCaseQuery)) {
                    matches = true;
                }
                
                // Search in hospital name
                if (!matches && doctor.getHospitalName() != null && 
                    doctor.getHospitalName().toLowerCase().contains(lowerCaseQuery)) {
                    matches = true;
                }
                
                // Search in department
                if (!matches && doctor.getDepartmentName() != null && 
                    doctor.getDepartmentName().toLowerCase().contains(lowerCaseQuery)) {
                    matches = true;
                }
                
                if (matches) {
                    filteredList.add(doctor);
                }
            }
        }
        
        notifyDataSetChanged();
    }
    
    public void filterBySpecialty(String specialty) {
        filteredList.clear();
        
        if (specialty == null || specialty.trim().isEmpty()) {
            filteredList.addAll(doctorList);
        } else {
            for (DoctorResponse doctor : doctorList) {
                if (doctor.getSpecialtyName() != null && 
                    doctor.getSpecialtyName().equalsIgnoreCase(specialty)) {
                    filteredList.add(doctor);
                }
            }
        }
        
        notifyDataSetChanged();
    }
    
    public void filterByRating(float minRating) {
        filteredList.clear();
        
        for (DoctorResponse doctor : doctorList) {
            try {
                float rating = Float.parseFloat(doctor.getRating() != null ? doctor.getRating() : "0");
                if (rating >= minRating) {
                    filteredList.add(doctor);
                }
            } catch (NumberFormatException e) {
                // If rating is not a valid number, include doctor if minRating is 0
                if (minRating == 0) {
                    filteredList.add(doctor);
                }
            }
        }
        
        notifyDataSetChanged();
    }
    
    class DoctorViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivDoctorAvatar;
        private TextView tvDoctorName;
        private TextView tvSpecialty;
        private TextView tvHospital;
        private TextView tvExperience;
        private RatingBar ratingBar;
        private TextView tvRating;
        private TextView tvReviews;
        private TextView tvConsultationFee;
        private View btnBookAppointment;
        
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivDoctorAvatar = itemView.findViewById(R.id.ivDoctorAvatar);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvHospital = itemView.findViewById(R.id.tvHospital);
            tvExperience = itemView.findViewById(R.id.tvExperience);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReviews = itemView.findViewById(R.id.tvReviews);
            tvConsultationFee = itemView.findViewById(R.id.tvConsultationFee);
            btnBookAppointment = itemView.findViewById(R.id.btnBookAppointment);
            
            // Set click listeners
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onDoctorClickListener != null) {
                    onDoctorClickListener.onDoctorClick(filteredList.get(position));
                }
            });
            
            if (btnBookAppointment != null) {
                btnBookAppointment.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onDoctorClickListener != null) {
                        onDoctorClickListener.onBookAppointmentClick(filteredList.get(position));
                    }
                });
            }
        }
        
        public void bind(DoctorResponse doctor) {
            // Doctor name
            if (tvDoctorName != null) {
                tvDoctorName.setText(doctor.getFullName() != null ? doctor.getFullName() : "N/A");
            }
            
            // Specialty
            if (tvSpecialty != null) {
                tvSpecialty.setText(doctor.getSpecialtyName() != null ? doctor.getSpecialtyName() : "General");
            }
            
            // Hospital
            if (tvHospital != null) {
                tvHospital.setText(doctor.getHospitalName() != null ? doctor.getHospitalName() : "Hospital");
            }
            
            // Experience
            if (tvExperience != null) {
                String experience = doctor.getExperience() != null ? doctor.getExperience() + " years" : "N/A";
                tvExperience.setText(experience);
            }
            
            // Rating
            if (ratingBar != null && tvRating != null) {
                try {
                    float rating = Float.parseFloat(doctor.getRating() != null ? doctor.getRating() : "0");
                    ratingBar.setRating(rating);
                    tvRating.setText(String.format("%.1f", rating));
                } catch (NumberFormatException e) {
                    ratingBar.setRating(0);
                    tvRating.setText("0.0");
                }
            }
            
            // Reviews count
            if (tvReviews != null) {
                int reviewCount = doctor.getTotalReviews() != null ? doctor.getTotalReviews() : 0;
                tvReviews.setText("(" + reviewCount + " reviews)");
            }
            
            // Consultation fee
            if (tvConsultationFee != null) {
                String fee = doctor.getConsultationFee() != null ? 
                           doctor.getConsultationFee() + " VND" : "Consultation fee";
                tvConsultationFee.setText(fee);
            }
            
            // Doctor avatar
            if (ivDoctorAvatar != null) {
                if (doctor.getAvatarUrl() != null && !doctor.getAvatarUrl().isEmpty()) {
                    Glide.with(context)
                            .load(doctor.getAvatarUrl())
                            .transform(new CircleCrop())
                            .placeholder(R.drawable.sample_profile_image)
                            .error(R.drawable.sample_profile_image)
                            .into(ivDoctorAvatar);
                } else {
                    ivDoctorAvatar.setImageResource(R.drawable.sample_profile_image);
                }
            }
        }
    }
} 