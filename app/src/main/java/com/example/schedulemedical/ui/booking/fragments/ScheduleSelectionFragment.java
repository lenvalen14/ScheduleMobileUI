package com.example.schedulemedical.ui.booking.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private static final String ARG_BOOKING_DATA = "booking_data";
    
    // UI Components
    private TextView tvSelectedDoctor, tvCurrentMonth, btnPrevMonth, btnNextMonth;
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
        
        // Update month display
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", new Locale("vi", "VN"));
        tvCurrentMonth.setText(monthFormat.format(currentCalendar.getTime()));
        
        // Create calendar
        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Add empty days for previous month
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarDays.add(new CalendarDay());
        }
        
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
        
        calendarAdapter.notifyDataSetChanged();
    }
    
    private void generateTimeSlots() {
        timeSlots.clear();
        
        // Mock time slots - in real app, this would come from doctor's schedule
        String[] slots = {
            "08:00 - 08:30", "08:30 - 09:00", "09:00 - 09:30",
            "09:30 - 10:00", "10:00 - 10:30", "10:30 - 11:00",
            "14:00 - 14:30", "14:30 - 15:00", "15:00 - 15:30",
            "15:30 - 16:00", "16:00 - 16:30", "16:30 - 17:00"
        };
        
        for (String slot : slots) {
            int capacity = 5; // Mock capacity
            int booked = (int) (Math.random() * capacity); // Mock booked count
            boolean isAvailable = booked < capacity && selectedDate != null;
            
            TimeSlot timeSlot = new TimeSlot(slot, capacity, booked, isAvailable);
            timeSlots.add(timeSlot);
        }
        
        timeSlotAdapter.notifyDataSetChanged();
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
        selectedDate = day.getDate();
        bookingData.selectedDate = selectedDate;
        
        // Regenerate time slots for selected date
        generateTimeSlots();
        
        // Clear previous time slot selection
        bookingData.selectedTimeSlot = null;
        
        // Notify parent activity
        if (getActivity() instanceof BookingWizardActivity) {
            ((BookingWizardActivity) getActivity()).onStepDataChanged();
        }
    }
    
    @Override
    public void onTimeSlotSelected(TimeSlot timeSlot, int position) {
        bookingData.selectedTimeSlot = timeSlot.getTimeRange();
        
        // Notify parent activity
        if (getActivity() instanceof BookingWizardActivity) {
            ((BookingWizardActivity) getActivity()).onStepDataChanged();
        }
    }
} 