package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        setContentView(R.layout.activity_main);
            sessionManager = new SessionManager(this);
        checkLoginStatus();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
    }
    }

    private void checkLoginStatus() {
        try {
            if (!sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                // Điều hướng dựa trên vai trò người dùng
                if (sessionManager.isAdmin()) {
                    startActivity(new Intent(this, AdminActivity.class));
                } else if (sessionManager.isStudent()) {
                    startActivity(new Intent(this, ScheduleActivity.class));
                } else {
                    // Vai trò không xác định, logout và yêu cầu đăng nhập lại
                    sessionManager.logout();
                    startActivity(new Intent(this, LoginActivity.class));
                }
            }
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error in checkLoginStatus: " + e.getMessage());
            e.printStackTrace();
        }
    }
}