package com.example.schedulemedical.ui.forgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.UserApiService;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordResetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_reset);

        String email = getIntent().getStringExtra("email");
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextInputEditText etNewPassword = findViewById(R.id.etNewPassword);
        TextInputEditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        MaterialButton btnConfirm = findViewById(R.id.btnConfirm);
        TextInputLayout tilNewPassword = findViewById(R.id.tilNewPassword);
        TextInputLayout tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString() : "";
                String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";
                tilNewPassword.setError(null);
                tilConfirmPassword.setError(null);
                if (TextUtils.isEmpty(newPassword)) {
                    tilNewPassword.setError("Vui lòng nhập mật khẩu mới");
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    tilConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    tilConfirmPassword.setError("Mật khẩu xác nhận không khớp");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotPasswordResetActivity.this, "Thiếu email xác thực!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Gọi API đặt lại mật khẩu
                UserApiService userApiService = ApiClient.getUserApiService();
                Map<String, String> body = new HashMap<>();
                body.put("email", email);
                body.put("newPassword", newPassword);
                userApiService.forgotPassword(body).enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ForgotPasswordResetActivity.this, "Đặt lại mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPasswordResetActivity.this, PasswordChangedActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ForgotPasswordResetActivity.this, "Đặt lại mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(ForgotPasswordResetActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
} 