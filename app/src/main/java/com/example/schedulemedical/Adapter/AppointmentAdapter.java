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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

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

        // TODO: Load doctor info from API if needed
        if (holder.tvDoctorName != null) {
            holder.tvDoctorName.setText("BS ID: " + appointment.getDoctorId()); // Placeholder
        }
        if (holder.tvSpecialty != null) {
            holder.tvSpecialty.setText("Chuyên khoa ..."); // Placeholder
        }

        // Format date and time
        if (appointment.getScheduledTime() != null) {
            String dateStr = appointment.getScheduledTime().length() >= 10 ? appointment.getScheduledTime().substring(0, 10) : "";
            String timeStr = appointment.getScheduledTime().length() >= 16 ? appointment.getScheduledTime().substring(11, 16) : "";
            if (holder.tvDate != null) {
                holder.tvDate.setText(dateStr);
            }
            if (holder.tvTime != null) {
                holder.tvTime.setText(timeStr);
            }
        }

        if (holder.tvStatus != null) {
            holder.tvStatus.setText(appointment.getStatus());
            // Set status color and background (simple version)
            setStatusStyle(holder, appointment.getStatus());
        }

        // Set note if available
        if (holder.tvConfirmStatus != null) {
            if (appointment.getNote() != null && !appointment.getNote().isEmpty()) {
                holder.tvConfirmStatus.setText(appointment.getNote());
            } else {
                holder.tvConfirmStatus.setText("Confirmed");
            }
        }

        // Set click listeners
        if (holder.btnCancel != null) {
            holder.btnCancel.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelAppointment(appointment);
                }
            });
        }

        if (holder.btnReschedule != null) {
            holder.btnReschedule.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRescheduleAppointment(appointment);
                }
            });
        }
    }

    private void setStatusStyle(AppointmentViewHolder holder, String status) {
        if (holder.tvStatus == null) return;
        try {
            if ("PENDING".equalsIgnoreCase(status)) {
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.orange));
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
        CircleImageView ivDoctor;
        TextView tvDoctorName, tvSpecialty, tvDate, tvTime, tvStatus, tvConfirmStatus;
        MaterialButton btnCancel, btnReschedule;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDoctor = itemView.findViewById(R.id.ivDoctor);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvConfirmStatus = itemView.findViewById(R.id.tvConfirmStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnReschedule = itemView.findViewById(R.id.btnReschedule);
        }
    }
}
