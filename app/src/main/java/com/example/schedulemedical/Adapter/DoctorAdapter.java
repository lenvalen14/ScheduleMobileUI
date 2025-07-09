package com.example.schedulemedical.Adapter;

import static com.example.schedulemedical.utils.NavigationHelper.EXTRA_DOCTOR_ID;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;
import com.example.schedulemedical.model.dto.response.UserResponse;
import com.example.schedulemedical.ui.booking.BookingWizardActivity;
import com.example.schedulemedical.ui.doctorprofile.DoctorProfileActivity;

import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class DoctorAdapter extends ListAdapter<DoctorResponse, DoctorAdapter.DoctorViewHolder> {
    private final Context context;
    private OnDoctorClickListener onDoctorClickListener;
    private boolean useCircleAvatar = true;
    private List<DoctorResponse> originalList = new ArrayList<>();

    public interface OnDoctorClickListener {
        void onDoctorClick(DoctorResponse doctor);
        void onBookAppointmentClick(DoctorResponse doctor);
    }

    public DoctorAdapter(Context context) {
        super(new DiffUtil.ItemCallback<DoctorResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull DoctorResponse oldItem, @NonNull DoctorResponse newItem) {
                return oldItem.getDoctorId() == newItem.getDoctorId();
            }
            @Override
            public boolean areContentsTheSame(@NonNull DoctorResponse oldItem, @NonNull DoctorResponse newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.context = context;
    }

    public void setOnDoctorClickListener(OnDoctorClickListener listener) {
        this.onDoctorClickListener = listener;
    }

    public void setUseCircleAvatar(boolean useCircleAvatar) {
        this.useCircleAvatar = useCircleAvatar;
    }

    // Cập nhật dữ liệu gốc và submit cho ListAdapter
    public void updateDoctors(List<DoctorResponse> newDoctors) {
        if (newDoctors == null) newDoctors = new ArrayList<>();
        this.originalList = new ArrayList<>(newDoctors);
        submitList(new ArrayList<>(originalList));
    }

    // Lọc theo query
    public void filter(String query) {
        List<DoctorResponse> filtered = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            filtered.addAll(originalList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (DoctorResponse doctor : originalList) {
                boolean matches = false;
                UserResponse user = doctor.getUser();
                SpecialtyResponse specialty = doctor.getSpecialty();
                HospitalResponse hospital = doctor.getHospital();
                if (user != null && user.getFullName() != null && user.getFullName().toLowerCase().contains(lowerCaseQuery)) matches = true;
                if (!matches && specialty != null && specialty.getName() != null && specialty.getName().toLowerCase().contains(lowerCaseQuery)) matches = true;
                if (!matches && hospital != null && hospital.getName() != null && hospital.getName().toLowerCase().contains(lowerCaseQuery)) matches = true;
                if (matches) filtered.add(doctor);
            }
        }
        submitList(filtered);
    }

    // Lọc theo chuyên khoa
    public void filterBySpecialty(String specialtyName) {
        List<DoctorResponse> filtered = new ArrayList<>();
        if (specialtyName == null || specialtyName.trim().isEmpty()) {
            filtered.addAll(originalList);
        } else {
            for (DoctorResponse doctor : originalList) {
                SpecialtyResponse specialty = doctor.getSpecialty();
                if (specialty != null && specialty.getName() != null && specialty.getName().equalsIgnoreCase(specialtyName)) {
                    filtered.add(doctor);
                }
            }
        }
        submitList(filtered);
    }

    // Lọc theo rating
    public void filterByRating(float minRating) {
        List<DoctorResponse> filtered = new ArrayList<>();
        for (DoctorResponse doctor : originalList) {
            Float rating = doctor.getRating();
            if (rating == null && minRating == 0) {
                filtered.add(doctor);
            } else if (rating != null && rating >= minRating) {
                filtered.add(doctor);
            }
        }
        submitList(filtered);
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class DoctorViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivDoctorPhoto;
        private final TextView tvDoctorName, tvSpecialty, tvHospital, tvExperience, tvRating;
        private final RatingBar ratingBar;
        private final View btnBookAppointment;

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
                    Intent intent = new Intent(context, DoctorProfileActivity.class);
                    intent.putExtra(EXTRA_DOCTOR_ID, getItem(position).getUser().getUserId());
                    context.startActivity(intent);
                }
            });
            if (btnBookAppointment != null) {
                btnBookAppointment.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = BookingWizardActivity.createIntentWithDoctor(context, getItem(position));
                        context.startActivity(intent);
                    }
                });
            }
        }
        public void bind(DoctorResponse doctor) {
            UserResponse user = doctor.getUser();
            tvDoctorName.setText(user != null ? user.getFullName() : "");
            tvSpecialty.setText(doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : "");
            tvHospital.setText(doctor.getHospital() != null ? doctor.getHospital().getName() : "");
            tvExperience.setText(doctor.getYearsOfExperience() != null ? doctor.getYearsOfExperience() + " năm kinh nghiệm" : "");
            Float rating = doctor.getRating();
            if (rating != null) {
                ratingBar.setRating(rating);
                tvRating.setText(String.valueOf(rating));
            } else {
                ratingBar.setRating(0);
                tvRating.setText("-");
            }
            String avatarUrl = user != null ? user.getAvatar() : null;
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                if (useCircleAvatar) {
                    Glide.with(context).load(avatarUrl).transform(new CircleCrop()).into(ivDoctorPhoto);
                } else {
                    Glide.with(context).load(avatarUrl).into(ivDoctorPhoto);
                }
            } else {
                ivDoctorPhoto.setImageResource(R.drawable.ic_doctor_placeholder);
            }
        }
    }
}
