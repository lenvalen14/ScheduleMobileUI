package com.example.schedulemedical.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.Specialty;
import java.util.List;

public class SpecialtyCardAdapter extends RecyclerView.Adapter<SpecialtyCardAdapter.SpecialtyViewHolder> {
    private final Context context;
    private final List<Specialty> specialtyList;
    private OnSpecialtyClickListener onSpecialtyClickListener;

    public interface OnSpecialtyClickListener {
        void onSpecialtyClick(Specialty specialty);
    }

    public SpecialtyCardAdapter(Context context, List<Specialty> specialtyList) {
        this.context = context;
        this.specialtyList = specialtyList;
    }

    public void setOnSpecialtyClickListener(OnSpecialtyClickListener listener) {
        this.onSpecialtyClickListener = listener;
    }

    @NonNull
    @Override
    public SpecialtyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_specialty, parent, false);
        return new SpecialtyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialtyViewHolder holder, int position) {
        Specialty specialty = specialtyList.get(position);
        holder.bind(specialty);
    }

    @Override
    public int getItemCount() {
        return specialtyList != null ? specialtyList.size() : 0;
    }

    class SpecialtyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivSpecialtyIcon;
        private final TextView tvSpecialtyName;

        public SpecialtyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSpecialtyIcon = itemView.findViewById(R.id.ivSpecialtyIcon);
            tvSpecialtyName = itemView.findViewById(R.id.tvSpecialtyName);
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && onSpecialtyClickListener != null) {
                    onSpecialtyClickListener.onSpecialtyClick(specialtyList.get(pos));
                }
            });
        }

        public void bind(Specialty specialty) {
            tvSpecialtyName.setText(specialty.getName());
            // Nếu muốn set icon động, có thể map specialty.getName() sang icon tương ứng ở đây
            // ivSpecialtyIcon.setImageResource(...);
        }
    }
} 