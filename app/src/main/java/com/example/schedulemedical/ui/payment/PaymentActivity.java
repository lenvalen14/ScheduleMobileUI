package com.example.schedulemedical.ui.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.PaymentApiService;
import com.example.schedulemedical.model.dto.request.VnpayPaymentRequest;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.ui.home.HomeActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "PaymentActivity";
    
    // UI Components
    private ImageView ivBack;
    private TextView tvDoctorName;
    private TextView tvAppointmentDate;
    private TextView tvAppointmentTime;
    private TextView tvConsultationFee;
    private TextView tvTotalAmount;
    private CardView cardVnpay;
    private CardView cardMomo;
    private LinearLayout layoutPaymentMethods;
    private Button btnPay;
    private WebView webViewPayment;
    private LinearLayout layoutPaymentDetails;
    
    // Data
    private Integer appointmentId;
    private String doctorName;
    private String appointmentDate;
    private String appointmentTime;
    private String consultationFee;
    private Double totalAmount;
    private String selectedPaymentMethod = "VNPAY";
    
    // Services
    private PaymentApiService paymentApiService;
    private AuthManager authManager;
    private ProgressDialog progressDialog;
    
    // Payment states
    private boolean isProcessingPayment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        
        initializeServices();
        initializeViews();
        getIntentData();
        setupClickListeners();
        setupProgressDialog();
        updateUI();
    }
    
    private void initializeServices() {
        ApiClient.init(this);
        paymentApiService = ApiClient.getPaymentApiService();
        authManager = new AuthManager(this);
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.ivBack);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvAppointmentDate = findViewById(R.id.tvAppointmentDate);
        tvAppointmentTime = findViewById(R.id.tvAppointmentTime);
        tvConsultationFee = findViewById(R.id.tvConsultationFee);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        cardVnpay = findViewById(R.id.cardVnpay);
        cardMomo = findViewById(R.id.cardMomo);
        layoutPaymentMethods = findViewById(R.id.layoutPaymentMethods);
        btnPay = findViewById(R.id.btnPay);
        webViewPayment = findViewById(R.id.webViewPayment);
        layoutPaymentDetails = findViewById(R.id.layoutPaymentDetails);
    }
    
    private void getIntentData() {
        Intent intent = getIntent();
        appointmentId = intent.getIntExtra("appointmentId", -1);
        doctorName = intent.getStringExtra("doctorName");
        appointmentDate = intent.getStringExtra("appointmentDate");
        appointmentTime = intent.getStringExtra("appointmentTime");
        consultationFee = intent.getStringExtra("consultationFee");
        
        if (appointmentId == -1) {
            Toast.makeText(this, "Thông tin lịch hẹn không hợp lệ", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // Calculate total amount
        try {
            totalAmount = Double.parseDouble(consultationFee.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            totalAmount = 500000.0; // Default fee
        }
    }
    
    private void setupClickListeners() {
        // Back button
        ivBack.setOnClickListener(v -> onBackPressed());
        
        // Payment method selection
        cardVnpay.setOnClickListener(v -> selectPaymentMethod("VNPAY"));
        cardMomo.setOnClickListener(v -> selectPaymentMethod("MOMO"));
        
        // Pay button
        btnPay.setOnClickListener(v -> processPayment());
    }
    
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý thanh toán...");
        progressDialog.setCancelable(false);
    }
    
    private void updateUI() {
        // Update appointment details
        if (tvDoctorName != null) {
            tvDoctorName.setText(doctorName != null ? doctorName : "Bác sĩ");
        }
        
        if (tvAppointmentDate != null) {
            tvAppointmentDate.setText(appointmentDate != null ? appointmentDate : "");
        }
        
        if (tvAppointmentTime != null) {
            tvAppointmentTime.setText(appointmentTime != null ? appointmentTime : "");
        }
        
        // Format consultation fee
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedFee = formatter.format(totalAmount);
        
        if (tvConsultationFee != null) {
            tvConsultationFee.setText(formattedFee);
        }
        
        if (tvTotalAmount != null) {
            tvTotalAmount.setText(formattedFee);
        }
        
        // Set default payment method selection
        selectPaymentMethod("VNPAY");
    }
    
    private void selectPaymentMethod(String method) {
        selectedPaymentMethod = method;
        
        // Update UI selection
        updatePaymentMethodSelection();
        
        Log.d(TAG, "Selected payment method: " + method);
    }
    
    private void updatePaymentMethodSelection() {
        // Reset all cards
        cardVnpay.setCardElevation(4);
        cardMomo.setCardElevation(4);
        cardVnpay.setStrokeWidth(0);
        cardMomo.setStrokeWidth(0);
        
        // Highlight selected card
        if ("VNPAY".equals(selectedPaymentMethod)) {
            cardVnpay.setCardElevation(8);
            cardVnpay.setStrokeWidth(3);
            cardVnpay.setStrokeColor(getResources().getColor(R.color.primary_color));
        } else if ("MOMO".equals(selectedPaymentMethod)) {
            cardMomo.setCardElevation(8);
            cardMomo.setStrokeWidth(3);
            cardMomo.setStrokeColor(getResources().getColor(R.color.primary_color));
        }
    }
    
    private void processPayment() {
        if (isProcessingPayment) {
            return;
        }
        
        if (!authManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        
        isProcessingPayment = true;
        showLoading();
        
        if ("VNPAY".equals(selectedPaymentMethod)) {
            processVnpayPayment();
        } else if ("MOMO".equals(selectedPaymentMethod)) {
            processMomoPayment();
        } else {
            hideLoading();
            isProcessingPayment = false;
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void processVnpayPayment() {
        VnpayPaymentRequest request = new VnpayPaymentRequest();
        request.setAppointmentId(appointmentId);
        request.setAmount(totalAmount);
        request.setDescription("Thanh toán phí khám bệnh - " + doctorName);
        request.setReturnUrl("vnpay://payment/result");
        
        paymentApiService.createVnpayPayment(request)
            .enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    hideLoading();
                    isProcessingPayment = false;
                    
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            Object data = response.body().getData();
                            
                            if (data instanceof Map) {
                                Map<String, Object> paymentData = (Map<String, Object>) data;
                                String paymentUrl = (String) paymentData.get("paymentUrl");
                                
                                if (paymentUrl != null && !paymentUrl.isEmpty()) {
                                    showWebViewPayment(paymentUrl);
                                } else {
                                    showError("Không thể tạo liên kết thanh toán");
                                }
                            } else {
                                showError("Dữ liệu thanh toán không hợp lệ");
                            }
                            
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing VNPay response", e);
                            showError("Lỗi xử lý phản hồi thanh toán");
                        }
                    } else {
                        showError("Không thể tạo thanh toán VNPay");
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    hideLoading();
                    isProcessingPayment = false;
                    Log.e(TAG, "VNPay payment failed", t);
                    showError("Lỗi kết nối: " + t.getMessage());
                }
            });
    }
    
    private void processMomoPayment() {
        // Similar implementation for MoMo
        Toast.makeText(this, "MoMo payment coming soon!", Toast.LENGTH_SHORT).show();
        hideLoading();
        isProcessingPayment = false;
    }
    
    private void showWebViewPayment(String paymentUrl) {
        // Hide payment details and show WebView
        layoutPaymentDetails.setVisibility(View.GONE);
        webViewPayment.setVisibility(View.VISIBLE);
        
        // Configure WebView
        webViewPayment.getSettings().setJavaScriptEnabled(true);
        webViewPayment.getSettings().setDomStorageEnabled(true);
        
        webViewPayment.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "WebView URL: " + url);
                
                if (url.startsWith("vnpay://payment/result")) {
                    // Payment completed, handle result
                    handlePaymentResult(url);
                    return true;
                } else if (url.contains("vnp_ResponseCode")) {
                    // VNPay callback URL
                    handleVnpayCallback(url);
                    return true;
                }
                
                return false;
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page finished loading: " + url);
            }
        });
        
        // Load payment URL
        webViewPayment.loadUrl(paymentUrl);
    }
    
    private void handlePaymentResult(String url) {
        Uri uri = Uri.parse(url);
        String responseCode = uri.getQueryParameter("vnp_ResponseCode");
        
        if ("00".equals(responseCode)) {
            // Payment successful
            showPaymentSuccess();
        } else {
            // Payment failed
            showPaymentFailed("Thanh toán thất bại. Mã lỗi: " + responseCode);
        }
    }
    
    private void handleVnpayCallback(String url) {
        Uri uri = Uri.parse(url);
        String responseCode = uri.getQueryParameter("vnp_ResponseCode");
        
        if ("00".equals(responseCode)) {
            showPaymentSuccess();
        } else {
            showPaymentFailed("Thanh toán thất bại");
        }
    }
    
    private void showPaymentSuccess() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
            
            // Navigate to success screen or home
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
    
    private void showPaymentFailed(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            
            // Show payment details again
            webViewPayment.setVisibility(View.GONE);
            layoutPaymentDetails.setVisibility(View.VISIBLE);
            
            isProcessingPayment = false;
        });
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
    
    @Override
    public void onBackPressed() {
        if (webViewPayment.getVisibility() == View.VISIBLE) {
            // If WebView is visible, go back to payment details
            webViewPayment.setVisibility(View.GONE);
            layoutPaymentDetails.setVisibility(View.VISIBLE);
            isProcessingPayment = false;
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
} 