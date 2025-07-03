package com.example.schedulemedical.ui.hospital;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;

public class HospitalActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_hospital;
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

        // Search button
//        ImageButton searchButton = findViewById(R.id.btnSearch);
//        if (searchButton != null) {
//            searchButton.setOnClickListener(view -> {
//                NavigationHelper.navigateToFilterDoctor(this);
//            });
//        }
    }

    private void handleIntentExtras() {
        // Xử lý hospital ID nếu có
        int hospitalId = getIntent().getIntExtra(NavigationHelper.EXTRA_HOSPITAL_ID, -1);
        if (hospitalId != -1) {
            // TODO: Load hospital data based on ID
            Toast.makeText(this, "Loading hospital ID: " + hospitalId, Toast.LENGTH_SHORT).show();
        }
    }
}