//package com.example.schedulemedical.ui.home;
//
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.example.schedulemedical.R;
//import com.example.schedulemedical.ui.base.BaseActivity;
//import com.example.schedulemedical.utils.NavigationHelper;
//
//public class HomeActivity extends BaseActivity {
//
//    @Override
//    protected int getLayoutResourceId() {
//        return R.layout.activity_home;
//    }
//
//    @Override
//    protected void setupViews() {
//        setupNavigation();
//    }
//
//    private void setupNavigation() {
//        // Navigation đến Hospital
//        ImageView hospitalIcon = findViewById(R.id.ivHospital);
//        if (hospitalIcon != null) {
//            hospitalIcon.setOnClickListener(view -> {
//                NavigationHelper.navigateToHospital(this);
//            });
//        }
//
//        // Navigation đến Filter Doctor
//        ImageView searchIcon = findViewById(R.id.ivSearch);
//        if (searchIcon != null) {
//            searchIcon.setOnClickListener(view -> {
//                NavigationHelper.navigateToFilterDoctor(this);
//            });
//        }
//
//        // Navigation đến User Profile
//        ImageView profileIcon = findViewById(R.id.ivProfile);
//        if (profileIcon != null) {
//            profileIcon.setOnClickListener(view -> {
//                NavigationHelper.navigateToUserProfile(this);
//            });
//        }
//
//        // Navigation đến My Scheduled
//        TextView myScheduledText = findViewById(R.id.tvMyScheduled);
//        if (myScheduledText != null) {
//            myScheduledText.setOnClickListener(view -> {
//                NavigationHelper.navigateToMyScheduled(this);
//            });
//        }
//
//        // Navigation đến Chat
//        ImageView chatIcon = findViewById(R.id.ivChat);
//        if (chatIcon != null) {
//            chatIcon.setOnClickListener(view -> {
//                NavigationHelper.navigateToChat(this);
//            });
//        }
//    }
//}