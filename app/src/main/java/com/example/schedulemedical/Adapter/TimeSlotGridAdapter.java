package com.example.schedulemedical.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.model.TimeSlot;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TimeSlotGridAdapter extends RecyclerView.Adapter<TimeSlotGridAdapter.TimeSlotViewHolder> {
    private Context context;
    private List<TimeSlot> timeSlots;
    private OnTimeSlotClickListener listener;
    private int selectedPosition = -1;
    
    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(TimeSlot timeSlot, int position);
    }
    
    public interface OnTimeSlotSelectedListener {
        void onTimeSlotSelected(TimeSlot timeSlot, int position);
    }
    
    private OnTimeSlotSelectedListener selectedListener;
    
    public TimeSlotGridAdapter(Context context, List<TimeSlot> timeSlots) {
        this.context = context;
        this.timeSlots = timeSlots;
    }
    
    public void setOnTimeSlotClickListener(OnTimeSlotClickListener listener) {
        this.listener = listener;
    }
    
    public void setOnTimeSlotSelectedListener(OnTimeSlotSelectedListener listener) {
        this.selectedListener = listener;
    }
    
    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlots.get(position);
        
        holder.btnTimeSlot.setText(timeSlot.getDisplayTime());
        
        // Update button state based on availability and selection
        updateButtonState(holder.btnTimeSlot, timeSlot, position == selectedPosition);
        
        holder.btnTimeSlot.setOnClickListener(v -> {
            if (timeSlot.isAvailable()) {
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                
                // Update previous selected item
                if (previousPosition != -1 && previousPosition < timeSlots.size()) {
                    timeSlots.get(previousPosition).setSelected(false);
                    notifyItemChanged(previousPosition);
                }
                
                // Update current selected item
                timeSlot.setSelected(true);
                notifyItemChanged(selectedPosition);
                
                if (listener != null) {
                    listener.onTimeSlotClick(timeSlot, selectedPosition);
                }
                if (selectedListener != null) {
                    selectedListener.onTimeSlotSelected(timeSlot, selectedPosition);
                }
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return timeSlots != null ? timeSlots.size() : 0;
    }
    
    private void updateButtonState(MaterialButton button, TimeSlot timeSlot, boolean isSelected) {
        if (!timeSlot.isAvailable()) {
            // Disabled state - past time or fully booked
            button.setEnabled(false);
            button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.gray_e0));
            button.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            button.setStrokeColor(ContextCompat.getColorStateList(context, R.color.gray_e0));
        } else if (isSelected) {
            // Selected state
            button.setEnabled(true);
            button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.blue_007b));
            button.setTextColor(ContextCompat.getColor(context, R.color.white));
            button.setStrokeColor(ContextCompat.getColorStateList(context, R.color.blue_007b));
        } else {
            // Available state
            button.setEnabled(true);
            button.setBackgroundTintList(ContextCompat.getColorStateList(context, android.R.color.transparent));
            button.setTextColor(ContextCompat.getColor(context, R.color.blue_007b));
            button.setStrokeColor(ContextCompat.getColorStateList(context, R.color.blue_007b));
        }
    }
    
    public TimeSlot getSelectedTimeSlot() {
        if (selectedPosition >= 0 && selectedPosition < timeSlots.size()) {
            return timeSlots.get(selectedPosition);
        }
        return null;
    }
    
    public void updateTimeSlots(List<TimeSlot> newTimeSlots) {
        this.timeSlots = newTimeSlots;
        this.selectedPosition = -1; // Reset selection
        notifyDataSetChanged();
    }
    
    public void clearSelection() {
        if (selectedPosition != -1) {
            int previousPosition = selectedPosition;
            selectedPosition = -1;
            notifyItemChanged(previousPosition);
        }
    }
    
    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        MaterialButton btnTimeSlot;
        
        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            btnTimeSlot = itemView.findViewById(R.id.btnTimeSlot);
        }
    }
} 