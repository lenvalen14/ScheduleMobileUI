package com.example.schedulemedical.ui.filterDoctor;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;

public class FilterDoctorActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_filter_doctor;
    }

    @Override
    protected void setupViews() {
        setupNavigation();
        handleIntentExtras();
    }

    private void setupNavigation() {
        // Back button
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(view -> {
                NavigationHelper.goBack(this);
            });
        }
    }

    private void handleIntentExtras() {
        // Xử lý filter data nếu có
        Bundle filterData = getIntent().getBundleExtra(NavigationHelper.EXTRA_FILTER_DATA);
        if (filterData != null) {
            // TODO: Apply existing filter data
        }
    }
}