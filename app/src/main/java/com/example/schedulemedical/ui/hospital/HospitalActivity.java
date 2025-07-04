package com.example.schedulemedical.ui.hospital;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.schedulemedical.Adapter.HospitalAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.HospitalApiService;
import com.example.schedulemedical.model.dto.response.HospitalListResponse;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalActivity extends BaseActivity implements HospitalAdapter.OnHospitalClickListener {
    
    private static final String TAG = "HospitalActivity";
    
    // Views
    private RecyclerView rvHospitals;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialCardView searchCard;
    private ImageView btnBack;
    private EditText etSearch;
    
    // Data
    private HospitalAdapter hospitalAdapter;
    private List<HospitalResponse> hospitalList;
    private HospitalApiService hospitalApiService;
    
    // Pagination
    private int currentPage = 1;
    private static final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private boolean hasMoreData = true;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_hospital;
    }

    @Override
    protected void setupViews() {
        initializeViews();
        setupRecyclerView();
        setupNavigation();
        handleIntentExtras();
        
        // Initialize API service and load data
        hospitalApiService = ApiClient.getHospitalApiService();
        loadHospitals(true);
    }
    
    private void initializeViews() {
        rvHospitals = findViewById(R.id.rvHospitals);
        btnBack = findViewById(R.id.btnBack);
        searchCard = findViewById(R.id.search_card);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        etSearch = findViewById(R.id.etSearch);

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = etSearch.getText().toString().trim();
                searchHospitals(keyword);
                return true;
            }
            return false;
        });

        // Gán sự kiện kéo để làm mới
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadHospitals(true); // Tải lại từ đầu
        });
    }
    
    private void setupRecyclerView() {
        hospitalList = new ArrayList<>();
        hospitalAdapter = new HospitalAdapter(this, hospitalList);
        hospitalAdapter.setOnHospitalClickListener(this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvHospitals.setLayoutManager(layoutManager);
        rvHospitals.setAdapter(hospitalAdapter);
        
        // Add scroll listener for pagination
        rvHospitals.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && hasMoreData) {
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    
                    if (lastVisibleItem >= totalItemCount - 5) {
                        loadHospitals(false);
                    }
                }
            }
        });
    }

    private void setupNavigation() {
        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(view -> {
                NavigationHelper.goBack(this);
            });
        }
    }

    private void handleIntentExtras() {
        // Xử lý hospital ID nếu có
        int hospitalId = getIntent().getIntExtra(NavigationHelper.EXTRA_HOSPITAL_ID, -1);
        if (hospitalId != -1) {
            // TODO: Load specific hospital or filter by ID
            Log.d(TAG, "Loading hospital ID: " + hospitalId);
        }
    }
    
    private void loadHospitals(boolean refresh) {
        if (isLoading) return;
        isLoading = true;
        if (refresh) {
            currentPage = 1;
            hasMoreData = true;
        }
        Call<HospitalListResponse> call = hospitalApiService.getAllHospitals(
            currentPage,
            PAGE_SIZE,
            null, // search
            null, // type
            null, // location
            null, // specialty
            null, // latitude
            null, // longitude
            null  // radius
        );
        call.enqueue(new Callback<HospitalListResponse>() {
            @Override
            public void onResponse(Call<HospitalListResponse> call, Response<HospitalListResponse> response) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<HospitalResponse> hospitals = response.body().getData();
                    if (hospitals != null) {
                        if (refresh) {
                            hospitalAdapter.updateHospitals(hospitals);
                        } else {
                            hospitalAdapter.addHospitals(hospitals);
                        }
                        if (response.body().getMeta() != null) {
                            hasMoreData = response.body().getMeta().getHasNext() != null &&
                                    response.body().getMeta().getHasNext();
                            if (!refresh) {
                                currentPage++;
                            }
                        }
                    }
                } else {
                    showError("Lỗi mạng: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<HospitalListResponse> call, Throwable t) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                showError("Không thể kết nối đến máy chủ");
            }
        });
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    @Override
    public void onHospitalClick(HospitalResponse hospital) {
        // Navigate to hospital detail
        if (hospital.getHospitalId() != null) {
            Intent intent = new Intent(this, HospitalDetailActivity.class);
            intent.putExtra("hospitalId", hospital.getHospitalId());
            startActivity(intent);
        }
    }

    @Override
    public void onBookNowClick(HospitalResponse hospital) {

    }

    private void searchHospitals(String keyword) {
        isLoading = true;
        swipeRefreshLayout.setRefreshing(true);
        currentPage = 1;
        hasMoreData = true;

        Call<HospitalListResponse> call = hospitalApiService.getAllHospitals(
            currentPage,
            PAGE_SIZE,
            keyword, // truyền từ khóa search vào đây
            null, // type
            null, // location
            null, // specialty
            null, // latitude
            null, // longitude
            null  // radius
        );
        call.enqueue(new Callback<HospitalListResponse>() {
            @Override
            public void onResponse(Call<HospitalListResponse> call, Response<HospitalListResponse> response) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<HospitalResponse> hospitals = response.body().getData();
                    if (hospitals != null) {
                        hospitalAdapter.updateHospitals(hospitals);
                        // Cập nhật lại phân trang nếu cần
                        if (response.body().getMeta() != null) {
                            hasMoreData = response.body().getMeta().getHasNext() != null &&
                                    response.body().getMeta().getHasNext();
                        }
                    }
                } else {
                    showError("Lỗi mạng: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<HospitalListResponse> call, Throwable t) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                showError("Không thể kết nối đến máy chủ");
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}