package com.example.schedulemedical.ui.forgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.schedulemedical.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.AuthApiService;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class SendOTPActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otpactivity);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        TextInputLayout tilEmail = findViewById(R.id.tilEmail);
        TextInputEditText etEmail = findViewById(R.id.etEmail);
        MaterialButton btnSendCode = findViewById(R.id.btnSendCode);

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
                tilEmail.setError(null);
                if (TextUtils.isEmpty(email)) {
                    tilEmail.setError("Vui lòng nhập email");
                    return;
                }
                // Gọi API gửi OTP
                AuthApiService authApiService = ApiClient.getAuthApiService();
                Map<String, String> body = new HashMap<>();
                body.put("email", email);
                authApiService.sendOTP(body).enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(SendOTPActivity.this, "Đã gửi mã xác thực!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SendOTPActivity.this, VerifyCodeActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SendOTPActivity.this, "Gửi mã thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(SendOTPActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
