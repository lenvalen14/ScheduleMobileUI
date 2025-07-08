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
import com.google.android.material.button.MaterialButton;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    public interface OnAppointmentActionListener {
        void onCancelAppointment(AppointmentResponse appointment);
        void onRescheduleAppointment(AppointmentResponse appointment);
        void onConfirmAppointment(AppointmentResponse appointment);
    }

    private List<AppointmentResponse> appointmentList;
    private OnAppointmentActionListener listener;
    private boolean isDoctor = false;

    public AppointmentAdapter(List<AppointmentResponse> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public AppointmentAdapter(List<AppointmentResponse> appointmentList, boolean isDoctor) {
        this.appointmentList = appointmentList;
        this.isDoctor = isDoctor;
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

        // Tên người liên quan
        String name = "";
        if (isDoctor && appointment.getUser() != null) {
            name = appointment.getUser().getFullName();
        } else if (appointment.getDoctor() != null && appointment.getDoctor().getUser() != null) {
            name = "BS. " + appointment.getDoctor().getUser().getFullName();
        }
        holder.tvDoctorName.setText(name);

        // Chuyên khoa
        String specialty = appointment.getDoctor() != null &&
                appointment.getDoctor().getSpecialty() != null
                ? appointment.getDoctor().getSpecialty().getName()
                : "";
        holder.tvSpecialty.setText("Chuyên khoa " + specialty);

        // Ngày giờ hẹn
        if (appointment.getScheduledTime() != null) {
            holder.tvDateTime.setText(formatDateTime(appointment.getScheduledTime()));
        }

        // Bệnh viện
        String hospitalName = appointment.getService() != null ? appointment.getService().getName() : "";
        holder.tvHospital.setText(hospitalName);

        // Trạng thái
        String status = appointment.getStatus() != null ? appointment.getStatus().toUpperCase() : "";
        holder.tvStatus.setText(getStatusText(status));
        setStatusStyle(holder, status);

        // Ẩn tất cả các nút
        holder.btnCancel.setVisibility(View.GONE);
        holder.btnConfirm.setVisibility(View.GONE);
        holder.btnReschedule.setVisibility(View.GONE);

        // Logic hiển thị nút
        if (isDoctor) {
            if ("PENDING".equals(status)) {
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnConfirm.setVisibility(View.VISIBLE);

                holder.btnCancel.setOnClickListener(v -> {
                    if (listener != null) listener.onCancelAppointment(appointment);
                });

                holder.btnConfirm.setOnClickListener(v -> {
                    if (listener != null) listener.onConfirmAppointment(appointment);
                });
            }
        } else {
            if ("PENDING".equals(status)) {
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnCancel.setOnClickListener(v -> {
                    if (listener != null) listener.onCancelAppointment(appointment);
                });
            } else if ("COMPLETED".equals(status)) {
                holder.btnReschedule.setVisibility(View.VISIBLE);
                holder.btnReschedule.setOnClickListener(v -> {
                    if (listener != null) listener.onRescheduleAppointment(appointment);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList != null ? appointmentList.size() : 0;
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvSpecialty, tvDateTime, tvHospital, tvStatus;
        MaterialButton btnCancel, btnReschedule, btnConfirm;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tv_doctor_name);
            tvSpecialty = itemView.findViewById(R.id.tv_doctor_specialty);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvHospital = itemView.findViewById(R.id.tv_hospital_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnReschedule = itemView.findViewById(R.id.btnReschedule);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
        }
    }

    private String formatDateTime(String isoDateTime) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ZonedDateTime zdt = ZonedDateTime.parse(isoDateTime);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy - HH:mm", Locale.forLanguageTag("vi"));
                String formatted = zdt.format(formatter);
                return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
            }
            return isoDateTime;
        } catch (Exception e) {
            return isoDateTime;
        }
    }

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
}
