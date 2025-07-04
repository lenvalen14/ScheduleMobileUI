package com.example.schedulemedical.ui.booking.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.CalendarAdapter;
import com.example.schedulemedical.Adapter.TimeSlotGridAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.CalendarDay;
import com.example.schedulemedical.model.TimeSlot;
import com.example.schedulemedical.ui.booking.BookingWizardActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleSelectionFragment extends Fragment implements CalendarAdapter.OnDateSelectedListener, TimeSlotGridAdapter.OnTimeSlotSelectedListener {
    private static final String TAG = "ScheduleSelectionFragment";
    private static final String ARG_BOOKING_DATA = "booking_data";
    
    // UI Components
    private TextView tvSelectedDoctor, tvCurrentMonth, btnPrevMonth, btnNextMonth, tvTimeSlotMessage;
    private RecyclerView rvCalendar, rvTimeSlots;
    
    // Data
    private BookingWizardActivity.BookingData bookingData;
    private List<CalendarDay> calendarDays;
    private List<TimeSlot> timeSlots;
    private CalendarAdapter calendarAdapter;
    private TimeSlotGridAdapter timeSlotAdapter;
    
    // Calendar
    private Calendar currentCalendar;
    private Date selectedDate;
    
    public static ScheduleSelectionFragment newInstance(BookingWizardActivity.BookingData bookingData) {
        ScheduleSelectionFragment fragment = new ScheduleSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOKING_DATA, bookingData);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookingData = (BookingWizardActivity.BookingData) getArguments().getSerializable(ARG_BOOKING_DATA);
        }
        
        currentCalendar = Calendar.getInstance();
        calendarDays = new ArrayList<>();
        timeSlots = new ArrayList<>();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_selection, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupClickListeners();
        setupRecyclerViews();
        updateDoctorInfo();
        generateCalendar();
        generateTimeSlots();
    }
    
    private void initializeViews(View view) {
        tvSelectedDoctor = view.findViewById(R.id.tvSelectedDoctor);
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        tvTimeSlotMessage = view.findViewById(R.id.tvTimeSlotMessage);
        rvCalendar = view.findViewById(R.id.rvCalendar);
        rvTimeSlots = view.findViewById(R.id.rvTimeSlots);
    }
    
    private void setupClickListeners() {
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            generateCalendar();
        });
        
        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            generateCalendar();
        });
    }
    
    private void setupRecyclerViews() {
        // Calendar RecyclerView
        calendarAdapter = new CalendarAdapter(requireContext(), calendarDays);
        calendarAdapter.setOnDateSelectedListener(this);
        rvCalendar.setLayoutManager(new GridLayoutManager(requireContext(), 7));
        rvCalendar.setAdapter(calendarAdapter);
        
        // Time Slots RecyclerView
        timeSlotAdapter = new TimeSlotGridAdapter(requireContext(), timeSlots);
        timeSlotAdapter.setOnTimeSlotSelectedListener(this);
        rvTimeSlots.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvTimeSlots.setAdapter(timeSlotAdapter);
    }
    
    private void updateDoctorInfo() {
        if (bookingData.doctorName != null) {
            tvSelectedDoctor.setText("Bác sĩ: " + bookingData.doctorName);
            tvSelectedDoctor.setVisibility(View.VISIBLE);
        } else {
            tvSelectedDoctor.setVisibility(View.GONE);
        }
    }
    
    private void generateCalendar() {
        calendarDays.clear();
        
        Log.d(TAG, "generateCalendar() called");
        
        // Update month display
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", new Locale("vi", "VN"));
        tvCurrentMonth.setText(monthFormat.format(currentCalendar.getTime()));
        
        Log.d(TAG, "Current month: " + monthFormat.format(currentCalendar.getTime()));
        
        // Create calendar
        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        Log.d(TAG, "First day of week: " + firstDayOfWeek + ", Days in month: " + daysInMonth);
        
        // Add empty days for previous month
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarDays.add(new CalendarDay());
        }
        
        Log.d(TAG, "Added " + firstDayOfWeek + " empty days");
        
        // Add days of current month
        Calendar today = Calendar.getInstance();
        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
            Date date = cal.getTime();
            
            boolean isToday = isSameDay(date, today.getTime());
            boolean isEnabled = !date.before(today.getTime()); // Only future dates
            boolean isAvailable = isEnabled && isDoctorAvailable(date);
            
            CalendarDay calendarDay = new CalendarDay(date, day, isToday, isEnabled, isAvailable);
            calendarDays.add(calendarDay);
        }
        
        Log.d(TAG, "Total calendar days created: " + calendarDays.size());
        Log.d(TAG, "Days in month added: " + daysInMonth);
        
        // Debug: Log first few days
        for (int i = 0; i < Math.min(10, calendarDays.size()); i++) {
            CalendarDay day = calendarDays.get(i);
            Log.d(TAG, "Day " + i + ": " + (day.isEmpty() ? "EMPTY" : "Day " + day.getDay() + " - Enabled: " + day.isEnabled()));
        }
        
        calendarAdapter.updateCalendarDays(calendarDays);
        Log.d(TAG, "Calendar adapter updated");
    }
    
    private void generateTimeSlots() {
        timeSlots.clear();
        
        Log.d(TAG, "generateTimeSlots() called - selectedDate: " + selectedDate);
        
        if (selectedDate == null) {
            // No date selected yet - show message
            Log.d(TAG, "No date selected, showing message");
            showTimeSlotMessage("Vui lòng chọn ngày để xem khung giờ khả dụng");
            timeSlotAdapter.notifyDataSetChanged();
            return;
        }
        
        // Hide message and show time slots
        hideTimeSlotMessage();
        
        // Mock time slots - in real app, this would come from doctor's schedule
        String[] slots = {
            "08:00 - 08:30", "08:30 - 09:00", "09:00 - 09:30",
            "09:30 - 10:00", "10:00 - 10:30", "10:30 - 11:00",
            "14:00 - 14:30", "14:30 - 15:00", "15:00 - 15:30",
            "15:30 - 16:00", "16:00 - 16:30", "16:30 - 17:00"
        };
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Log.d(TAG, "Generating time slots for date: " + dateFormat.format(selectedDate));
        
        for (String slot : slots) {
            int capacity = 5; // Mock capacity
            int booked = (int) (Math.random() * 3); // Mock booked count (0-2 so always some available)
            boolean isAvailable = booked < capacity;
            
            TimeSlot timeSlot = new TimeSlot(slot, capacity, booked, isAvailable);
            timeSlots.add(timeSlot);
        }
        
        Log.d(TAG, "Generated " + timeSlots.size() + " time slots");
        timeSlotAdapter.notifyDataSetChanged();
    }
    
    private void showTimeSlotMessage(String message) {
        if (tvTimeSlotMessage != null) {
            tvTimeSlotMessage.setText(message);
            tvTimeSlotMessage.setVisibility(View.VISIBLE);
        }
        if (rvTimeSlots != null) {
            rvTimeSlots.setVisibility(View.GONE);
        }
    }
    
    private void hideTimeSlotMessage() {
        if (tvTimeSlotMessage != null) {
            tvTimeSlotMessage.setVisibility(View.GONE);
        }
        if (rvTimeSlots != null) {
            rvTimeSlots.setVisibility(View.VISIBLE);
        }
    }
    
    private boolean isDoctorAvailable(Date date) {
        // Mock availability - in real app, check doctor's schedule
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        
        // Assume doctor is available Monday to Friday
        return dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY;
    }
    
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    
    @Override
    public void onDateSelected(CalendarDay day, int position) {
        Log.d(TAG, "onDateSelected called - day: " + day.getDay() + ", date: " + day.getDate());
        
        // Add Toast to confirm date selection
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Toast.makeText(requireContext(), "Đã chọn ngày: " + dateFormat.format(day.getDate()), Toast.LENGTH_SHORT).show();
        
        selectedDate = day.getDate();
        bookingData.selectedDate = selectedDate;
        
        Log.d(TAG, "Updated selectedDate: " + selectedDate);
        
        // Regenerate time slots for selected date
        generateTimeSlots();
        
        // Clear previous time slot selection
        bookingData.selectedTimeSlot = null;
        Log.d(TAG, "Cleared previous time slot selection");
        
        // Notify parent activity
        if (getActivity() instanceof BookingWizardActivity) {
            Log.d(TAG, "Calling onStepDataChanged()");
            ((BookingWizardActivity) getActivity()).onStepDataChanged();
        } else {
            Log.e(TAG, "Activity is not BookingWizardActivity");
        }
    }
    
    @Override
    public void onTimeSlotSelected(TimeSlot timeSlot, int position) {
        Log.d(TAG, "onTimeSlotSelected called - timeSlot: " + timeSlot.getTimeRange());
        
        // Add Toast to confirm time slot selection
        Toast.makeText(requireContext(), "Đã chọn khung giờ: " + timeSlot.getTimeRange(), Toast.LENGTH_SHORT).show();
        
        bookingData.selectedTimeSlot = timeSlot.getTimeRange();
        
        Log.d(TAG, "Updated selectedTimeSlot: " + bookingData.selectedTimeSlot);
        
        // Notify parent activity
        if (getActivity() instanceof BookingWizardActivity) {
            Log.d(TAG, "Calling onStepDataChanged()");
            ((BookingWizardActivity) getActivity()).onStepDataChanged();
        } else {
            Log.e(TAG, "Activity is not BookingWizardActivity");
        }
    }
} 