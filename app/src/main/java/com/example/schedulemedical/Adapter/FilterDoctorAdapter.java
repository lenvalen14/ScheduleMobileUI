package com.example.schedulemedical.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;
import com.example.schedulemedical.model.dto.response.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class FilterDoctorAdapter extends RecyclerView.Adapter<FilterDoctorAdapter.DoctorViewHolder> {

    private final Context context;
    private final List<DoctorResponse> doctorList = new ArrayList<>();
    private OnDoctorClickListener listener;

    public interface OnDoctorClickListener {
        void onDoctorClick(DoctorResponse doctor);
        void onBookAppointmentClick(DoctorResponse doctor);
    }

    public void setOnDoctorClickListener(OnDoctorClickListener listener) {
        this.listener = listener;
    }

    public FilterDoctorAdapter(Context context) {
        this.context = context;
    }

    public void updateDoctors(List<DoctorResponse> doctors) {
        doctorList.clear();
        doctorList.addAll(doctors);
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
        holder.bind(doctorList.get(position));
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvDoctorSpecialty, tvHospitalName;
        TextView tvRating;
        ImageView ivDoctorPhoto;
        ImageButton btnInfo;
        View btnBookAppointment;

        DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDoctorSpecialty = itemView.findViewById(R.id.tvDoctorSpecialty);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            ivDoctorPhoto = itemView.findViewById(R.id.ivDoctorPhoto);
            tvRating = itemView.findViewById(R.id.ratingContainer).findViewById(R.id.tvRating);
            btnInfo = itemView.findViewById(R.id.btnInfo);
            btnBookAppointment = itemView.findViewById(R.id.btnBookAppointment);
        }

        void bind(DoctorResponse doctor) {
            UserResponse user = doctor.getUser();
            SpecialtyResponse specialty = doctor.getSpecialty();
            HospitalResponse hospital = doctor.getHospital();

            // Doctor name
            tvDoctorName.setText(user != null && user.getFullName() != null ? user.getFullName() : "N/A");

            // Specialty
            tvDoctorSpecialty.setText(specialty != null && specialty.getName() != null
                    ? specialty.getName() : "Chưa rõ chuyên khoa");

            // Hospital
            tvHospitalName.setText(hospital != null && hospital.getName() != null
                    ? hospital.getName() : "Không rõ bệnh viện");

            // Avatar
            String avatarUrl = user != null ? user.getAvatar() : null;
            Glide.with(context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.sample_profile_image)
                    .error(R.drawable.sample_profile_image)
                    .into(ivDoctorPhoto);

            // Rating
            float rating = hospital != null && hospital.getRating() != null
                    ? hospital.getRating().floatValue() : 0f;
            int reviews = hospital != null && hospital.getReviews() != null ? hospital.getReviews() : 0;
            tvRating.setText(String.format("%.1f (%d reviews)", rating, reviews));

            // Click events
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onDoctorClick(doctor);
            });

            if (btnBookAppointment != null) {
                btnBookAppointment.setOnClickListener(v -> {
                    if (listener != null) listener.onBookAppointmentClick(doctor);
                });
            }
        }
    }
}
