package com.example.schedulemedical.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class HospitalCardAdapter extends RecyclerView.Adapter<HospitalCardAdapter.HospitalViewHolder> {
    private final Context context;
    private final List<HospitalResponse> hospitalList;
    private OnHospitalClickListener onHospitalClickListener;

    public interface OnHospitalClickListener {
        void onHospitalClick(HospitalResponse hospital);
        void onBookNowClick(HospitalResponse hospital);
    }

    public HospitalCardAdapter(Context context, List<HospitalResponse> hospitalList) {
        this.context = context;
        this.hospitalList = hospitalList;
    }

    public void setOnHospitalClickListener(OnHospitalClickListener listener) {
        this.onHospitalClickListener = listener;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_hospital, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        HospitalResponse hospital = hospitalList.get(position);
        holder.bind(hospital);
    }

    @Override
    public int getItemCount() {
        return hospitalList != null ? hospitalList.size() : 0;
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
            // Tìm TextView rating trong layoutRating
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
                    onHospitalClickListener.onHospitalClick(hospitalList.get(pos));
                }
            });

            btnBookNow.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && onHospitalClickListener != null) {
                    onHospitalClickListener.onBookNowClick(hospitalList.get(pos));
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
        }
    }
} 