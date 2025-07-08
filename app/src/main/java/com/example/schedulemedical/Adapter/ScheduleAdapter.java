package com.example.schedulemedical.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.ScheduleResponse;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    public interface OnScheduleActionListener {
        void onEdit(ScheduleResponse schedule);
        void onDelete(ScheduleResponse schedule);
    }

    private List<ScheduleResponse> scheduleList;
    private OnScheduleActionListener listener;

    public ScheduleAdapter(List<ScheduleResponse> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public void setOnScheduleActionListener(OnScheduleActionListener listener) {
        this.listener = listener;
    }

    public void updateData(List<ScheduleResponse> newList) {
        this.scheduleList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule_entry, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleResponse schedule = scheduleList.get(position);

        holder.tvDay.setText(convertDayOfWeek(schedule.getDayOfWeek()));
        holder.tvTime.setText(schedule.getStartTime() + " - " + schedule.getEndTime());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(schedule);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(schedule);
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList != null ? scheduleList.size() : 0;
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvTime;
        ImageButton btnEdit, btnDelete;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private String convertDayOfWeek(int day) {
        switch (day) {
            case 1: return "Thứ 2";
            case 2: return "Thứ 3";
            case 3: return "Thứ 4";
            case 4: return "Thứ 5";
            case 5: return "Thứ 6";
            case 6: return "Thứ 7";
            case 7: return "Chủ nhật";
            default: return "Không rõ";
        }
    }
}
