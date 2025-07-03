package com.example.schedulemedical.ui.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.login.LoginActivity;
import com.google.android.material.color.DynamicColors;

import androidx.core.splashscreen.SplashScreen;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());

        Log.d("API_LEVEL", "API level: " + Build.VERSION.SDK_INT);


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, 3000);
    }
}
