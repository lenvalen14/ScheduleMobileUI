package com.example.schedulemedical.ui.schedule;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;

public class MyScheduledActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_my_scheduled;
    }

    @Override
    protected void setupViews() {
        setupNavigation();
//        loadScheduledAppointments();
    }

    private void setupNavigation() {
        // Back button
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(view -> {
                NavigationHelper.goBack(this);
            });
        }

        // Profile button
        ImageButton profileButton = findViewById(R.id.nav_profile);
        if (profileButton != null) {
            profileButton.setOnClickListener(view -> {
                NavigationHelper.navigateToUserProfile(this);
            });
        }
    }

//    private void loadScheduledAppointments() {
//        // TODO: Load scheduled appointments from API
//        RecyclerView appointmentsRecyclerView = findViewById(R.id.rvAppointments);
//        if (appointmentsRecyclerView != null) {
//            // TODO: Set up adapter for appointments list
//        }
//
//        // Update appointment count
//        TextView appointmentCountText = findViewById(R.id.tvAppointmentCount);
//        if (appointmentCountText != null) {
//            appointmentCountText.setText("5 appointments"); // Placeholder
//        }
//    }
}