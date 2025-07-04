package com.example.schedulemedical.ui.hospital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.schedulemedical.R;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private List<String> imageUrls;
    private Context context;
    private OnImageClickListener onImageClickListener;

    public interface OnImageClickListener {
        void onImageClick(int position, String imageUrl);
    }

    public GalleryAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.onImageClickListener = listener;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery_image, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        
        // Debug logging
        android.util.Log.d("GalleryAdapter", "Loading image at position " + position + ": " + imageUrl);
        
        // Load image with Glide - simplified for debugging
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.logo_benh_vien_mat)
                .error(R.drawable.logo_benh_vien_mat)
                .centerCrop()
                .into(holder.ivGalleryImage);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            android.util.Log.d("GalleryAdapter", "Image clicked: " + imageUrl);
            if (onImageClickListener != null) {
                onImageClickListener.onImageClick(position, imageUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    public void updateImages(List<String> newImageUrls) {
        this.imageUrls = newImageUrls;
        notifyDataSetChanged();
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGalleryImage;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGalleryImage = itemView.findViewById(R.id.ivGalleryImage);
        }
    }
} 