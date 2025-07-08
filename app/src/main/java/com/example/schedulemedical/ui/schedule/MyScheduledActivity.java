package com.example.schedulemedical.ui.schedule;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.AppointmentAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.repository.AppointmentRepository;
import com.example.schedulemedical.model.dto.response.AppointmentResponse;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyScheduledActivity extends BaseActivity implements AppointmentAdapter.OnAppointmentActionListener {

    private static final String TAG = "MyScheduledActivity";

    private AuthManager authManager;
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<AppointmentResponse> allAppointments = new ArrayList<>();

    private AppointmentRepository appointmentRepository;
    private int userId;
    private int doctorId;
    private String role;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_my_scheduled;
    }

    @Override
    protected void setupViews() {
        role = getIntent().getStringExtra("role");
        doctorId = getIntent().getIntExtra("doctorId", -1);

        authManager = new AuthManager(this);
        userId = authManager.getUserId();
        appointmentRepository = new AppointmentRepository();

        setupChipFilter();
        setupRecyclerView();
        loadAppointments(null);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
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
        recyclerView = findViewById(R.id.rvAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(new ArrayList<>(), "DOCTOR".equalsIgnoreCase(role));
        adapter.setOnAppointmentActionListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void loadAppointments(String status) {
        Log.d(TAG, "Loading appointments - Status: " + status);

        int page = 1;
        int limit = 20;

        Integer fetchUserId = null;
        Integer fetchDoctorId = null;

        if ("DOCTOR".equalsIgnoreCase(role)) {
            fetchDoctorId = doctorId;
        } else {
            fetchUserId = userId;
        }

        appointmentRepository.getAppointments(
                page, limit, fetchUserId, fetchDoctorId, status,
                new AppointmentRepository.DataCallback<ApiResponse>() {
                    @Override
                    public void onSuccess(ApiResponse response) {
                        List<AppointmentResponse> data = new ArrayList<>();
                        if (response != null && response.getData() != null) {
                            try {
                                String json = new Gson().toJson(response.getData());
                                Type listType = new TypeToken<List<AppointmentResponse>>(){}.getType();
                                data = new Gson().fromJson(json, listType);
                                Log.d(TAG, "Appointments loaded: " + data.size());
                            } catch (Exception e) {
                                Log.e(TAG, "Parsing error", e);
                            }
                        }
                        allAppointments = data;
                        adapter.updateData(data);
                        updateAppointmentCount(data.size());
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Load error: " + error);
                        allAppointments = new ArrayList<>();
                        adapter.updateData(allAppointments);
                        updateAppointmentCount(0);
                    }
                }
        );
    }

    private void updateAppointmentCount(int count) {
        TextView tvAppointmentCount = findViewById(R.id.tvAppointmentCount);
        if (tvAppointmentCount != null) {
            tvAppointmentCount.setText(count + " lịch hẹn");
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
        if (appointment != null && appointment.getDoctorId() != null) {
            NavigationHelper.navigateToSchedule(this, appointment.getDoctorId());
        }
    }

    @Override
    public void onConfirmAppointment(AppointmentResponse appointment) {
        if (appointment == null || appointment.getAppointmentId() == null) return;

        appointmentRepository.confirmAppointment(appointment.getAppointmentId(), new AppointmentRepository.DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(MyScheduledActivity.this, "Xác nhận thành công!", Toast.LENGTH_SHORT).show();
                loadAppointments(null);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MyScheduledActivity.this, "Xác nhận thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
