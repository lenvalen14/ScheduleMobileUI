package com.example.schedulemedical.ui.forgotPassword;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.UserApiService;
import com.example.schedulemedical.model.dto.request.UpdatePasswordRequest;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.utils.AuthManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextInputEditText etNewPassword = findViewById(R.id.etNewPassword);
        TextInputEditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        MaterialButton btnConfirm = findViewById(R.id.btnConfirm);
        TextInputLayout tilNewPassword = findViewById(R.id.tilNewPassword);
        TextInputLayout tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        TextInputEditText etCurrentPassword = findViewById(R.id.etCurrentPassword);
        TextInputLayout tilCurrentPassword = findViewById(R.id.tilCurrentPassword);

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
                String currentPassword = etCurrentPassword.getText() != null ? etCurrentPassword.getText().toString() : "";
                tilCurrentPassword.setError(null);
                if (TextUtils.isEmpty(currentPassword)) {
                    tilCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
                    return;
                }
                //Gọi API đổi mật khẩu
                AuthManager authManager = new AuthManager(ResetPasswordActivity.this);
                String email = authManager.getUserEmail();
                UpdatePasswordRequest req = new UpdatePasswordRequest(currentPassword, newPassword);
                UserApiService userApiService = ApiClient.getUserApiService();
                userApiService.updateUserPassword(email, req).enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        Log.d("API_RESPONSE", "Code: " + response.code());
                        Log.d("API_RESPONSE", "Success: " + response.isSuccessful());

                        if (response.isSuccessful()) {
                            Log.d("API_RESPONSE", "Body: " + new Gson().toJson(response.body()));
                            Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String errorMsg = "Unknown error";
                            try {
                                if (response.errorBody() != null) {
                                    errorMsg = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.e("API_ERROR", "Error: " + errorMsg);
                            Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Log.e("API_FAILURE", "Throwable: " + t.getMessage(), t);
                        Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
} 