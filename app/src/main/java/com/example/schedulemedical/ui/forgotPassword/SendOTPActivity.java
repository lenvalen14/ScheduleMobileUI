package com.example.schedulemedical.ui.forgotPassword;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schedulemedical.R;

public class SendOTPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_otpactivity);

        ImageView btn_back = findViewById(R.id.ivBack);

        btn_back.setOnClickListener(v -> finish());
    }
}
