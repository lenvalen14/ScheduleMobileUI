package com.example.schedulemedical.ui.booking;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.TimeSlotAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.AppointmentApiService;
import com.example.schedulemedical.data.api.DoctorApiService;
import com.example.schedulemedical.model.dto.request.CreateAppointmentRequest;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.ui.payment.PaymentActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity implements TimeSlotAdapter.OnTimeSlotClickListener {
    private static final String TAG = "BookingActivity";
    
    // UI Components
    private ImageView ivBack;
    private TextView tvDoctorName;
    private TextView tvSpecialty;
    private TextView tvHospitalName;
    private TextView tvConsultationFee;
    private MaterialCardView cardSelectDate;
    private TextView tvSelectedDate;
    private RecyclerView rvTimeSlots;
    private TextView tvNoTimeSlots;
    private Button btnBookAppointment;
    
    // Data
    private Integer doctorId;
    private String doctorName;
    private String specialty;
    private String hospitalName;
    private String consultationFee;
    private Calendar selectedDate;
    private String selectedTimeSlot;
    private List<String> availableTimeSlots;
    private TimeSlotAdapter timeSlotAdapter;
    
    // Services
    private DoctorApiService doctorApiService;
    private AppointmentApiService appointmentApiService;
    private AuthManager authManager;
    private ProgressDialog progressDialog;
    
    // Date formatters
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        
        initializeServices();
        initializeViews();
        getIntentData();
        setupClickListeners();
        setupRecyclerView();
        setupProgressDialog();
        updateUI();
    }
    
    private void initializeServices() {
        ApiClient.init(this);
        doctorApiService = ApiClient.getDoctorApiService();
        appointmentApiService = ApiClient.getAppointmentApiService();
        authManager = new AuthManager(this);
        availableTimeSlots = new ArrayList<>();
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.ivBack);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvConsultationFee = findViewById(R.id.tvConsultationFee);
        cardSelectDate = findViewById(R.id.cardSelectDate);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        rvTimeSlots = findViewById(R.id.rvTimeSlots);
        tvNoTimeSlots = findViewById(R.id.tvNoTimeSlots);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);
        
        // Initialize with today's date
        selectedDate = Calendar.getInstance();
    }
    
    private void getIntentData() {
        Intent intent = getIntent();
        doctorId = intent.getIntExtra("doctorId", -1);
        doctorName = intent.getStringExtra("doctorName");
        specialty = intent.getStringExtra("specialty");
        hospitalName = intent.getStringExtra("hospitalName");
        consultationFee = intent.getStringExtra("consultationFee");
        
        if (doctorId == -1) {
            Toast.makeText(this, "Thông tin bác sĩ không hợp lệ", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void setupClickListeners() {
        // Back button
        ivBack.setOnClickListener(v -> finish());
        
        // Date selector
        cardSelectDate.setOnClickListener(v -> showDatePicker());
        
        // Book appointment button
        btnBookAppointment.setOnClickListener(v -> bookAppointment());
    }
    
    private void setupRecyclerView() {
        timeSlotAdapter = new TimeSlotAdapter(this, availableTimeSlots);
        timeSlotAdapter.setOnTimeSlotClickListener(this);
        
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvTimeSlots.setLayoutManager(gridLayoutManager);
        rvTimeSlots.setAdapter(timeSlotAdapter);
    }
    
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);
    }
    
    private void updateUI() {
        // Update doctor info
        if (tvDoctorName != null) {
            tvDoctorName.setText(doctorName != null ? doctorName : "Bác sĩ");
        }
        
        if (tvSpecialty != null) {
            tvSpecialty.setText(specialty != null ? specialty : "Chuyên khoa");
        }
        
        if (tvHospitalName != null) {
            tvHospitalName.setText(hospitalName != null ? hospitalName : "Bệnh viện");
        }
        
        if (tvConsultationFee != null) {
            tvConsultationFee.setText(consultationFee != null ? consultationFee + " VND" : "Phí khám");
        }
        
        // Update selected date
        updateSelectedDateDisplay();
        
        // Load available time slots for today
        loadAvailableTimeSlots();
        
        // Initially disable book button
        updateBookButtonState();
    }
    
    private void updateSelectedDateDisplay() {
        if (tvSelectedDate != null && selectedDate != null) {
            tvSelectedDate.setText(displayDateFormat.format(selectedDate.getTime()));
        }
    }
    
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                updateSelectedDateDisplay();
                loadAvailableTimeSlots();
                
                // Clear selected time slot when date changes
                selectedTimeSlot = null;
                timeSlotAdapter.clearSelection();
                updateBookButtonState();
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        
        // Set maximum date to 30 days from now
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        
        datePickerDialog.show();
    }
    
    private void loadAvailableTimeSlots() {
        if (doctorId == null || selectedDate == null) {
            return;
        }
        
        showLoading();
        
        String dateString = apiDateFormat.format(selectedDate.getTime());
        
        doctorApiService.getDoctorSchedule(doctorId, dateString)
            .enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    hideLoading();
                    
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            Object data = response.body().getData();
                            Gson gson = new Gson();
                            String jsonString = gson.toJson(data);
                            
                            Type listType = new TypeToken<List<String>>(){}.getType();
                            List<String> timeSlots = gson.fromJson(jsonString, listType);
                            
                            if (timeSlots != null && !timeSlots.isEmpty()) {
                                availableTimeSlots.clear();
                                availableTimeSlots.addAll(timeSlots);
                                timeSlotAdapter.updateTimeSlots(availableTimeSlots);
                                showTimeSlots();
                            } else {
                                showNoTimeSlots();
                            }
                            
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing time slots", e);
                            showNoTimeSlots();
                        }
                    } else {
                        Log.e(TAG, "Failed to load time slots: " + response.code());
                        showNoTimeSlots();
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    hideLoading();
                    Log.e(TAG, "Network error loading time slots", t);
                    showNoTimeSlots();
                }
            });
    }
    
    private void showTimeSlots() {
        if (rvTimeSlots != null) {
            rvTimeSlots.setVisibility(View.VISIBLE);
        }
        if (tvNoTimeSlots != null) {
            tvNoTimeSlots.setVisibility(View.GONE);
        }
    }
    
    private void showNoTimeSlots() {
        if (rvTimeSlots != null) {
            rvTimeSlots.setVisibility(View.GONE);
        }
        if (tvNoTimeSlots != null) {
            tvNoTimeSlots.setVisibility(View.VISIBLE);
            tvNoTimeSlots.setText("Không có lịch trống cho ngày này");
        }
    }
    
    private void bookAppointment() {
        if (!authManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt lịch", Toast.LENGTH_LONG).show();
            return;
        }
        
        if (selectedTimeSlot == null || selectedTimeSlot.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn khung giờ", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showLoading();
        
        CreateAppointmentRequest request = new CreateAppointmentRequest();
        request.setDoctorId(doctorId);
        request.setPatientId(authManager.getUserId());
        request.setAppointmentDate(apiDateFormat.format(selectedDate.getTime()));
        request.setAppointmentTime(selectedTimeSlot);
        request.setReason("Khám tổng quát"); // Default reason
        request.setStatus("PENDING");
        
        appointmentApiService.createAppointment(request)
            .enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    hideLoading();
                    
                    if (response.isSuccessful() && response.body() != null) {
                        // Appointment created successfully
                        showSuccessAndNavigateToPayment(response.body());
                    } else {
                        String errorMsg = "Không thể đặt lịch hẹn";
                        if (response.code() == 409) {
                            errorMsg = "Lịch hẹn đã được đặt bởi người khác";
                        }
                        Toast.makeText(BookingActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    hideLoading();
                    Log.e(TAG, "Failed to create appointment", t);
                    Toast.makeText(BookingActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }
    
    private void showSuccessAndNavigateToPayment(ApiResponse<Object> response) {
        Toast.makeText(this, "Đặt lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
        
        // Navigate to payment
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("appointmentId", extractAppointmentId(response));
        intent.putExtra("doctorName", doctorName);
        intent.putExtra("appointmentDate", displayDateFormat.format(selectedDate.getTime()));
        intent.putExtra("appointmentTime", selectedTimeSlot);
        intent.putExtra("consultationFee", consultationFee);
        startActivity(intent);
        
        // Close booking activity
        finish();
    }
    
    private Integer extractAppointmentId(ApiResponse<Object> response) {
        try {
            // Try to extract appointment ID from response
            Object data = response.getData();
            if (data instanceof Number) {
                return ((Number) data).intValue();
            }
            // Could be more complex extraction based on actual API response
            return 1; // Fallback ID
        } catch (Exception e) {
            Log.e(TAG, "Error extracting appointment ID", e);
            return 1; // Fallback ID
        }
    }
    
    private void updateBookButtonState() {
        if (btnBookAppointment != null) {
            boolean canBook = selectedTimeSlot != null && !selectedTimeSlot.isEmpty();
            btnBookAppointment.setEnabled(canBook);
            btnBookAppointment.setAlpha(canBook ? 1.0f : 0.5f);
        }
    }
    
    private void showLoading() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    
    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    
    // TimeSlotAdapter.OnTimeSlotClickListener implementation
    @Override
    public void onTimeSlotClick(String timeSlot) {
        selectedTimeSlot = timeSlot;
        updateBookButtonState();
        Log.d(TAG, "Selected time slot: " + timeSlot);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
} 