package com.example.schedulemedical.Adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.AppointmentResponse;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.google.android.material.button.MaterialButton;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<AppointmentResponse> appointmentList;
    private OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onCancelAppointment(AppointmentResponse appointment);
        void onRescheduleAppointment(AppointmentResponse appointment);
    }

    public AppointmentAdapter(List<AppointmentResponse> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public void setOnAppointmentActionListener(OnAppointmentActionListener listener) {
        this.listener = listener;
    }

    public void updateData(List<AppointmentResponse> newList) {
        this.appointmentList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentResponse appointment = appointmentList.get(position);
        // Lấy tên bác sĩ từ doctor.user.fullName
        String doctorName = "";
        if (appointment.getDoctor() != null && appointment.getDoctor().getUser() != null) {
            doctorName = appointment.getDoctor().getUser().getFullName();
        }
        if (holder.tvDoctorName != null) {
            holder.tvDoctorName.setText("BS. " + (doctorName != null ? doctorName : ""));
        }
        // Lấy chuyên khoa từ doctor.specialty.name
        String specialty = "";
        if (appointment.getDoctor() != null && appointment.getDoctor().getSpecialty() != null) {
            specialty = appointment.getDoctor().getSpecialty().getName();
        }
        if (holder.tvSpecialty != null) {
            holder.tvSpecialty.setText("Chuyên khoa " + (specialty != null ? specialty : ""));
        }
        // Set ngày-giờ (gộp)
        if (holder.tvDateTime != null && appointment.getScheduledTime() != null) {
            holder.tvDateTime.setText(formatDateTime(appointment.getScheduledTime()));
        }
        // Lấy tên bệnh viện từ service.name (mock thay cho hospital)
        String hospitalName = "";
        if (appointment.getService() != null && appointment.getService().getName() != null) {
            hospitalName = appointment.getService().getName();
        }
        if (holder.tvHospital != null) {
            holder.tvHospital.setText(hospitalName);
        }
        if (holder.tvStatus != null) {
            holder.tvStatus.setText(getStatusText(appointment.getStatus()));
            setStatusStyle(holder, appointment.getStatus());
        }
        // Điều khiển nút theo status
        if (holder.btnCancel != null) holder.btnCancel.setVisibility(View.GONE);
        if (holder.btnReschedule != null) holder.btnReschedule.setVisibility(View.GONE);
        String status = appointment.getStatus() != null ? appointment.getStatus().toUpperCase() : "";
        if (holder.btnCancel != null && "PENDING".equals(status)) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> {
                if (listener != null) listener.onCancelAppointment(appointment);
            });
        }
        if (holder.btnReschedule != null && ("COMPLETED".equals(status))) {
            holder.btnReschedule.setVisibility(View.VISIBLE);
            holder.btnReschedule.setOnClickListener(v -> {
                if (listener != null) listener.onRescheduleAppointment(appointment);
            });
        }
    }

    private void setStatusStyle(AppointmentViewHolder holder, String status) {
        if (holder.tvStatus == null) return;
        try {
            if ("PENDING".equalsIgnoreCase(status)) {
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.orange));
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
            } else if ("SCHEDULED".equalsIgnoreCase(status)) {
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.blue_03));
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_upcoming);
            } else if ("COMPLETED".equalsIgnoreCase(status)) {
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.green));
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
            } else if ("CANCELLED".equalsIgnoreCase(status)) {
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.red));
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_canceled);
            } else {
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.black));
            }
        } catch (Exception e) {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList != null ? appointmentList.size() : 0;
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvSpecialty, tvDateTime, tvHospital, tvStatus;
        MaterialButton btnCancel, btnReschedule;
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tv_doctor_name);
            tvSpecialty = itemView.findViewById(R.id.tv_doctor_specialty);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvHospital = itemView.findViewById(R.id.tv_hospital_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnReschedule = itemView.findViewById(R.id.btnReschedule);
        }
    }

    // Thêm vào class AppointmentViewHolder:
    // TextView tvDateTime, tvHospital;
    // Sửa constructor:
    // tvDateTime = itemView.findViewById(R.id.tvDateTime);
    // tvHospital = itemView.findViewById(R.id.tvHospital);

    // Thêm hàm formatDateTime
    private String formatDateTime(String isoDateTime) {
        try {
            // Giả sử isoDateTime dạng "2025-07-09T10:00:00.000Z"
            java.time.ZonedDateTime zdt = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                zdt = ZonedDateTime.parse(isoDateTime);
            }
            java.time.format.DateTimeFormatter formatter = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy - HH:mm", Locale.forLanguageTag("vi"));
            }
            String formatted = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatted = zdt.format(formatter);
            }
            // Viết hoa chữ cái đầu thứ
            return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
        } catch (Exception e) {
            return isoDateTime;
        }
    }
    // Thêm hàm getStatusText
    private String getStatusText(String status) {
        if (status == null) return "";
        switch (status.toUpperCase()) {
            case "PENDING": return "Chờ xác nhận";
            case "SCHEDULED": return "Sắp tới";
            case "COMPLETED": return "Hoàn thành";
            case "CANCELLED": return "Đã hủy";
            default: return status;
        }
    }
}
