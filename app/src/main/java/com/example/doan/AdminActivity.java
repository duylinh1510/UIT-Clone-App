package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";

    private TextView txtWelcome;
    private Button btnManageStudents;
    private Button btnManageTeachers;
    private Button btnManageDepartments;
    private Button btnManageSubjects;
    private Button btnManageGrades;
    private Button btnManageSchedule;
    private Button btnLogout;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initViews();
        initData();
        setupClickListeners();
    }

    private void initViews() {
        txtWelcome = findViewById(R.id.txtWelcome);
        btnManageStudents = findViewById(R.id.btnManageStudents);
        btnManageTeachers = findViewById(R.id.btnManageTeachers);
        btnManageDepartments = findViewById(R.id.btnManageDepartments);
        btnManageSubjects = findViewById(R.id.btnManageSubjects);
        btnManageGrades = findViewById(R.id.btnManageGrades);
        btnManageSchedule = findViewById(R.id.btnManageSchedule);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void initData() {
        sessionManager = new SessionManager(this);
        
        // Kiểm tra quyền admin
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền truy cập!", Toast.LENGTH_LONG).show();
            logout();
            return;
        }

        // Hiển thị thông tin chào mừng
        String username = sessionManager.getUsername();
        txtWelcome.setText("Chào mừng Admin: " + username);
    }

    private void setupClickListeners() {
        btnManageStudents.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminStudentsActivity.class);
            startActivity(intent);
        });

        btnManageTeachers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminTeachersActivity.class);
            startActivity(intent);
        });

        btnManageDepartments.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminDepartmentsActivity.class);
            startActivity(intent);
        });

        btnManageSubjects.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminSubjectsActivity.class);
            startActivity(intent);
        });

        btnManageGrades.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminGradesActivity.class);
            startActivity(intent);
        });

        btnManageSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminTimetablesActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Không cho phép quay lại, chỉ có thể đăng xuất
        Toast.makeText(this, "Nhấn nút Đăng xuất để thoát", Toast.LENGTH_SHORT).show();
    }
} 