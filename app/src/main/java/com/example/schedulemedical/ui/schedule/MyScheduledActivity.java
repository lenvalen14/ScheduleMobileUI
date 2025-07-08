package com.example.schedulemedical.ui.schedule;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.AppointmentAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.repository.AppointmentRepository;
import com.example.schedulemedical.model.dto.response.AppointmentResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import android.util.Log;

public class MyScheduledActivity extends BaseActivity implements AppointmentAdapter.OnAppointmentActionListener {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<AppointmentResponse> allAppointments = new ArrayList<>();
    private AppointmentRepository appointmentRepository;
    private int userId;
    private static final String TAG = "MyScheduledActivity";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_my_scheduled;
    }

    @Override
    protected void setupViews() {
        userId = new AuthManager(this).getUserId();
        appointmentRepository = new AppointmentRepository();
        setupChipFilter();
        setupRecyclerView();
        loadAppointments(null); // load all by default
    }

    private void setupChipFilter() {
        ChipGroup chipGroup = findViewById(R.id.chipGroupFilter);
        if (chipGroup != null) {
            chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
                String status = null;
                if (checkedId == R.id.chipAll) status = null;
                else if (checkedId == R.id.chipPending) status = "PENDING";
                else if (checkedId == R.id.chipUpcoming) status = "SCHEDULED";
                else if (checkedId == R.id.chipCompleted) status = "COMPLETED";
                else if (checkedId == R.id.chipCanceled) status = "CANCELLED";
                loadAppointments(status);
            });
        }
    }

    private void setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView called");
        recyclerView = findViewById(R.id.rvAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(new ArrayList<>());
        adapter.setOnAppointmentActionListener(this);
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "RecyclerView and Adapter set");
    }

    private void loadAppointments(String status) {
        Log.d(TAG, "loadAppointments called with status: " + status);
        int page = 1, limit = 20;
        appointmentRepository.getAppointments(userId, status, page, limit, new AppointmentRepository.DataCallback<ApiResponse>() {
            @Override
            public void onSuccess(ApiResponse response) {
                List<AppointmentResponse> data = new ArrayList<>();
                if (response != null && response.getData() != null) {
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.getData());
                        Log.d(TAG, "API response data json: " + json);
                        Type listType = new TypeToken<List<AppointmentResponse>>(){}.getType();
                        data = gson.fromJson(json, listType);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing appointments", e);
                    }
                }
                allAppointments = data;
                Log.d(TAG, "Appointments loaded: " + data.size());
                adapter.updateData(data);
                updateAppointmentCount(data.size());
            }
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading appointments: " + error);
                allAppointments = new ArrayList<>();
                adapter.updateData(allAppointments);
                updateAppointmentCount(0);
            }
        });
    }

    private void updateAppointmentCount(int count) {
        TextView tvAppointmentCount = findViewById(R.id.tvAppointmentCount);
        if (tvAppointmentCount != null) {
            tvAppointmentCount.setText(count + " appointment" + (count != 1 ? "s" : ""));
        }
    }

    @Override
    public void onCancelAppointment(AppointmentResponse appointment) {
        if (appointment == null || appointment.getAppointmentId() == null) return;
        appointmentRepository.cancelAppointment(appointment.getAppointmentId(), new AppointmentRepository.DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(MyScheduledActivity.this, "Đã hủy lịch thành công!", Toast.LENGTH_SHORT).show();
                loadAppointments(null);
            }
            @Override
            public void onError(String error) {
                Toast.makeText(MyScheduledActivity.this, "Hủy lịch thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRescheduleAppointment(AppointmentResponse appointment) {
        // TODO: Implement reschedule appointment logic
        NavigationHelper.navigateToSchedule(this, appointment.getDoctorId());
    }
}
