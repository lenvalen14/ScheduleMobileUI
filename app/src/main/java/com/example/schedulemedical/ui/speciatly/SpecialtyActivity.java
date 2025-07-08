package com.example.schedulemedical.ui.speciatly;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.schedulemedical.Adapter.SpecialtyAdapterView;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.repository.DoctorRepository;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.ui.filterDoctor.DoctorFilterOptionsViewModel;
import com.example.schedulemedical.ui.filterDoctor.DoctorFilterOptionsViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class SpecialtyActivity extends BaseActivity {

    private SpecialtyAdapterView adapter;
    private DoctorFilterOptionsViewModel viewModel;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_speciatly; // Đảm bảo layout này đúng tên file XML
    }

    @Override
    protected void setupViews() {
        // Ánh xạ view
        RecyclerView recyclerView = findViewById(R.id.recyclerViewSpecialties);
        EditText etSearch = findViewById(R.id.etSearch);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        ImageView btnBack = findViewById(R.id.btnBack);

        // Khởi tạo adapter
        adapter = new SpecialtyAdapterView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Tạo ViewModel
        DoctorRepository repo = new DoctorRepository();
        DoctorFilterOptionsViewModelFactory factory = new DoctorFilterOptionsViewModelFactory(repo);
        viewModel = new ViewModelProvider(this, factory).get(DoctorFilterOptionsViewModel.class);

        // Quan sát LiveData
        viewModel.specialties.observe(this, specialties -> {
            adapter.setSpecialties(specialties);
            swipeRefreshLayout.setRefreshing(false);
        });

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.loadSpecialties());

        // Tìm kiếm chuyên khoa theo tên
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().toLowerCase();
                List<SpecialtyResponse> filtered = new ArrayList<>();
                if (viewModel.specialties.getValue() != null) {
                    for (SpecialtyResponse sp : viewModel.specialties.getValue()) {
                        if (sp.getName().toLowerCase().contains(query)) {
                            filtered.add(sp);
                        }
                    }
                }
                adapter.setSpecialties(filtered);
            }
        });

        // Xử lý nút back
        btnBack.setOnClickListener(v -> finish());
    }
}
