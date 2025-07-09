package com.example.schedulemedical.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.Specialty;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schedulemedical.utils.SpecialtyIconUtil;
import android.util.Log;

public class SpecialtyCardAdapter extends ListAdapter<Specialty, SpecialtyCardAdapter.SpecialtyViewHolder> {
    private final Context context;
    private OnSpecialtyClickListener onSpecialtyClickListener;

    public interface OnSpecialtyClickListener {
        void onSpecialtyClick(Specialty specialty);
    }

    public SpecialtyCardAdapter(Context context) {
        super(new DiffUtil.ItemCallback<Specialty>() {
            @Override
            public boolean areItemsTheSame(@NonNull Specialty oldItem, @NonNull Specialty newItem) {
                return oldItem.getName().equals(newItem.getName());
            }
            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull Specialty oldItem, @NonNull Specialty newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.context = context;
    }

    public void setOnSpecialtyClickListener(OnSpecialtyClickListener listener) {
        this.onSpecialtyClickListener = listener;
    }

    public void updateData(java.util.List<Specialty> newList) {
        submitList(newList != null ? new java.util.ArrayList<>(newList) : new java.util.ArrayList<>());
    }

    @NonNull
    @Override
    public SpecialtyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_specialty, parent, false);
        return new SpecialtyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialtyViewHolder holder, int position) {
        Specialty specialty = getItem(position);
        holder.bind(specialty);
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
                    onSpecialtyClickListener.onSpecialtyClick(getItem(pos));
                }
            });
        }
        public void bind(Specialty specialty) {
            tvSpecialtyName.setText(specialty.getName());
            // Map icon và background động
            String name = specialty.getName() != null ? specialty.getName().trim().toLowerCase() : "";
            Log.d("SpecialtyCardAdapter", "Specialty name raw: '" + specialty.getName() + "', normalized: '" + name + "'");
            int iconResId = SpecialtyIconUtil.getIconResId(name);
            int bgResId = SpecialtyIconUtil.getBackgroundResId(name);
            ivSpecialtyIcon.setImageResource(iconResId);
            View flIconBg = (View) ivSpecialtyIcon.getParent();
            if (flIconBg != null) {
                flIconBg.setBackgroundResource(bgResId);
            }
        }
    }
} 