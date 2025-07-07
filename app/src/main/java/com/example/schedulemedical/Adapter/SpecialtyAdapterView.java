package com.example.schedulemedical.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;

import java.util.ArrayList;
import java.util.List;

public class SpecialtyAdapterView extends RecyclerView.Adapter<SpecialtyAdapterView.SpecialtyViewHolder> {

    private List<SpecialtyResponse> specialties = new ArrayList<>();

    public void setSpecialties(List<SpecialtyResponse> specialties) {
        this.specialties = specialties;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SpecialtyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_specialty_view, parent, false);
        return new SpecialtyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialtyViewHolder holder, int position) {
        SpecialtyResponse item = specialties.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDescription.setText(item.getDescription());

        // Button click
        holder.btnBookNow.setOnClickListener(v -> {
            // TODO: open booking screen
            Toast.makeText(v.getContext(), "Đặt lịch cho " + item.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return specialties.size();
    }

    static class SpecialtyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        Button btnBookNow;

        public SpecialtyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSpecialtyName);
            tvDescription = itemView.findViewById(R.id.tvSpecialtyDescription);
            btnBookNow = itemView.findViewById(R.id.btnBookNow);
        }
    }
}
