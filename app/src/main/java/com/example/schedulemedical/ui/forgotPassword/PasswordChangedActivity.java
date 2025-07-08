package com.example.schedulemedical.ui.forgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.login.LoginActivity;
import com.google.android.material.button.MaterialButton;

public class PasswordChangedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_changed);

        MaterialButton btnGoToLogin = findViewById(R.id.btnBackToLogin);
        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });

        // Tự động chuyển về login sau 3 giây
        new Handler().postDelayed(this::goToLogin, 3000);
    }

    private void goToLogin() {
        Intent intent = new Intent(PasswordChangedActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
} 