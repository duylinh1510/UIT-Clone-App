package com.example.doan;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initData();
        checkIfLoggedIn();
        setupClickListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initData() {
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);
    }

    private void checkIfLoggedIn() {
        // Nếu đã đăng nhập, chuyển đến Activity phù hợp với role
        if (sessionManager.isLoggedIn()) {
            Log.d(TAG, "User already logged in, redirecting based on role");
            redirectToAppropriateActivity();
        }
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (username.isEmpty()) {
            etUsername.setError("Vui lòng nhập tên đăng nhập");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // Hiển thị loading
        showLoading(true);

        // Tạo LoginRequest
        LoginRequest loginRequest = new LoginRequest(username, password);
        Log.d(TAG, "Login request: " + loginRequest.toString());

        // Gọi API login
        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showLoading(false);
                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Login response success: " + loginResponse.isSuccess());
                    Log.d(TAG, "Login response message: " + loginResponse.getMessage());

                    if (loginResponse.isSuccess()) {
                        // Đăng nhập thành công
                        LoginResponse.LoginData loginData = loginResponse.getData();
                        if (loginData != null) {
                            Log.d(TAG, "Login data received: " + loginData.toString());
                            
                            String role = loginData.getRole();
                            Log.d(TAG, "User role: " + role);
                            
                            if ("admin".equals(role)) {
                                // Lưu session cho admin
                                sessionManager.createAdminSession(
                                    loginData.getUserId(),
                                    loginData.getUsername(),
                                    loginData.getRole()
                                );
                                
                                Toast.makeText(LoginActivity.this,
                                    "Đăng nhập thành công! Chào mừng Admin " + loginData.getUsername(),
                                    Toast.LENGTH_SHORT).show();
                                
                                // Chuyển đến AdminActivity
                                startAdminActivity();
                                
                            } else if ("student".equals(role)) {
                                // Lưu session cho sinh viên
                                StudentProfile studentProfile = loginData.getProfile();
                                if (studentProfile != null) {
                                    sessionManager.createSession(
                                        loginData.getUserId(),
                                        loginData.getUsername(),
                                        loginData.getRole(),
                                        studentProfile.getStudentId(),
                                        studentProfile.getStudentCode(),
                                        studentProfile.getFullName()
                                    );
                                    
                                    Toast.makeText(LoginActivity.this,
                                        "Đăng nhập thành công! Chào mừng " + studentProfile.getFullName(),
                                        Toast.LENGTH_SHORT).show();
                                    
                                    // Chuyển đến ScheduleActivity (sinh viên)
                                    startScheduleActivity();
                                } else {
                                    showError("Lỗi: Không nhận được thông tin sinh viên");
                                }
                                
                            } else {
                                showError("Role không được hỗ trợ: " + role);
                            }
                        } else {
                            Log.e(TAG, "Login data is null");
                            showError("Lỗi: Không nhận được thông tin đăng nhập");
                        }
                    } else {
                        // Đăng nhập thất bại
                        Log.e(TAG, "Login failed: " + loginResponse.getMessage());
                        showError(loginResponse.getMessage());
                    }
                } else {
                    // Lỗi HTTP
                    Log.e(TAG, "HTTP Error: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    showError("Lỗi kết nối server (Mã lỗi: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Login request failed", t);
                Log.e(TAG, "Error message: " + t.getMessage());
                Log.e(TAG, "Error cause: " + (t.getCause() != null ? t.getCause().getMessage() : "No cause"));

                String errorMessage = "Không thể kết nối đến server";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("ConnectException")) {
                        errorMessage = "Không thể kết nối đến server. Kiểm tra địa chỉ IP.";
                    } else if (t.getMessage().contains("SocketTimeoutException")) {
                        errorMessage = "Kết nối bị timeout. Kiểm tra kết nối mạng.";
                    } else if (t.getMessage().contains("UnknownHostException")) {
                        errorMessage = "Không tìm thấy server. Kiểm tra địa chỉ IP.";
                    }
                }

                showError(errorMessage);
            }
        });
    }

    private void startScheduleActivity() {
        Intent intent = new Intent(LoginActivity.this, ScheduleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startAdminActivity() {
        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setText("Đang đăng nhập...");
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnLogin.setText("ĐĂNG NHẬP");
        }
    }

    private void showError(String message) {
        Log.e(TAG, "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        // Tắt ứng dụng khi nhấn back ở màn hình login
        finishAffinity();
    }

    private void redirectToAppropriateActivity() {
        if (sessionManager.isAdmin()) {
            startAdminActivity();
        } else if (sessionManager.isStudent()) {
            startScheduleActivity();
        } else {
            // Role không xác định, logout và yêu cầu đăng nhập lại
            sessionManager.logout();
            Toast.makeText(this, "Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
        }
    }
}