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
    private boolean useCircleAvatar = true;

    public interface OnDoctorClickListener {
        void onDoctorClick(DoctorResponse doctor);
        void onBookAppointmentClick(DoctorResponse doctor);
    }

    public DoctorAdapter(Context context, List<DoctorResponse> doctorList) {
        this.context = context;
        this.doctorList = doctorList != null ? doctorList : new ArrayList<>();
        this.filteredList = new ArrayList<>(this.doctorList);
    }

    public DoctorAdapter(Context context, List<DoctorResponse> doctorList, boolean useCircleAvatar) {
        this(context, doctorList);
        this.useCircleAvatar = useCircleAvatar;
    }

    public DoctorAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    public void setOnDoctorClickListener(OnDoctorClickListener listener) {
        this.onDoctorClickListener = listener;
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
            Float rating = doctor.getRating();
            if (rating == null && minRating == 0) {
                filteredList.add(doctor);
            } else if (rating != null && rating >= minRating) {
                filteredList.add(doctor);
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        holder.bind(filteredList.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    class DoctorViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivDoctorPhoto;
        private TextView tvDoctorName, tvSpecialty, tvHospital, tvExperience, tvRating;
        private RatingBar ratingBar;
        private View btnBookAppointment;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            ivDoctorPhoto = itemView.findViewById(R.id.ivDoctorPhoto);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvDoctorSpecialty);
            tvHospital = itemView.findViewById(R.id.tvHospitalName);
            tvExperience = itemView.findViewById(R.id.tvExperience);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvRating = itemView.findViewById(R.id.tvRatingValue);
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

            tvDoctorName.setText(user != null && user.getFullName() != null ? user.getFullName() : "N/A");
            tvSpecialty.setText(specialty != null && specialty.getName() != null ? specialty.getName() : "General");
            tvHospital.setText(hospital != null && hospital.getName() != null ? hospital.getName() : "Hospital");

            String exp = doctor.getYearsOfExperience() != null ? doctor.getYearsOfExperience() + " năm" : "0 năm";
            tvExperience.setText(exp);

            Float rating = doctor.getRating();
            float safeRating = rating != null ? rating : 0f;
            ratingBar.setRating(safeRating);
            tvRating.setText(String.format("%.1f", safeRating));

            if (user != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                if (useCircleAvatar) {
                    Glide.with(context)
                            .load(user.getAvatar())
                            .transform(new CircleCrop())
                            .placeholder(R.drawable.sample_profile_image)
                            .error(R.drawable.sample_profile_image)
                            .into(ivDoctorPhoto);
                } else {
                    Glide.with(context)
                            .load(user.getAvatar())
                            .placeholder(R.drawable.sample_profile_image)
                            .error(R.drawable.sample_profile_image)
                            .into(ivDoctorPhoto);
                }
            } else {
                ivDoctorPhoto.setImageResource(R.drawable.sample_profile_image);
            }
        }
    }
}
