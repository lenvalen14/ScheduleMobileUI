package com.example.schedulemedical.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;

import com.example.schedulemedical.R;
import com.example.schedulemedical.utils.AuthManager;
import com.example.schedulemedical.utils.NavigationHelper;
import com.example.schedulemedical.data.api.ApiClient;

public class LoginActivity extends ComponentActivity {
    private AuthManager authManager;
    private ProgressDialog progressDialog;
    
    private EditText emailEdt;
    private EditText passwordEdt;
    private Button loginBtn;
    private TextView forgotpass;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialize API client
        ApiClient.init(this);
        
        // Initialize AuthManager
        authManager = new AuthManager(this);
        
        // Check if user is already logged in
        if (authManager.isLoggedIn()) {
            navigateToHome();
            return;
        }
        
        initViews();
        setupClickListeners();
        setupProgressDialog();
    }
    
    private void initViews() {
        emailEdt = findViewById(R.id.editTextEmail);
        passwordEdt = findViewById(R.id.editTextPassword);
        loginBtn = findViewById(R.id.btnLogin);
        forgotpass = findViewById(R.id.tvForgotPassword);
        registerLink = findViewById(R.id.tvSignUp);
    }
    
    private void setupClickListeners() {
        loginBtn.setOnClickListener(view -> {
            performLogin();
        });

        forgotpass.setOnClickListener(view -> {
            NavigationHelper.navigateToForgotPassword(this);
        });

        if (registerLink != null) {
            registerLink.setOnClickListener(view -> {
                NavigationHelper.navigateToRegister(this);
            });
        }
    }
    
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.setCancelable(false);
    }
    
    private void performLogin() {
        String email = emailEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();
        
        // Validate input
        if (!validateInput(email, password)) {
            return;
        }
        
        // Show loading
        progressDialog.show();
        loginBtn.setEnabled(false);
        
        // Perform login
        authManager.login(email, password, new AuthManager.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    loginBtn.setEnabled(true);
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    navigateToHome();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    loginBtn.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailEdt.setError("Email không được để trống");
            emailEdt.requestFocus();
            return false;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdt.setError("Email không hợp lệ");
            emailEdt.requestFocus();
            return false;
        }
        
        if (password.isEmpty()) {
            passwordEdt.setError("Mật khẩu không được để trống");
            passwordEdt.requestFocus();
            return false;
        }
        
        if (password.length() < 6) {
            passwordEdt.setError("Mật khẩu phải có ít nhất 6 ký tự");
            passwordEdt.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void navigateToHome() {
        Intent intent = new Intent(this, com.example.schedulemedical.ui.home.HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
