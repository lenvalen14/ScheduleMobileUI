package com.example.schedulemedical.ui.forgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.schedulemedical.R;
import com.google.android.material.button.MaterialButton;
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

public class VerifyCodeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        String email = getIntent().getStringExtra("email");
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        EditText etOtp1 = findViewById(R.id.etOtp1);
        EditText etOtp2 = findViewById(R.id.etOtp2);
        EditText etOtp3 = findViewById(R.id.etOtp3);
        EditText etOtp4 = findViewById(R.id.etOtp4);
        EditText etOtp5 = findViewById(R.id.etOtp5);
        EditText etOtp6 = findViewById(R.id.etOtp6);
        MaterialButton btnVerify = findViewById(R.id.btnVerify);

        setupOtpInputs(etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etOtp1.getText().toString().trim() +
                        etOtp2.getText().toString().trim() +
                        etOtp3.getText().toString().trim() +
                        etOtp4.getText().toString().trim() +
                        etOtp5.getText().toString().trim() +
                        etOtp6.getText().toString().trim();
                if (TextUtils.isEmpty(code) || code.length() < 6) {
                    Toast.makeText(VerifyCodeActivity.this, "Vui lòng nhập đủ 6 số mã xác thực", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Gọi API xác thực OTP
                AuthApiService authApiService = ApiClient.getAuthApiService();
                Map<String, String> body = new HashMap<>();
                body.put("email", email);
                body.put("code", code);
                authApiService.verifyOTP(body).enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(VerifyCodeActivity.this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(VerifyCodeActivity.this, ForgotPasswordResetActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        } else {
                            Toast.makeText(VerifyCodeActivity.this, "Xác thực thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(VerifyCodeActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setupOtpInputs(EditText... otps) {
        for (int i = 0; i < otps.length; i++) {
            final int index = i;
            otps[i].addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
                @Override
                public void afterTextChanged(android.text.Editable s) {
                    if (s.length() == 1 && index < otps.length - 1) {
                        otps[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        otps[index - 1].requestFocus();
                    }
                }
            });
            otps[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && otps[index].getText().toString().isEmpty() && index > 0) {
                    otps[index - 1].requestFocus();
                    return true;
                }
                return false;
            });
        }
        // Tự động focus vào ô đầu tiên khi vào màn hình
        if (otps.length > 0) otps[0].requestFocus();
    }
}