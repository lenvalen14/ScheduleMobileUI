package com.example.schedulemedical.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.ui.hospital.HospitalActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {
    private Context context;
    private List<HospitalResponse> hospitalList;
    private List<HospitalResponse> filteredList;
    private OnHospitalClickListener onHospitalClickListener;
    
    public interface OnHospitalClickListener {
        void onHospitalClick(HospitalResponse hospital);
        void onBookNowClick(HospitalResponse hospital);
    }
    
    public HospitalAdapter(HospitalActivity context, List<HospitalResponse> hospitalList) {
        this.context = context;
        this.hospitalList = hospitalList != null ? hospitalList : new ArrayList<>();
        this.filteredList = new ArrayList<>(this.hospitalList);
    }
    
    public void setOnHospitalClickListener(OnHospitalClickListener listener) {
        this.onHospitalClickListener = listener;
    }
    
    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_hospital_detail, parent, false);
        return new HospitalViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        HospitalResponse hospital = filteredList.get(position);
        holder.bind(hospital);
    }
    
    @Override
    public int getItemCount() {
        return filteredList.size();
    }
    
    public void updateHospitals(List<HospitalResponse> newHospitals) {
        this.hospitalList.clear();
        if (newHospitals != null) {
            this.hospitalList.addAll(newHospitals);
        }
        this.filteredList.clear();
        this.filteredList.addAll(this.hospitalList);
        notifyDataSetChanged();
    }
    
    public void addHospitals(List<HospitalResponse> newHospitals) {
        if (newHospitals != null && !newHospitals.isEmpty()) {
            int startPosition = this.filteredList.size();
            this.hospitalList.addAll(newHospitals);
            this.filteredList.addAll(newHospitals);
            notifyItemRangeInserted(startPosition, newHospitals.size());
        }
    }
    
    public void filter(String query) {
        filteredList.clear();
        
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(hospitalList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            
            for (HospitalResponse hospital : hospitalList) {
                boolean matches = false;
                
                // Search in hospital name
                if (hospital.getName() != null && 
                    hospital.getName().toLowerCase().contains(lowerCaseQuery)) {
                    matches = true;
                }
                
                // Search in address
                if (!matches && hospital.getAddress() != null && 
                    hospital.getAddress().toLowerCase().contains(lowerCaseQuery)) {
                    matches = true;
                }
                
                // Search in type
                if (!matches && hospital.getType() != null && 
                    hospital.getType().toLowerCase().contains(lowerCaseQuery)) {
                    matches = true;
                }
                
                // Search in description
                if (!matches && hospital.getDescription() != null && 
                    hospital.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    matches = true;
                }
                
                if (matches) {
                    filteredList.add(hospital);
                }
            }
        }
        
        notifyDataSetChanged();
    }
    
    public void filterByType(String type) {
        filteredList.clear();
        
        if (type == null || type.trim().isEmpty()) {
            filteredList.addAll(hospitalList);
        } else {
            for (HospitalResponse hospital : hospitalList) {
                if (hospital.getType() != null && 
                    hospital.getType().equalsIgnoreCase(type)) {
                    filteredList.add(hospital);
                }
            }
        }
        
        notifyDataSetChanged();
    }
    
    public void filterByRating(float minRating) {
        filteredList.clear();
        
        for (HospitalResponse hospital : hospitalList) {
            float rating = hospital.getRating() != null ? hospital.getRating() : 0f;
            if (rating >= minRating) {
                filteredList.add(hospital);
            }
        }
        
        notifyDataSetChanged();
    }
    
    class HospitalViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivHospitalLogo;
        private TextView tvHospitalName;
        private TextView tvHospitalAddress;
        private TextView tvRating;
        private TextView tvDistance;
        private TextView tvHospitalType;
        private MaterialButton btnBookNow;
        
        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivHospitalLogo = itemView.findViewById(R.id.ivHospitalLogo);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            tvHospitalAddress = itemView.findViewById(R.id.tvHospitalAddress);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvHospitalType = itemView.findViewById(R.id.tvHospitalType);
            btnBookNow = itemView.findViewById(R.id.btnBookNow);
            
            // Set click listeners
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onHospitalClickListener != null) {
                    onHospitalClickListener.onHospitalClick(filteredList.get(position));
                }
            });
            
            if (btnBookNow != null) {
                btnBookNow.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onHospitalClickListener != null) {
                        onHospitalClickListener.onBookNowClick(filteredList.get(position));
                    }
                });
            }
        }
        
        public void bind(HospitalResponse hospital) {
            // Hospital name
            if (tvHospitalName != null) {
                tvHospitalName.setText(hospital.getName() != null ? hospital.getName() : "N/A");
            }
            
            // Hospital address
            if (tvHospitalAddress != null) {
                tvHospitalAddress.setText(hospital.getAddress() != null ? hospital.getAddress() : "N/A");
            }
            
            // Rating
            if (tvRating != null) {
                String rating = hospital.getFormattedRating();
                tvRating.setText(rating);
            }
            
            // Distance
            if (tvDistance != null) {
                String distance = hospital.getFormattedDistance();
                if (!distance.isEmpty()) {
                    tvDistance.setText(distance);
                    tvDistance.setVisibility(View.VISIBLE);
                } else {
                    tvDistance.setVisibility(View.GONE);
                }
            }
            
            // Hospital type
            if (tvHospitalType != null) {
                tvHospitalType.setText(hospital.getType() != null ? hospital.getType() : "");
            }
            
            // Hospital logo
            if (ivHospitalLogo != null) {
                if (hospital.getLogo() != null && !hospital.getLogo().isEmpty()) {
                    Glide.with(context)
                            .load(hospital.getLogo())
                            .placeholder(R.drawable.logo_benh_vien_mat)
                            .error(R.drawable.logo_benh_vien_mat)
                            .into(ivHospitalLogo);
                } else {
                    ivHospitalLogo.setImageResource(R.drawable.logo_benh_vien_mat);
                }
            }
        }
    }
} 