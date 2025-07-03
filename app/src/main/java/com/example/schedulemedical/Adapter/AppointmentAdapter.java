package com.example.schedulemedical.Adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.request.appoiment.AppointmentDTO;
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

    private List<AppointmentDTO> appointmentList;
    private OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onCancelAppointment(AppointmentDTO appointment);
        void onRescheduleAppointment(AppointmentDTO appointment);
    }

    public AppointmentAdapter(List<AppointmentDTO> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public void setOnAppointmentActionListener(OnAppointmentActionListener listener) {
        this.listener = listener;
    }

    public void updateData(List<AppointmentDTO> newList) {
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
        AppointmentDTO appointment = appointmentList.get(position);

        // TODO: Load doctor info from API
        if (holder.tvDoctorName != null) {
            holder.tvDoctorName.setText("Dr. " + appointment.getDoctorId()); // Placeholder
        }
        if (holder.tvSpecialty != null) {
            holder.tvSpecialty.setText("General Medicine"); // Placeholder
        }

        // Format date and time with backward compatibility
        if (appointment.getScheduledTime() != null) {
            String dateStr = formatDateTime(appointment.getScheduledTime(), "dd/MM/yyyy");
            String timeStr = formatDateTime(appointment.getScheduledTime(), "HH:mm");
            
            if (holder.tvDate != null) {
                holder.tvDate.setText(dateStr);
            }
            if (holder.tvTime != null) {
                holder.tvTime.setText(timeStr);
            }
        }

        if (holder.tvStatus != null) {
            holder.tvStatus.setText(appointment.getStatus().getValue());
            // Set status color and background
            setStatusStyle(holder, appointment.getStatus());
        }

        // Set note if available (using the "Confirmed" text in llInfo)
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

    private String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return dateTime.format(formatter);
            } catch (Exception e) {
                // Fallback to SimpleDateFormat
                return formatDateTimeLegacy(dateTime, pattern);
            }
        } else {
            return formatDateTimeLegacy(dateTime, pattern);
        }
    }

    private String formatDateTimeLegacy(LocalDateTime dateTime, String pattern) {
        try {
            // Convert LocalDateTime to Date for backward compatibility
            Date date = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            return "N/A";
        }
    }

    private void setStatusStyle(AppointmentViewHolder holder, AppointmentDTO.AppointmentStatus status) {
        if (holder.tvStatus == null) return;
        
        try {
            switch (status) {
                case PENDING:
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.orange));
                    holder.tvStatus.setBackgroundResource(R.drawable.bg_status_upcoming);
                    break;
                case CONFIRMED:
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.blue));
                    holder.tvStatus.setBackgroundResource(R.drawable.bg_status_upcoming);
                    break;
                case COMPLETED:
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.green));
                    holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                    break;
                case CANCELLED:
                case NO_SHOW:
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.red));
                    holder.tvStatus.setBackgroundResource(R.drawable.bg_status_canceled);
                    break;
            }
        } catch (Exception e) {
            // Handle missing color resources gracefully
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
