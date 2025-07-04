package com.example.schedulemedical.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.model.CalendarDay;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private Context context;
    private List<CalendarDay> calendarDays;
    private OnDateClickListener listener;
    private OnDateSelectedListener selectedListener;
    private int selectedPosition = -1;
    
    public interface OnDateClickListener {
        void onDateClick(CalendarDay day, int position);
    }
    
    public interface OnDateSelectedListener {
        void onDateSelected(CalendarDay day, int position);
    }
    
    public CalendarAdapter(Context context, List<CalendarDay> calendarDays) {
        this.context = context;
        this.calendarDays = calendarDays;
    }
    
    public void setOnDateClickListener(OnDateClickListener listener) {
        this.listener = listener;
    }
    
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.selectedListener = listener;
    }
    
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDay day = calendarDays.get(position);
        
        if (day.isEmpty()) {
            holder.tvDay.setText("");
            holder.itemView.setClickable(false);
            holder.itemView.setBackground(null);
        } else {
            holder.tvDay.setText(String.valueOf(day.getDay()));
            holder.itemView.setClickable(day.isEnabled());
            
            // Set selected state based on position
            day.setSelected(position == selectedPosition);
            
            // Update appearance based on day state
            updateDayAppearance(holder, day);
            
            holder.itemView.setOnClickListener(v -> {
                if (day.isEnabled()) {
                    // Update selection
                    int previousPosition = selectedPosition;
                    selectedPosition = holder.getAdapterPosition();
                    
                    // Notify changes
                    if (previousPosition != -1) {
                        notifyItemChanged(previousPosition);
                    }
                    notifyItemChanged(selectedPosition);
                    
                    // Call listeners
                    if (listener != null) {
                        listener.onDateClick(day, position);
                    }
                    if (selectedListener != null) {
                        selectedListener.onDateSelected(day, selectedPosition);
                    }
                }
            });
        }
    }
    
    @Override
    public int getItemCount() {
        return calendarDays != null ? calendarDays.size() : 0;
    }
    
    private void updateDayAppearance(CalendarViewHolder holder, CalendarDay day) {
        if (!day.isEnabled()) {
            // Disabled day (past date)
            holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.text_disabled));
            holder.itemView.setBackground(null);
        } else if (day.isSelected()) {
            // Selected day
            holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.calendar_day_selected_bg));
        } else if (day.isToday()) {
            // Today
            holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.blue_007b));
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.calendar_day_today_bg));
        } else {
            // Normal available day
            holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.calendar_day_normal_bg));
        }
    }
    
    public void updateCalendarDays(List<CalendarDay> newCalendarDays) {
        this.calendarDays = newCalendarDays;
        this.selectedPosition = -1; // Reset selection
        notifyDataSetChanged();
    }
    
    public void clearSelection() {
        int oldPosition = selectedPosition;
        selectedPosition = -1;
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
    }
    
    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
        }
    }
} 