package com.example.schedulemedical.ui.schedule;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.ScheduleAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.ScheduleResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.ui.doctorprofile.DoctorViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageSchedule extends BaseActivity implements ScheduleAdapter.OnScheduleActionListener {

    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private final List<ScheduleResponse> scheduleList = new ArrayList<>();
    private DoctorViewModel doctorViewModel;
    private int doctorId = -1;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_manage_schedule;
    }

    @Override
    protected void setupViews() {
        doctorId = getIntent().getIntExtra("doctorId", -1);

        recyclerView = findViewById(R.id.rvSchedules);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ScheduleAdapter(scheduleList);
        adapter.setOnScheduleActionListener(this);
        recyclerView.setAdapter(adapter);

        doctorViewModel = new ViewModelProvider(this).get(DoctorViewModel.class);

        doctorViewModel.doctorSchedules.observe(this, schedules -> {
            if (schedules != null) {
                Log.d("ManageSchedule", "Fetched " + schedules.size() + " schedules from API");
                scheduleList.clear();
                scheduleList.addAll(schedules);
                adapter.updateData(scheduleList);
            } else {
                Log.e("ManageSchedule", "Schedules are null or API failed");
            }
        });

        doctorViewModel.loadSchedulesByDoctorId(doctorId);

        findViewById(R.id.btnAddSchedule).setOnClickListener(v -> showScheduleDialog(null));
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onEdit(ScheduleResponse schedule) {
        showScheduleDialog(schedule);
    }

    @Override
    public void onDelete(ScheduleResponse schedule) {
        new AlertDialog.Builder(this)
                .setTitle("Xoá lịch")
                .setMessage("Bạn có chắc muốn xoá lịch làm việc này?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    doctorViewModel.deleteSchedule(schedule.getScheduleId()).observe(this, response -> {
                        if (Boolean.TRUE.equals(response)) {
                            Toast.makeText(this, "Đã xoá lịch", Toast.LENGTH_SHORT).show();
                            doctorViewModel.loadSchedulesByDoctorId(doctorId);
                        } else {
                            Toast.makeText(this, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void showScheduleDialog(ScheduleResponse existing) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_schedule_form, null);
        builder.setView(view);

        Spinner spinnerDay = view.findViewById(R.id.spinnerDay);
        TextView tvStart = view.findViewById(R.id.timeStart);
        TextView tvEnd = view.findViewById(R.id.timeEnd);

        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
        spinnerDay.setAdapter(dayAdapter);

        final int[] startHour = {8}, startMinute = {0};
        final int[] endHour = {17}, endMinute = {0};

        if (existing != null) {
            spinnerDay.setSelection(existing.getDayOfWeek() - 1);
            String[] startParts = existing.getStartTime().split(":");
            String[] endParts = existing.getEndTime().split(":");

            startHour[0] = Integer.parseInt(startParts[0]);
            startMinute[0] = Integer.parseInt(startParts[1]);
            endHour[0] = Integer.parseInt(endParts[0]);
            endMinute[0] = Integer.parseInt(endParts[1]);

            tvStart.setText(String.format(Locale.getDefault(), "%02d:%02d", startHour[0], startMinute[0]));
            tvEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", endHour[0], endMinute[0]));
        }

        tvStart.setOnClickListener(v -> {
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                startHour[0] = hourOfDay;
                startMinute[0] = minute;
                tvStart.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }, startHour[0], startMinute[0], true).show();
        });

        tvEnd.setOnClickListener(v -> {
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                endHour[0] = hourOfDay;
                endMinute[0] = minute;
                tvEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }, endHour[0], endMinute[0], true).show();
        });

        builder.setTitle(existing == null ? "Thêm lịch làm việc" : "Chỉnh sửa lịch");
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            int selectedDay = spinnerDay.getSelectedItemPosition() + 1;
            String start = String.format(Locale.getDefault(), "%02d:%02d", startHour[0], startMinute[0]);
            String end = String.format(Locale.getDefault(), "%02d:%02d", endHour[0], endMinute[0]);

            if (existing == null) {
                ScheduleResponse newSchedule = new ScheduleResponse(null, doctorId, selectedDay, start, end);
                doctorViewModel.createSchedule(newSchedule).observe(this, response -> {
                    if (response != null) {
                        Toast.makeText(this, "Thêm lịch thành công", Toast.LENGTH_SHORT).show();
                        doctorViewModel.loadSchedulesByDoctorId(doctorId);
                    } else {
                        Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                existing.setDayOfWeek(selectedDay);
                existing.setStartTime(start);
                existing.setEndTime(end);

                doctorViewModel.updateSchedule(existing.getScheduleId(), existing).observe(this, response -> {
                    if (response != null) {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        doctorViewModel.loadSchedulesByDoctorId(doctorId);
                    } else {
                        Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Huỷ", null);
        builder.show();
    }
}
