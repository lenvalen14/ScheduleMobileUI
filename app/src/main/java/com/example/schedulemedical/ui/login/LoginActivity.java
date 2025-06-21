package com.example.schedulemedical.ui.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.schedulemedical.R;

public class LoginActivity extends ComponentActivity {
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText emailEdt = findViewById(R.id.editTextEmail);
        EditText passwordEdt = findViewById(R.id.editTextPassword);
        Button loginBtn = findViewById(R.id.btnLogin);

        TextView forgotpass = findViewById(R.id.tvForgotPassword);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginBtn.setOnClickListener(view -> {
            String email = emailEdt.getText().toString();
            String password = passwordEdt.getText().toString();
            loginViewModel.login(email, password);
        });

        loginViewModel.getLoginResult().observe(this, result -> {
            if (result.getData() != null) {
                Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
