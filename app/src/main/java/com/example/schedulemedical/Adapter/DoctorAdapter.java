package com.example.schedulemedical.Adapter;

import android.content.Context;
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
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;
import com.example.schedulemedical.model.dto.response.UserResponse;

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

                UserResponse user = doctor.getUser();
                SpecialtyResponse specialty = doctor.getSpecialty();
                HospitalResponse hospital = doctor.getHospital();

                if (user != null && user.getFullName() != null &&
                        user.getFullName().toLowerCase().contains(lowerCaseQuery)) {
                    matches = true;
                }

                if (!matches && specialty != null && specialty.getName() != null &&
                        specialty.getName().toLowerCase().contains(lowerCaseQuery)) {
                    matches = true;
                }

                if (!matches && hospital != null && hospital.getName() != null &&
                        hospital.getName().toLowerCase().contains(lowerCaseQuery)) {
                    matches = true;
                }

                if (matches) {
                    filteredList.add(doctor);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void filterBySpecialty(String specialtyName) {
        filteredList.clear();

        if (specialtyName == null || specialtyName.trim().isEmpty()) {
            filteredList.addAll(doctorList);
        } else {
            for (DoctorResponse doctor : doctorList) {
                SpecialtyResponse specialty = doctor.getSpecialty();
                if (specialty != null && specialty.getName() != null &&
                        specialty.getName().equalsIgnoreCase(specialtyName)) {
                    filteredList.add(doctor);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void filterByRating(float minRating) {
        filteredList.clear();

        for (DoctorResponse doctor : doctorList) {
            HospitalResponse hospital = doctor.getHospital();
            try {
                float rating = hospital != null && hospital.getRating() != null
                        ? hospital.getRating().floatValue() : 0f;
                if (rating >= minRating) {
                    filteredList.add(doctor);
                }
            } catch (Exception e) {
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
            UserResponse user = doctor.getUser();
            SpecialtyResponse specialty = doctor.getSpecialty();
            HospitalResponse hospital = doctor.getHospital();

            // Name
            tvDoctorName.setText(user != null && user.getFullName() != null ? user.getFullName() : "N/A");

            // Specialty
            tvSpecialty.setText(specialty != null && specialty.getName() != null ? specialty.getName() : "General");

            // Hospital
            tvHospital.setText(hospital != null && hospital.getName() != null ? hospital.getName() : "Hospital");

            // Experience
            String exp = doctor.getYearsOfExperience() != null ? doctor.getYearsOfExperience() + " năm" : "N/A";
            tvExperience.setText(exp);

            // Rating
            float rating = hospital != null && hospital.getRating() != null ? hospital.getRating().floatValue() : 0f;
            ratingBar.setRating(rating);
            tvRating.setText(String.format("%.1f", rating));

            // Reviews
            int reviewCount = hospital != null && hospital.getReviews() != null ? hospital.getReviews() : 0;
            tvReviews.setText("(" + reviewCount + " reviews)");

            // Fee
//            String fee = doctor.getConsultationFee() != null ? doctor.getConsultationFee() + " VND" : "Chưa có giá";
//            tvConsultationFee.setText(fee);

            // Avatar
            if (user != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Glide.with(context)
                        .load(user.getAvatar())
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
