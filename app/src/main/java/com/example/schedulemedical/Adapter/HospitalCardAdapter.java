package com.example.schedulemedical.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import com.bumptech.glide.Glide;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.google.android.material.button.MaterialButton;
import androidx.recyclerview.widget.RecyclerView;

public class HospitalCardAdapter extends ListAdapter<HospitalResponse, HospitalCardAdapter.HospitalViewHolder> {
    private final Context context;
    private OnHospitalClickListener onHospitalClickListener;

    public interface OnHospitalClickListener {
        void onHospitalClick(HospitalResponse hospital);
        void onBookNowClick(HospitalResponse hospital);
        boolean isHospitalBookable(HospitalResponse hospital);
    }

    public HospitalCardAdapter(Context context) {
        super(new DiffUtil.ItemCallback<HospitalResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull HospitalResponse oldItem, @NonNull HospitalResponse newItem) {
                return oldItem.getHospitalId() == newItem.getHospitalId();
            }
            @Override
            public boolean areContentsTheSame(@NonNull HospitalResponse oldItem, @NonNull HospitalResponse newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.context = context;
    }

    public void setOnHospitalClickListener(OnHospitalClickListener listener) {
        this.onHospitalClickListener = listener;
    }

    public void updateData(java.util.List<HospitalResponse> newList) {
        submitList(newList != null ? new java.util.ArrayList<>(newList) : new java.util.ArrayList<>());
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_hospital, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        HospitalResponse hospital = getItem(position);
        holder.bind(hospital);
    }

    class HospitalViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivHospitalLogo;
        private final TextView tvHospitalName;
        private final TextView tvHospitalAddress;
        private final TextView tvRating;
        private final MaterialButton btnBookNow;

        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHospitalLogo = itemView.findViewById(R.id.ivHospitalLogo);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            tvHospitalAddress = itemView.findViewById(R.id.tvHospitalAddress);
            View layoutRating = itemView.findViewById(R.id.layoutRating);
            TextView ratingText = null;
            if (layoutRating instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) layoutRating).getChildCount(); i++) {
                    View child = ((ViewGroup) layoutRating).getChildAt(i);
                    if (child instanceof TextView) {
                        ratingText = (TextView) child;
                        break;
                    }
                }
            }
            tvRating = ratingText;
            btnBookNow = itemView.findViewById(R.id.btnBookNow);
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && onHospitalClickListener != null) {
                    onHospitalClickListener.onHospitalClick(getItem(pos));
                }
            });
            btnBookNow.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (onHospitalClickListener != null) {
                        onHospitalClickListener.onBookNowClick(getItem(pos));
                    } else {
                        Intent intent = com.example.schedulemedical.ui.booking.BookingWizardActivity.createIntentWithHospital(context, getItem(pos));
                        context.startActivity(intent);
                    }
                }
            });
        }
        public void bind(HospitalResponse hospital) {
            tvHospitalName.setText(hospital.getName());
            tvHospitalAddress.setText(hospital.getAddress());
            if (tvRating != null && hospital.getRating() != null) {
                tvRating.setText(String.valueOf(hospital.getRating()));
            }
            if (hospital.getLogo() != null && !hospital.getLogo().isEmpty()) {
                Glide.with(context).load(hospital.getLogo()).into(ivHospitalLogo);
            } else {
                ivHospitalLogo.setImageResource(R.drawable.logo_benh_vien_mat);
            }
            boolean isBookable = true;
            if (onHospitalClickListener != null) {
                isBookable = onHospitalClickListener.isHospitalBookable(hospital);
            }
            btnBookNow.setEnabled(isBookable);
            if (!isBookable) {
                btnBookNow.setText("Chưa có dịch vụ");
                btnBookNow.setAlpha(0.5f);
            } else {
                btnBookNow.setText("Đặt lịch ngay");
                btnBookNow.setAlpha(1.0f);
            }
        }
    }
} 