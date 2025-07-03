package com.example.schedulemedical.ui.forgotPassword;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schedulemedical.R;
import com.example.schedulemedical.utils.NavigationHelper;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_code);
        setupNavigation();
//        setupForgotPasswordControls();
    }

    private void setupNavigation() {
        // Back button
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(view -> {
                NavigationHelper.goBack(this);
            });
        }
    }

//    private void setupForgotPasswordControls() {
//        EditText emailEdt = findViewById(R.id.editTextEmail);
//        Button sendOTPButton = findViewById(R.id.btnSendOTP);
//        TextView loginLink = findViewById(R.id.tvBackToLogin);
//
//        if (sendOTPButton != null && emailEdt != null) {
//            sendOTPButton.setOnClickListener(view -> {
//                String email = emailEdt.getText().toString().trim();
//                if (validateEmail(email)) {
//                    // TODO: Send OTP to email
//                    Toast.makeText(this, "OTP sent to " + email, Toast.LENGTH_SHORT).show();
//                    NavigationHelper.navigateToSendOTP(this, email);
//                }
//            });
//        }
//
//        if (loginLink != null) {
//            loginLink.setOnClickListener(view -> {
//                NavigationHelper.navigateToLogin(this);
//            });
//        }
//    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}