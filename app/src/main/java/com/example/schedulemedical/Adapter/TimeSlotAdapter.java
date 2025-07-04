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

import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {
    private Context context;
    private List<String> timeSlots;
    private int selectedPosition = -1;
    private OnTimeSlotClickListener onTimeSlotClickListener;
    
    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(String timeSlot);
    }
    
    public TimeSlotAdapter(Context context, List<String> timeSlots) {
        this.context = context;
        this.timeSlots = timeSlots != null ? timeSlots : new ArrayList<>();
    }
    
    public void setOnTimeSlotClickListener(OnTimeSlotClickListener listener) {
        this.onTimeSlotClickListener = listener;
    }
    
    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        String timeSlot = timeSlots.get(position);
        holder.bind(timeSlot, position == selectedPosition);
    }
    
    @Override
    public int getItemCount() {
        return timeSlots.size();
    }
    
    public void updateTimeSlots(List<String> newTimeSlots) {
        this.timeSlots.clear();
        if (newTimeSlots != null) {
            this.timeSlots.addAll(newTimeSlots);
        }
        this.selectedPosition = -1; // Clear selection when updating
        notifyDataSetChanged();
    }
    
    public void clearSelection() {
        selectedPosition = -1;
        notifyDataSetChanged();
    }
    
    public String getSelectedTimeSlot() {
        if (selectedPosition >= 0 && selectedPosition < timeSlots.size()) {
            return timeSlots.get(selectedPosition);
        }
        return null;
    }
    
    class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTimeSlot;
        
        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeSlot = itemView.findViewById(R.id.btnTimeSlot);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Update selection
                    int previousSelected = selectedPosition;
                    selectedPosition = position;
                    
                    // Notify changes
                    if (previousSelected != -1) {
                        notifyItemChanged(previousSelected);
                    }
                    notifyItemChanged(selectedPosition);
                    
                    // Callback
                    if (onTimeSlotClickListener != null) {
                        onTimeSlotClickListener.onTimeSlotClick(timeSlots.get(position));
                    }
                }
            });
        }
        
        public void bind(String timeSlot, boolean isSelected) {
            tvTimeSlot.setText(timeSlot);
            
            // Update appearance based on selection
            if (isSelected) {
                tvTimeSlot.setBackgroundResource(R.drawable.time_slot_selected_bg);
                tvTimeSlot.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            } else {
                tvTimeSlot.setBackgroundResource(R.drawable.time_slot_normal_bg);
                tvTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            }
        }
    }
} 