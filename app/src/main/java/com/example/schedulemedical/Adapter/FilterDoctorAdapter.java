package com.example.schedulemedical.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    // Listener interface cho các hành động từ view
    public interface OnDoctorClickListener {
        void onDoctorClick(DoctorResponse doctor); // Xem chi tiết
        void onBookAppointmentClick(DoctorResponse doctor); // Đặt lịch
    }

    public void setOnDoctorClickListener(OnDoctorClickListener listener) {
        this.listener = listener;
    }

    public FilterDoctorAdapter(Context context) {
        this.context = context;
    }

    public void updateDoctors(List<DoctorResponse> doctors) {
        doctorList.clear();
        if (doctors != null) {
            doctorList.addAll(doctors);
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
        holder.bind(doctorList.get(position));
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvDoctorSpecialty, tvHospitalName;
        ImageView ivDoctorPhoto;
        ImageButton btnCall, btnChat, btnInfo;
        View btnBookAppointment;

        DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDoctorSpecialty = itemView.findViewById(R.id.tvDoctorSpecialty);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            ivDoctorPhoto = itemView.findViewById(R.id.ivDoctorPhoto);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnChat = itemView.findViewById(R.id.btnChat);
            btnInfo = itemView.findViewById(R.id.btnInfo);
            btnBookAppointment = itemView.findViewById(R.id.btnBookAppointment);
        }

        void bind(DoctorResponse doctor) {
            if (doctor == null) return;

            UserResponse user = doctor.getUser();
            SpecialtyResponse specialty = doctor.getSpecialty();
            HospitalResponse hospital = doctor.getHospital();

            // Set texts
            tvDoctorName.setText(user != null ? user.getFullName() : "Không rõ tên");
            tvDoctorSpecialty.setText(specialty != null ? specialty.getName() : "Chưa rõ chuyên khoa");
            tvHospitalName.setText(hospital != null ? hospital.getName() : "Chưa rõ bệnh viện");

            // Load avatar
            String avatarUrl = user != null ? user.getAvatar() : null;
            Glide.with(context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.sample_profile_image)
                    .error(R.drawable.sample_profile_image)
                    .into(ivDoctorPhoto);

            // Click vào cả item xem chi tiết
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onDoctorClick(doctor);
            });

            // Info button => cũng xem chi tiết
            btnInfo.setOnClickListener(v -> {
                if (listener != null) listener.onDoctorClick(doctor);
            });

            // Book appointment
            btnBookAppointment.setOnClickListener(v -> {
                if (listener != null) listener.onBookAppointmentClick(doctor);
            });

            // Call & Chat demo
            btnCall.setOnClickListener(v -> {
                Toast.makeText(context, "Gọi cho bác sĩ: " + (user != null ? user.getFullName() : ""), Toast.LENGTH_SHORT).show();
            });

            btnChat.setOnClickListener(v -> {
                Toast.makeText(context, "Nhắn tin với bác sĩ: " + (user != null ? user.getFullName() : ""), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
