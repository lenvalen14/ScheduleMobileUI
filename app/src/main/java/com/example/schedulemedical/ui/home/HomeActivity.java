package com.example.schedulemedical.ui.home;

import android.widget.TextView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends BaseActivity {


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected void setupViews() {
        setupBottomNavigation();

        TextView tvSeeAllDoctor = findViewById(R.id.tvSeeAllDoctor);
        if (tvSeeAllDoctor != null) {
            tvSeeAllDoctor.setOnClickListener(v -> {
                NavigationHelper.navigateToFilterDoctor(this);
            });
        }

        TextView tvSeeAllHospital = findViewById(R.id.tvSeeAllHospital);
        if (tvSeeAllHospital != null) {
            tvSeeAllHospital.setOnClickListener(v -> {
                NavigationHelper.navigateToHospital(this);
            });
        }

    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    return true;
                } else if (itemId == R.id.nav_explore) {
                    NavigationHelper.navigateToHospital(this);
                    return true;
                } else if (itemId == R.id.nav_calendar) {
                    // TODO: Điều hướng đến màn hình lịch
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    NavigationHelper.navigateToUserProfile(this);
                    return true;
                }

                return false;
            });
        }
    }
}
