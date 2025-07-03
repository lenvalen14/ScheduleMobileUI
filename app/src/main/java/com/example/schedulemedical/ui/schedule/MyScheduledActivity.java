package com.example.schedulemedical.ui.schedule;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.AppointmentAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.request.appoiment.AppointmentDTO;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyScheduledActivity extends BaseActivity implements AppointmentAdapter.OnAppointmentActionListener {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<AppointmentDTO> allAppointments;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_my_scheduled;
    }

    @Override
    protected void setupViews() {
        setupToolbar();
        setupChipFilter();
        setupRecyclerView();
        loadAllAppointments();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(view -> {
                NavigationHelper.goBack(this);
            });
        }
    }

    private void setupChipFilter() {
        ChipGroup chipGroup = findViewById(R.id.chipGroupFilter);
        if (chipGroup != null) {
            chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.chipAll) {
                    adapter.updateData(allAppointments);
                    updateAppointmentCount(allAppointments.size());
                } else if (checkedId == R.id.chipUpcoming) {
                    List<AppointmentDTO> filtered = filterByStatus(AppointmentDTO.AppointmentStatus.PENDING);
                    adapter.updateData(filtered);
                    updateAppointmentCount(filtered.size());
                } else if (checkedId == R.id.chipCompleted) {
                    List<AppointmentDTO> filtered = filterByStatus(AppointmentDTO.AppointmentStatus.COMPLETED);
                    adapter.updateData(filtered);
                    updateAppointmentCount(filtered.size());
                } else if (checkedId == R.id.chipCanceled) {
                    List<AppointmentDTO> filtered = filterByStatus(AppointmentDTO.AppointmentStatus.CANCELLED);
                    adapter.updateData(filtered);
                    updateAppointmentCount(filtered.size());
                }
            });
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rvAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(new ArrayList<>());
        adapter.setOnAppointmentActionListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void loadAllAppointments() {
        // Mock dữ liệu lịch hẹn với AppointmentDTO
        allAppointments = new ArrayList<>();
        
        // Tạo mock appointments
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            allAppointments.add(new AppointmentDTO(1, 1, 1,
                LocalDateTime.now().plusDays(5), "Khám tổng quát"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            allAppointments.add(new AppointmentDTO(2, 1, 1,
                LocalDateTime.now().minusDays(2), "Khám mắt"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            allAppointments.add(new AppointmentDTO(3, 1, 1,
                LocalDateTime.now().plusDays(6), "Tư vấn da liễu"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            allAppointments.add(new AppointmentDTO(4, 1, 1,
                LocalDateTime.now().minusDays(1), "Tiêm vaccine"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            allAppointments.add(new AppointmentDTO(5, 1, 1,
                LocalDateTime.now().minusDays(3), "Siêu âm"));
        }

        // Set status cho mock data
        allAppointments.get(0).setStatus(AppointmentDTO.AppointmentStatus.PENDING);
        allAppointments.get(1).setStatus(AppointmentDTO.AppointmentStatus.COMPLETED);
        allAppointments.get(2).setStatus(AppointmentDTO.AppointmentStatus.PENDING);
        allAppointments.get(3).setStatus(AppointmentDTO.AppointmentStatus.CANCELLED);
        allAppointments.get(4).setStatus(AppointmentDTO.AppointmentStatus.COMPLETED);

        adapter.updateData(allAppointments);
        updateAppointmentCount(allAppointments.size());

        // Chọn mặc định Chip "All"
        Chip chipAll = findViewById(R.id.chipAll);
        if (chipAll != null) {
            chipAll.setChecked(true);
        }
    }

    private List<AppointmentDTO> filterByStatus(AppointmentDTO.AppointmentStatus status) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return allAppointments.stream()
                    .filter(appointment -> appointment.getStatus() == status)
                    .collect(Collectors.toList());
        } else {
            List<AppointmentDTO> filtered = new ArrayList<>();
            for (AppointmentDTO appointment : allAppointments) {
                if (appointment.getStatus() == status) {
                    filtered.add(appointment);
                }
            }
            return filtered;
        }
    }

    private void updateAppointmentCount(int count) {
        TextView tvAppointmentCount = findViewById(R.id.tvAppointmentCount);
        if (tvAppointmentCount != null) {
            tvAppointmentCount.setText(count + " appointment" + (count != 1 ? "s" : ""));
        }
    }

    @Override
    public void onCancelAppointment(AppointmentDTO appointment) {
        // TODO: Implement cancel appointment logic
        appointment.setStatus(AppointmentDTO.AppointmentStatus.CANCELLED);
        adapter.updateData(allAppointments);
    }

    @Override
    public void onRescheduleAppointment(AppointmentDTO appointment) {
        // TODO: Implement reschedule appointment logic
        NavigationHelper.navigateToSchedule(this, appointment.getDoctorId());
    }
}
