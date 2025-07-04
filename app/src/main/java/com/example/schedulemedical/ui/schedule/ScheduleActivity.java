package com.example.schedulemedical.ui.schedule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.CalendarAdapter;
import com.example.schedulemedical.Adapter.ServiceAdapter;
import com.example.schedulemedical.Adapter.TimeSlotGridAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.AppointmentApiService;
import com.example.schedulemedical.data.api.DoctorApiService;
import com.example.schedulemedical.data.api.ServiceApiService;
import com.example.schedulemedical.model.CalendarDay;
import com.example.schedulemedical.model.Service;
import com.example.schedulemedical.model.TimeSlot;
import com.example.schedulemedical.model.dto.request.CreateAppointmentRequest;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.ui.payment.PaymentActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity implements 
    ServiceAdapter.OnServiceSelectedListener,
    TimeSlotGridAdapter.OnTimeSlotClickListener,
    CalendarAdapter.OnDateClickListener {
    
    private static final String TAG = "ScheduleActivity";
    
    // UI Components
    private ImageView btnBack, btnNotification, btnPrevMonth, btnNextMonth;
    private CircleImageView ivDoctorAvatar;
    private TextView tvDoctorName, tvSpecialty, tvHospitalName, tvConsultationFee;
    private TextView tvMonthYear, tvNoServices, tvNoTimeSlots;
    private ProgressBar progressServices, progressTimeSlots;
    private RecyclerView rvServices, rvTimeSlots, calendarRecyclerView;
    private MaterialButton btnBookAppointment;
    private MaterialCardView doctorInfoCard, serviceSelectionCard;
    
    // Adapters
    private ServiceAdapter serviceAdapter;
    private TimeSlotGridAdapter timeSlotAdapter;
    private CalendarAdapter calendarAdapter;
    
    // Data
    private Integer doctorId;
    private String doctorName, specialty, hospitalName, consultationFee;
    private List<Service> servicesList;
    private List<TimeSlot> timeSlotsList;
    private List<CalendarDay> calendarDays;
    private Calendar currentCalendar, selectedDate;
    private Service selectedService;
    private TimeSlot selectedTimeSlot;
    private CalendarDay selectedCalendarDay;
    
    // Services
    private DoctorApiService doctorApiService;
    private ServiceApiService serviceApiService;
    private AppointmentApiService appointmentApiService;
    private AuthManager authManager;
    private ProgressDialog progressDialog;
    
    // Date formatters
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", new Locale("vi", "VN"));
    private SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        
        initializeServices();
        initializeViews();
        getIntentData();
        setupClickListeners();
        setupRecyclerViews();
        setupProgressDialog();
        setupCalendar();
        
        if (doctorId != null) {
            loadDoctorInfo();
            loadServices();
        } else {
            showError("Thông tin bác sĩ không hợp lệ");
            finish();
        }
    }
    
    private void initializeServices() {
        ApiClient.init(this);
        doctorApiService = ApiClient.getDoctorApiService();
        serviceApiService = ApiClient.getServiceApiService();
        appointmentApiService = ApiClient.getAppointmentApiService();
        authManager = new AuthManager(this);
        
        // Initialize lists
        servicesList = new ArrayList<>();
        timeSlotsList = new ArrayList<>();
        calendarDays = new ArrayList<>();
        currentCalendar = Calendar.getInstance();
    }
    
    private void initializeViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        btnNotification = findViewById(R.id.btnNotification);
        
        // Doctor info
        doctorInfoCard = findViewById(R.id.doctorInfoCard);
        ivDoctorAvatar = findViewById(R.id.ivDoctorAvatar);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvConsultationFee = findViewById(R.id.tvConsultationFee);
        
        // Service selection
        serviceSelectionCard = findViewById(R.id.serviceSelectionCard);
        rvServices = findViewById(R.id.rvServices);
        progressServices = findViewById(R.id.progressServices);
        tvNoServices = findViewById(R.id.tvNoServices);
        
        // Calendar
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        
        // Time slots
        rvTimeSlots = findViewById(R.id.rvTimeSlots);
        progressTimeSlots = findViewById(R.id.progressTimeSlots);
        tvNoTimeSlots = findViewById(R.id.tvNoTimeSlots);
        
        // Book button
        btnBookAppointment = findViewById(R.id.btnBookAppointment);
    }
    
    private void getIntentData() {
        Intent intent = getIntent();
        doctorId = intent.getIntExtra("doctorId", -1);
        doctorName = intent.getStringExtra("doctorName");
        specialty = intent.getStringExtra("specialty");
        hospitalName = intent.getStringExtra("hospitalName");
        consultationFee = intent.getStringExtra("consultationFee");
        
        if (doctorId == -1) {
            doctorId = null;
        }
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });
        
        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });
        
        btnBookAppointment.setOnClickListener(v -> createAppointment());
    }
    
    private void setupRecyclerViews() {
        // Services RecyclerView
        serviceAdapter = new ServiceAdapter(this, servicesList);
        serviceAdapter.setOnServiceSelectedListener(this);
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        rvServices.setAdapter(serviceAdapter);
        
        // Time slots RecyclerView
        timeSlotAdapter = new TimeSlotGridAdapter(this, timeSlotsList);
        timeSlotAdapter.setOnTimeSlotClickListener(this);
        GridLayoutManager timeSlotLayoutManager = new GridLayoutManager(this, 3);
        rvTimeSlots.setLayoutManager(timeSlotLayoutManager);
        rvTimeSlots.setAdapter(timeSlotAdapter);
        
        // Calendar RecyclerView
        calendarAdapter = new CalendarAdapter(this, calendarDays);
        calendarAdapter.setOnDateClickListener(this);
        GridLayoutManager calendarLayoutManager = new GridLayoutManager(this, 7);
        calendarRecyclerView.setLayoutManager(calendarLayoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }
    
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);
    }
    
    private void setupCalendar() {
        updateCalendar();
    }
    
    private void updateCalendar() {
        tvMonthYear.setText(monthYearFormat.format(currentCalendar.getTime()));
        generateCalendarDays();
        calendarAdapter.notifyDataSetChanged();
    }
    
    private void generateCalendarDays() {
        calendarDays.clear();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentCalendar.getTime());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Add empty days for previous month
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarDays.add(new CalendarDay());
        }
        
        // Add days of current month
        Calendar today = Calendar.getInstance();
        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
            CalendarDay calendarDay = new CalendarDay(day, new Date(cal.getTimeInMillis()));
            
            // Check if day is in the past
            if (cal.before(today)) {
                calendarDay.setEnabled(false);
            }
            
            calendarDays.add(calendarDay);
        }
    }
    
    private void loadDoctorInfo() {
        if (doctorId == null) return;
        
        updateDoctorUI();
    }
    
    private void updateDoctorUI() {
        if (doctorName != null) {
            tvDoctorName.setText(doctorName);
        }
        if (specialty != null) {
            tvSpecialty.setText(specialty);
        }
        if (hospitalName != null) {
            tvHospitalName.setText(hospitalName);
        }
        if (consultationFee != null) {
            tvConsultationFee.setText(consultationFee + " VND");
        }
    }
    
    private void loadServices() {
        showServicesLoading(true);
        
        serviceApiService.getServices(1, 20, null).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                showServicesLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ApiResponse<Object> apiResponse = response.body();
                        
                        if (apiResponse.getData() instanceof List) {
                            List<?> dataList = (List<?>) apiResponse.getData();
                            List<Service> services = new ArrayList<>();
                            
                            Gson gson = new Gson();
                            for (Object item : dataList) {
                                Service service = gson.fromJson(gson.toJson(item), Service.class);
                                services.add(service);
                            }
                            
                            updateServicesList(services);
                        } else {
                            showServicesError("Không thể tải danh sách dịch vụ");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing services", e);
                        showServicesError("Lỗi khi xử lý dữ liệu dịch vụ");
                    }
                } else {
                    showServicesError("Không thể tải danh sách dịch vụ");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Services API call failed", t);
                showServicesLoading(false);
                showServicesError("Lỗi kết nối mạng");
            }
        });
    }
    
    private void loadTimeSlots(CalendarDay selectedDay) {
        if (doctorId == null || selectedDay == null || selectedDay.getDate() == null) {
            return;
        }
        
        showTimeSlotsLoading(true);
        clearTimeSlots();
        
        // Generate mock time slots based on doctor schedule
        // In real app, this would call an API to get doctor's schedule
        generateMockTimeSlots();
        
        showTimeSlotsLoading(false);
        updateTimeSlotsList(timeSlotsList);
    }
    
    private void generateMockTimeSlots() {
        timeSlotsList.clear();
        
        // Morning slots
        timeSlotsList.add(new TimeSlot("08:00", "09:00", true, 2, 5, false));
        timeSlotsList.add(new TimeSlot("09:00", "10:00", true, 3, 5, false));
        timeSlotsList.add(new TimeSlot("10:00", "11:00", true, 1, 5, false));
        timeSlotsList.add(new TimeSlot("11:00", "12:00", false, 5, 5, false)); // Full
        
        // Afternoon slots
        timeSlotsList.add(new TimeSlot("14:00", "15:00", true, 0, 5, false));
        timeSlotsList.add(new TimeSlot("15:00", "16:00", true, 2, 5, false));
        timeSlotsList.add(new TimeSlot("16:00", "17:00", true, 1, 5, false));
        timeSlotsList.add(new TimeSlot("17:00", "18:00", true, 0, 5, false));
    }
    
    private void createAppointment() {
        if (!validateBookingData()) {
            return;
        }
        
        Integer userId = authManager.getUserId();
        if (userId == null) {
            showError("Vui lòng đăng nhập để đặt lịch khám");
            return;
        }
        
        showLoading();
        
        // Create appointment request
        String scheduledTime = formatScheduledTime();
        CreateAppointmentRequest request = new CreateAppointmentRequest(
            doctorId,
            userId,
            selectedService.getServiceId(),
            scheduledTime,
            "Đặt lịch qua ứng dụng",
            "SCHEDULED"
        );
        
        appointmentApiService.createAppointment(request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                hideLoading();
                
                if (response.isSuccessful() && response.body() != null) {
                    showSuccessAndNavigateToPayment(response.body());
                } else {
                    showError("Không thể đặt lịch khám. Vui lòng thử lại.");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Create appointment failed", t);
                hideLoading();
                showError("Lỗi kết nối mạng. Vui lòng thử lại.");
            }
        });
    }
    
    private boolean validateBookingData() {
        if (selectedService == null) {
            showError("Vui lòng chọn dịch vụ khám");
            return false;
        }
        
        if (selectedCalendarDay == null || selectedCalendarDay.getDate() == null) {
            showError("Vui lòng chọn ngày khám");
            return false;
        }
        
        if (selectedTimeSlot == null) {
            showError("Vui lòng chọn khung giờ khám");
            return false;
        }
        
        return true;
    }
    
    private String formatScheduledTime() {
        if (selectedCalendarDay == null || selectedTimeSlot == null) {
            return null;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedCalendarDay.getDate());
        
        String[] timeParts = selectedTimeSlot.getStartTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return isoFormat.format(cal.getTime());
    }
    
    private void showSuccessAndNavigateToPayment(ApiResponse<Object> response) {
        Toast.makeText(this, "Đặt lịch khám thành công!", Toast.LENGTH_LONG).show();
        
        // Navigate to payment
        Intent intent = new Intent(this, PaymentActivity.class);
        
        // Extract appointment ID if available
        Integer appointmentId = extractAppointmentId(response);
        if (appointmentId != null) {
            intent.putExtra("appointmentId", appointmentId);
        }
        
        intent.putExtra("serviceName", selectedService.getName());
        intent.putExtra("servicePrice", selectedService.getPrice());
        intent.putExtra("doctorName", doctorName);
        intent.putExtra("scheduledTime", formatScheduledTime());
        
        startActivity(intent);
        finish();
    }
    
    private Integer extractAppointmentId(ApiResponse<Object> response) {
        try {
            Gson gson = new Gson();
            JsonElement element = gson.toJsonTree(response.getData());
            
            if (element.isJsonObject()) {
                JsonObject data = element.getAsJsonObject();
                if (data.has("appointmentId")) {
                    return data.get("appointmentId").getAsInt();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting appointment ID", e);
        }
        return null;
    }
    
    // Interface implementations
    @Override
    public void onServiceSelected(Service service, int position) {
        selectedService = service;
        updateBookButtonState();
    }
    
    @Override
    public void onTimeSlotClick(TimeSlot timeSlot, int position) {
        selectedTimeSlot = timeSlot;
        updateBookButtonState();
    }
    
    @Override
    public void onDateClick(CalendarDay day, int position) {
        if (day.isEnabled() && day.getDate() != null) {
            // Clear previous selection
            if (selectedCalendarDay != null) {
                selectedCalendarDay.setSelected(false);
            }
            
            selectedCalendarDay = day;
            day.setSelected(true);
            calendarAdapter.notifyDataSetChanged();
            
            // Clear time slot selection
            selectedTimeSlot = null;
            timeSlotAdapter.clearSelection();
            
            // Load time slots for selected date
            loadTimeSlots(day);
            
            updateBookButtonState();
        }
    }
    
    // UI Helper methods
    private void updateBookButtonState() {
        boolean isEnabled = selectedService != null && 
                          selectedCalendarDay != null && 
                          selectedTimeSlot != null;
        btnBookAppointment.setEnabled(isEnabled);
    }
    
    private void showServicesLoading(boolean show) {
        progressServices.setVisibility(show ? View.VISIBLE : View.GONE);
        rvServices.setVisibility(show ? View.GONE : View.VISIBLE);
        tvNoServices.setVisibility(View.GONE);
    }
    
    private void showServicesError(String message) {
        rvServices.setVisibility(View.GONE);
        tvNoServices.setVisibility(View.VISIBLE);
        tvNoServices.setText(message);
    }
    
    private void updateServicesList(List<Service> services) {
        if (services.isEmpty()) {
            showServicesError("Không có dịch vụ khả dụng");
        } else {
            servicesList.clear();
            servicesList.addAll(services);
            serviceAdapter.updateServices(servicesList);
            rvServices.setVisibility(View.VISIBLE);
            tvNoServices.setVisibility(View.GONE);
        }
    }
    
    private void showTimeSlotsLoading(boolean show) {
        progressTimeSlots.setVisibility(show ? View.VISIBLE : View.GONE);
        rvTimeSlots.setVisibility(show ? View.GONE : View.VISIBLE);
        tvNoTimeSlots.setVisibility(View.GONE);
    }
    
    private void clearTimeSlots() {
        timeSlotsList.clear();
        timeSlotAdapter.updateTimeSlots(timeSlotsList);
        tvNoTimeSlots.setText("Chọn ngày để xem các khung giờ khả dụng");
        tvNoTimeSlots.setVisibility(View.VISIBLE);
        rvTimeSlots.setVisibility(View.GONE);
    }
    
    private void updateTimeSlotsList(List<TimeSlot> timeSlots) {
        if (timeSlots.isEmpty()) {
            tvNoTimeSlots.setText("Không có khung giờ khả dụng cho ngày này");
            tvNoTimeSlots.setVisibility(View.VISIBLE);
            rvTimeSlots.setVisibility(View.GONE);
        } else {
            timeSlotAdapter.updateTimeSlots(timeSlots);
            rvTimeSlots.setVisibility(View.VISIBLE);
            tvNoTimeSlots.setVisibility(View.GONE);
        }
    }
    
    private void showLoading() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }
    
    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
