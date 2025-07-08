package com.example.schedulemedical.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.model.Notification;
import com.example.schedulemedical.model.dto.response.NotificationResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.widget.FrameLayout;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NotificationFragment extends Fragment {
    private NotificationViewModel viewModel;
    private NotificationAdapter adapter;
    private ProgressBar progressBar;
    private TextView errorText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_notifications);
        progressBar = view.findViewById(R.id.progress_bar);
        errorText = view.findViewById(R.id.text_error);
        adapter = new NotificationAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Sử dụng ApiClient để đồng bộ baseUrl và header
        Retrofit retrofit = ApiClient.getRetrofit();
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new NotificationViewModel(retrofit);
            }
        }).get(NotificationViewModel.class);

        // Xử lý nút back
        ImageView btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }
        // Ẩn nút notification nếu có
        ImageView btnNotification = view.findViewById(R.id.btnNotification);
        if (btnNotification != null) btnNotification.setVisibility(View.GONE);
        // Đồng bộ trạng thái bottom navigation
        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                    return true;
                } else if (itemId == R.id.nav_explore) {
                    com.example.schedulemedical.utils.NavigationHelper.navigateToHospital(requireContext());
                    return true;
                } else if (itemId == R.id.nav_calendar) {
                    com.example.schedulemedical.utils.NavigationHelper.navigateToBookingWizard(requireContext());
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    com.example.schedulemedical.utils.NavigationHelper.navigateToUserProfile(requireContext());
                    return true;
                }
                return false;
            });
        }
        // Giả sử userId lấy từ SharedPreferences/AuthManager
        int userId = 1;
        viewModel.fetchNotifications(userId, 1, 20);

        viewModel.getNotifications().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getData() != null) {
                adapter.setNotifications(response.getData());
                errorText.setVisibility(View.GONE);
            }
        });
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        });
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                errorText.setText(error);
                errorText.setVisibility(View.VISIBLE);
            } else {
                errorText.setVisibility(View.GONE);
            }
        });
    }

    // Adapter cho RecyclerView
    private static class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
        private List<Notification> notifications;
        public NotificationAdapter(List<Notification> notifications) {
            this.notifications = notifications;
        }
        public void setNotifications(List<Notification> notifications) {
            this.notifications = notifications;
            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Notification notification = notifications.get(position);
            holder.title.setText(notification.getTitle());
            holder.content.setText(notification.getContent());
            // Format thời gian
            if (notification.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                holder.time.setText(sdf.format(notification.getCreatedAt()));
            } else {
                holder.time.setText("");
            }
            // Hiển thị indicator nếu chưa đọc
            if (!notification.isRead()) {
                holder.unreadIndicator.setVisibility(View.VISIBLE);
            } else {
                holder.unreadIndicator.setVisibility(View.GONE);
            }
            // Icon luôn là ic_medical_notification, màu đã set ở drawable
        }
        @Override
        public int getItemCount() {
            return notifications.size();
        }
        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, content, time;
            View unreadIndicator;
            FrameLayout iconContainer;
            ImageView icon;
            ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.text_title);
                content = itemView.findViewById(R.id.text_content);
                time = itemView.findViewById(R.id.text_time);
                unreadIndicator = itemView.findViewById(R.id.unread_indicator);
                iconContainer = itemView.findViewById(R.id.icon_container);
                icon = itemView.findViewById(R.id.icon);
            }
        }
    }
} 