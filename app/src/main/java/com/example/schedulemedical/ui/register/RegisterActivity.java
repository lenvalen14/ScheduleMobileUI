package com.example.schedulemedical.ui.register;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;

import com.example.schedulemedical.R;
import com.example.schedulemedical.utils.NavigationHelper;

public class RegisterActivity extends ComponentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText nameEdt = findViewById(R.id.editTextName);
        EditText emailEdt = findViewById(R.id.editTextEmail);
        EditText passwordEdt = findViewById(R.id.editTextPassword);
        Button registerBtn = findViewById(R.id.btnRegister);

        // Xử lý đăng ký
        registerBtn.setOnClickListener(view -> {
            String name = nameEdt.getText().toString();
            String email = emailEdt.getText().toString();
            String password = passwordEdt.getText().toString();

//            if (validateInput(name, email, password, confirmPassword)) {
//                // TODO: Implement registration logic
//                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
//                NavigationHelper.navigateToLogin(this);
//            }
        });
    }

    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
