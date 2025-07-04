package com.example.schedulemedical.ui.booking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.AppointmentApiService;
import com.example.schedulemedical.model.dto.request.CreateAppointmentRequest;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.utils.AuthManager;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingWizardActivity extends AppCompatActivity {
    private static final String TAG = "BookingWizardActivity";
    
    // UI Components
    private ImageView btnBack;
    private TextView tvStepTitle, tvStepCounter;
    private ViewPager2 viewPager;
    private MaterialButton btnNext, btnPrevious;
    private View progressBar1, progressBar2, progressBar3, progressBar4;
    
    // Wizard Components
    private BookingWizardAdapter wizardAdapter;
    private int currentStep = 0;
    private final int TOTAL_STEPS = 4;
    
    // Services
    private AppointmentApiService appointmentApiService;
    private AuthManager authManager;
    private ProgressDialog progressDialog;
    
    // Booking Data
    public static class BookingData implements java.io.Serializable {
        public Integer specialtyId;
        public String specialtyName;
        public Integer doctorId;
        public String doctorName;
        public String doctorSpecialty;
        public String hospitalName;
        public Date selectedDate;
        public String selectedTimeSlot;
        public Integer serviceId;
        public String serviceName;
        public Double servicePrice;
        public String scheduledDateTime;
        
        public boolean isStepCompleted(int step) {
            boolean result;
            switch (step) {
                case 0: result = specialtyId != null; break;
                case 1: result = doctorId != null; break;
                case 2: result = selectedDate != null && selectedTimeSlot != null; break;
                case 3: result = serviceId != null; break;
                default: result = false; break;
            }
            Log.d("BookingData", "Step " + step + " completed: " + result + " (serviceId: " + serviceId + ")");
            return result;
        }
    }
    
    private BookingData bookingData = new BookingData();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_wizard);
        
        initializeServices();
        initializeViews();
        setupClickListeners();
        setupViewPager();
        setupProgressDialog();
        updateStepUI();
    }
    
    private void initializeServices() {
        ApiClient.init(this);
        appointmentApiService = ApiClient.getAppointmentApiService();
        authManager = new AuthManager(this);
    }
    
    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvStepTitle = findViewById(R.id.tvStepTitle);
        tvStepCounter = findViewById(R.id.tvStepCounter);
        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        
        // Progress indicators
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar4 = findViewById(R.id.progressBar4);
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnPrevious.setOnClickListener(v -> {
            if (currentStep > 0) {
                currentStep--;
                viewPager.setCurrentItem(currentStep, true);
                updateStepUI();
            }
        });
        
        btnNext.setOnClickListener(v -> {
            Log.d(TAG, "btnNext clicked - currentStep: " + currentStep + ", TOTAL_STEPS: " + TOTAL_STEPS);
            
            if (currentStep < TOTAL_STEPS - 1) {
                boolean stepCompleted = bookingData.isStepCompleted(currentStep);
                Log.d(TAG, "Checking if step " + currentStep + " is completed: " + stepCompleted);
                
                if (stepCompleted) {
                    Log.d(TAG, "Step completed, advancing from " + currentStep + " to " + (currentStep + 1));
                    currentStep++;
                    viewPager.setCurrentItem(currentStep, true);
                    updateStepUI();
                    Log.d(TAG, "ViewPager moved to position: " + currentStep);
                } else {
                    Log.w(TAG, "Step not completed, showing error message");
                    showError("Vui lòng hoàn thành bước hiện tại trước khi tiếp tục");
                }
            } else {
                Log.d(TAG, "Final step - creating appointment");
                // Final step - create appointment
                createAppointment();
            }
        });
    }
    
    private void setupViewPager() {
        wizardAdapter = new BookingWizardAdapter(this, bookingData);
        viewPager.setAdapter(wizardAdapter);
        viewPager.setUserInputEnabled(false); // Disable swipe navigation
        
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "ViewPager onPageSelected: " + position + " (was: " + currentStep + ")");
                currentStep = position;
                updateStepUI();
                Log.d(TAG, "Updated currentStep to: " + currentStep);
            }
        });
    }
    
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);
    }
    
    private void updateStepUI() {
        // Update step counter
        tvStepCounter.setText("Bước " + (currentStep + 1) + " / " + TOTAL_STEPS);
        
        // Update step title
        String[] stepTitles = {
            "Chọn chuyên khoa",
            "Chọn bác sĩ",
            "Chọn lịch khám",
            "Chọn dịch vụ"
        };
        tvStepTitle.setText(stepTitles[currentStep]);
        
        // Update progress indicators
        updateProgressIndicators();
        
        // Update navigation buttons
        btnPrevious.setVisibility(currentStep > 0 ? View.VISIBLE : View.GONE);
        
        boolean stepCompleted = bookingData.isStepCompleted(currentStep);
        if (currentStep == TOTAL_STEPS - 1) {
            btnNext.setText("Đặt lịch ngay");
            btnNext.setEnabled(stepCompleted);
            Log.d(TAG, "Final step - button enabled: " + stepCompleted);
        } else {
            btnNext.setText("Tiếp tục");
            btnNext.setEnabled(stepCompleted);
            Log.d(TAG, "Step " + currentStep + " - button enabled: " + stepCompleted);
        }
    }
    
    private void updateProgressIndicators() {
        View[] indicators = {progressBar1, progressBar2, progressBar3, progressBar4};
        
        for (int i = 0; i < indicators.length; i++) {
            if (i <= currentStep) {
                indicators[i].setBackgroundResource(R.drawable.progress_active);
            } else {
                indicators[i].setBackgroundResource(R.drawable.progress_inactive);
            }
        }
    }
    
    public void onStepDataChanged() {
        Log.d(TAG, "onStepDataChanged() called - currentStep: " + currentStep);
        logBookingDataState();
        updateStepUI();
        
        // Auto-advance if step is completed
        if (currentStep < TOTAL_STEPS - 1 && bookingData.isStepCompleted(currentStep)) {
            Log.d(TAG, "Step " + currentStep + " completed, auto-advancing");
            // Small delay for better UX
            btnNext.postDelayed(() -> {
                if (bookingData.isStepCompleted(currentStep)) {
                    btnNext.performClick();
                }
            }, 500);
        } else {
            Log.d(TAG, "Step " + currentStep + " not completed or is final step");
        }
    }
    
    private void logBookingDataState() {
        Log.d(TAG, "=== BookingData State ===");
        Log.d(TAG, "specialtyId: " + bookingData.specialtyId);
        Log.d(TAG, "doctorId: " + bookingData.doctorId);
        Log.d(TAG, "selectedDate: " + bookingData.selectedDate);
        Log.d(TAG, "selectedTimeSlot: " + bookingData.selectedTimeSlot);
        Log.d(TAG, "serviceId: " + bookingData.serviceId);
        Log.d(TAG, "serviceName: " + bookingData.serviceName);
        Log.d(TAG, "========================");
    }
    
    private void createAppointment() {
        if (!validateAllSteps()) {
            return;
        }
        
        Integer userId = authManager.getUserId();
        if (userId == null) {
            showError("Vui lòng đăng nhập để đặt lịch khám");
            return;
        }
        
        showLoading();
        
        // Format scheduled time
        String scheduledTime = formatScheduledTime();
        
        CreateAppointmentRequest request = new CreateAppointmentRequest(
            bookingData.doctorId,
            userId,
            bookingData.serviceId,
            scheduledTime,
            "Đặt lịch qua ứng dụng",
            "SCHEDULED"
        );
        
        appointmentApiService.createAppointment(request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                hideLoading();
                
                if (response.isSuccessful() && response.body() != null) {
                    showSuccessAndFinish(response.body());
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
    
    private boolean validateAllSteps() {
        for (int i = 0; i < TOTAL_STEPS; i++) {
            if (!bookingData.isStepCompleted(i)) {
                showError("Vui lòng hoàn thành tất cả các bước");
                return false;
            }
        }
        return true;
    }
    
    private String formatScheduledTime() {
        if (bookingData.selectedDate == null || bookingData.selectedTimeSlot == null) {
            return null;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(bookingData.selectedDate);
        
        // Parse time slot (format: "HH:mm - HH:mm")
        String[] timeParts = bookingData.selectedTimeSlot.split(" - ")[0].split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        return isoFormat.format(cal.getTime());
    }
    
    private void showSuccessAndFinish(ApiResponse<Object> response) {
        Toast.makeText(this, "Đặt lịch khám thành công!", Toast.LENGTH_LONG).show();
        
        // You can navigate to appointments list or show success dialog
        Intent resultIntent = new Intent();
        resultIntent.putExtra("appointmentCreated", true);
        resultIntent.putExtra("appointmentId", extractAppointmentId(response));
        setResult(RESULT_OK, resultIntent);
        
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
    
    // Helper methods
    public BookingData getBookingData() {
        return bookingData;
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