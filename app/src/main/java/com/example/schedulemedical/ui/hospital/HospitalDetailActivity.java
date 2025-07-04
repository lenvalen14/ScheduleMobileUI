package com.example.schedulemedical.ui.hospital;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.HospitalApiService;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalDetailActivity extends AppCompatActivity {
    private TextView tvName, tvType, tvAddress, tvPhone, tvEmail, tvDescription, tvEstablishYear, tvWorkScheduling, tvWebsite, tvCertificates, tvGallery, tvLatitude, tvLongitude, tvRating, tvReviews, tvTotalBeds, tvTotalNurses, tvVerified, tvDoctorCount;
    private ImageView ivLogo, btnBack, btnNotification;
    private RecyclerView recyclerGallery;
    private GalleryAdapter galleryAdapter;
    private int hospitalId;
    private HospitalApiService hospitalApiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_detail);

        hospitalId = getIntent().getIntExtra("hospitalId", -1);
        if (hospitalId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin bệnh viện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        hospitalApiService = ApiClient.getHospitalApiService();
        initViews();
        setupClickListeners();
        setupBottomNavigation();
        loadHospitalDetail();
    }

    private void initViews() {
        // Navigation elements
        btnBack = findViewById(R.id.btnBack);
        btnNotification = findViewById(R.id.btnNotification);
        
        // Content elements
        ivLogo = findViewById(R.id.ivLogo);
        tvName = findViewById(R.id.tvName);
        tvType = findViewById(R.id.tvType);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvDescription = findViewById(R.id.tvDescription);
        tvEstablishYear = findViewById(R.id.tvEstablishYear);
        tvWorkScheduling = findViewById(R.id.tvWorkScheduling);
        tvWebsite = findViewById(R.id.tvWebsite);
        tvCertificates = findViewById(R.id.tvCertificates);
        tvGallery = findViewById(R.id.tvGallery);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvRating = findViewById(R.id.tvRating);
        tvReviews = findViewById(R.id.tvReviews);
        tvTotalBeds = findViewById(R.id.tvTotalBeds);
        tvTotalNurses = findViewById(R.id.tvTotalNurses);
        tvVerified = findViewById(R.id.tvVerified);
        tvDoctorCount = findViewById(R.id.tvDoctorCount);
        
        // Gallery RecyclerView
        recyclerGallery = findViewById(R.id.recyclerGallery);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.goBack(HospitalDetailActivity.this);
            }
        });

        // Notification button
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HospitalDetailActivity.this, "Notifications feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    NavigationHelper.navigateToHome(this);
                    return true;
                } else if (itemId == R.id.nav_explore) {
                    NavigationHelper.navigateToHospital(this);
                    return true;
                } else if (itemId == R.id.nav_calendar) {
                    NavigationHelper.navigateToMyScheduled(this);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    NavigationHelper.navigateToUserProfile(this);
                    return true;
                }
                return false;
            });
        }
    }

    private void loadHospitalDetail() {
        hospitalApiService.getHospitalById(hospitalId).enqueue(new Callback<com.example.schedulemedical.model.dto.response.ApiResponse<HospitalResponse>>() {
            @Override
            public void onResponse(Call<com.example.schedulemedical.model.dto.response.ApiResponse<HospitalResponse>> call, Response<com.example.schedulemedical.model.dto.response.ApiResponse<HospitalResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    HospitalResponse hospital = response.body().getData();
                    renderHospital(hospital);
                } else {
                    Toast.makeText(HospitalDetailActivity.this, "Không thể tải chi tiết bệnh viện", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onFailure(Call<com.example.schedulemedical.model.dto.response.ApiResponse<HospitalResponse>> call, Throwable t) {
                Toast.makeText(HospitalDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void renderHospital(HospitalResponse h) {
        tvName.setText(h.getName());
        tvType.setText(h.getType());
        tvAddress.setText(h.getAddress());
        tvPhone.setText(h.getPhone());
        tvEmail.setText(h.getEmail());
        tvDescription.setText(h.getDescription());
        tvEstablishYear.setText(h.getEstablishYear() != null ? "Thành lập " + h.getEstablishYear() : "");
        tvWorkScheduling.setText(h.getWorkScheduling());
        tvWebsite.setText(h.getWebsite());
        tvCertificates.setText(h.getCertificates());
        
        // Update numeric displays with proper formatting
        tvLatitude.setText(h.getLatitude() != null ? String.valueOf(h.getLatitude()) : "");
        tvLongitude.setText(h.getLongitude() != null ? String.valueOf(h.getLongitude()) : "");
        tvRating.setText(h.getRating() != null ? String.valueOf(h.getRating()) : "4.5");
        tvReviews.setText(h.getReviews() != null ? "(" + h.getReviews() + " đánh giá)" : "(128 đánh giá)");
        tvTotalBeds.setText(h.getTotalBeds() != null ? h.getTotalBeds() + " giường" : "150 giường");
        tvTotalNurses.setText(h.getTotalNurses() != null ? h.getTotalNurses() + " y tá" : "80 y tá");
        tvDoctorCount.setText(h.getDoctorCount() != null ? h.getDoctorCount() + " bác sĩ" : "25 bác sĩ");
        tvVerified.setText(h.getVerified() != null && h.getVerified() ? "Đã xác thực" : "Chưa xác thực");
        
        // Load hospital logo
        if (h.getLogo() != null && !h.getLogo().isEmpty()) {
            Glide.with(this).load(h.getLogo()).into(ivLogo);
        } else {
            ivLogo.setImageResource(R.drawable.logo_benh_vien_mat);
        }
        
        // Setup gallery images
        setupGallery(h.getGallery());
        
        // For testing - add sample images if no gallery data
        if (h.getGallery() == null || h.getGallery().isEmpty()) {
            setupTestGallery();
        }
    }

    private void setupGallery(String galleryString) {
        android.util.Log.d("HospitalDetail", "setupGallery called with: " + galleryString);
        
        if (galleryString != null && !galleryString.isEmpty()) {
            // Parse gallery string (assuming it's comma-separated URLs)
            List<String> imageUrls = new ArrayList<>();
            if (galleryString.contains(",")) {
                // Split and trim URLs
                String[] urls = galleryString.split(",");
                for (String url : urls) {
                    String trimmedUrl = url.trim();
                    if (!trimmedUrl.isEmpty()) {
                        imageUrls.add(trimmedUrl);
                    }
                }
            } else {
                imageUrls.add(galleryString.trim());
            }
            
            android.util.Log.d("HospitalDetail", "Parsed " + imageUrls.size() + " image URLs: " + imageUrls.toString());
            
            if (!imageUrls.isEmpty()) {
                // Update gallery text to show count and make it expandable
                tvGallery.setText("Xem " + imageUrls.size() + " hình ảnh");
                
                // Setup RecyclerView
                setupGalleryRecyclerView(imageUrls);
                
                // Show gallery by default for testing
                recyclerGallery.setVisibility(View.VISIBLE);
                
                // Set up gallery click to toggle visibility
                tvGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleGalleryVisibility();
                    }
                });
            } else {
                tvGallery.setText("Chưa có hình ảnh");
                recyclerGallery.setVisibility(View.GONE);
            }
        } else {
            android.util.Log.d("HospitalDetail", "Gallery string is null or empty");
            tvGallery.setText("Chưa có hình ảnh");
            recyclerGallery.setVisibility(View.GONE);
        }
    }

    private void setupGalleryRecyclerView(List<String> imageUrls) {
        android.util.Log.d("HospitalDetail", "Setting up RecyclerView with " + imageUrls.size() + " images");
        
        galleryAdapter = new GalleryAdapter(this, imageUrls);
        
        // Setup grid layout with 3 columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerGallery.setLayoutManager(layoutManager);
        recyclerGallery.setAdapter(galleryAdapter);
        
        android.util.Log.d("HospitalDetail", "RecyclerView adapter set, item count: " + galleryAdapter.getItemCount());
        
        // Set item click listener
        galleryAdapter.setOnImageClickListener(new GalleryAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(int position, String imageUrl) {
                android.util.Log.d("HospitalDetail", "Image clicked at position: " + position);
                showImageFullscreen(position, imageUrls);
            }
        });
    }

    private void toggleGalleryVisibility() {
        if (recyclerGallery.getVisibility() == View.GONE) {
            recyclerGallery.setVisibility(View.VISIBLE);
            tvGallery.setText("Ẩn hình ảnh");
        } else {
            recyclerGallery.setVisibility(View.GONE);
            tvGallery.setText("Xem " + (galleryAdapter != null ? galleryAdapter.getItemCount() : 0) + " hình ảnh");
        }
    }

    private void showImageFullscreen(int position, List<String> imageUrls) {
        // For now, just show a toast with image info
        Toast.makeText(this, "Xem ảnh " + (position + 1) + "/" + imageUrls.size(), Toast.LENGTH_SHORT).show();
        // TODO: Implement fullscreen image viewer with ViewPager
    }

    private void setupTestGallery() {
        android.util.Log.d("HospitalDetail", "Setting up test gallery");
        
        // Create test image URLs
        List<String> testUrls = new ArrayList<>();
        testUrls.add("https://picsum.photos/300/200?random=1");
        testUrls.add("https://picsum.photos/300/200?random=2");
        testUrls.add("https://picsum.photos/300/200?random=3");
        testUrls.add("https://picsum.photos/300/200?random=4");
        testUrls.add("https://picsum.photos/300/200?random=5");
        testUrls.add("https://picsum.photos/300/200?random=6");
        
        // Update gallery text
        tvGallery.setText("Xem " + testUrls.size() + " hình ảnh (test)");
        
        // Setup RecyclerView with test data
        setupGalleryRecyclerView(testUrls);
        
        // Show gallery
        recyclerGallery.setVisibility(View.VISIBLE);
        
        // Set up gallery click to toggle visibility
        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGalleryVisibility();
            }
        });
    }
} 