package com.example.schedulemedical.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.model.Service;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private Context context;
    private List<Service> services;
    private int selectedPosition = -1;
    private OnServiceSelectedListener listener;
    
    public interface OnServiceSelectedListener {
        void onServiceSelected(Service service, int position);
    }
    
    public ServiceAdapter(Context context, List<Service> services) {
        this.context = context;
        this.services = services;
    }
    
    public void setOnServiceSelectedListener(OnServiceSelectedListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        
        holder.tvServiceName.setText(service.getName());
        holder.tvServicePrice.setText(service.getFormattedPrice());
        
        // Set duration if available
        if (service.getDuration() != null && !service.getDuration().isEmpty()) {
            holder.tvServiceDuration.setText(service.getDuration());
            holder.tvServiceDuration.setVisibility(View.VISIBLE);
        } else {
            holder.tvServiceDuration.setVisibility(View.GONE);
        }
        
        // Set description if available
        if (service.getDescription() != null && !service.getDescription().isEmpty()) {
            String description = String.join(", ", service.getDescription());
            holder.tvServiceDescription.setText(description);
            holder.tvServiceDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvServiceDescription.setVisibility(View.GONE);
        }
        
        // Handle radio button selection
        holder.rbServiceSelect.setChecked(position == selectedPosition);
        
        holder.rbServiceSelect.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            
            // Notify previous selected item
            if (previousPosition != -1) {
                notifyItemChanged(previousPosition);
            }
            
            // Notify current selected item
            notifyItemChanged(selectedPosition);
            
            if (listener != null) {
                listener.onServiceSelected(service, selectedPosition);
            }
        });
        
        // Handle card click
        holder.itemView.setOnClickListener(v -> {
            holder.rbServiceSelect.performClick();
        });
    }
    
    @Override
    public int getItemCount() {
        return services != null ? services.size() : 0;
    }
    
    public Service getSelectedService() {
        if (selectedPosition >= 0 && selectedPosition < services.size()) {
            return services.get(selectedPosition);
        }
        return null;
    }
    
    public void updateServices(List<Service> newServices) {
        this.services = newServices;
        this.selectedPosition = -1; // Reset selection
        notifyDataSetChanged();
    }
    
    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvServiceDuration, tvServicePrice, tvServiceDescription;
        RadioButton rbServiceSelect;
        
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceDuration = itemView.findViewById(R.id.tvServiceDuration);
            tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
            tvServiceDescription = itemView.findViewById(R.id.tvServiceDescription);
            rbServiceSelect = itemView.findViewById(R.id.rbServiceSelect);
        }
    }
} 