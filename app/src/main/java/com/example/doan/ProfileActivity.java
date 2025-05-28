package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.AlertDialog;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";

    private ImageView profileImage;
    private TextView studentName;
    private TextView studentCode;
    private TextView studentBirthDate;

    private TextView studentEmail;

    private TextView studentAdress;
    private TextView studentClass;
    private TextView studentDepartment;
    private LinearLayout loadingLayout;
    private LinearLayout contentLayout;

    private Button btnLogout;

    private ApiService apiService;
    private SessionManager sessionManager;
    private int studentId;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        initData();
        setupNavigation();
        loadStudentProfile();
        setupLogoutButton();

        // Khởi tạo GestureDetector
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
        // Gán sự kiện touch cho layout chính (có thể là contentLayout hoặc toàn màn hình)
        findViewById(R.id.contentLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void initViews() {
        profileImage = findViewById(R.id.profileImage);
        studentName = findViewById(R.id.studentName);
        studentCode = findViewById(R.id.studentCode);
        studentBirthDate = findViewById(R.id.studentBirthDate);
        studentEmail = findViewById(R.id.studentEmail);
        studentAdress = findViewById(R.id.studentAddress);
        studentClass = findViewById(R.id.studentClass);
        studentDepartment = findViewById(R.id.studentDepartment);
        loadingLayout = findViewById(R.id.loadingLayout);
        contentLayout = findViewById(R.id.contentLayout);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void initData() {
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);
        studentId = sessionManager.getStudentId();

        Log.d(TAG, "Student ID: " + studentId);
    }

    private void loadStudentProfile() {
        if (studentId == 0) {
            showError("Không tìm thấy thông tin sinh viên. Vui lòng đăng nhập lại.");
            return;
        }

        showLoading(true);
        Log.d(TAG, "Loading profile for student ID: " + studentId);

        Call<StudentProfileResponse> call = apiService.getStudentProfile(studentId);
        call.enqueue(new Callback<StudentProfileResponse>() {
            @Override
            public void onResponse(Call<StudentProfileResponse> call, Response<StudentProfileResponse> response) {
                showLoading(false);

                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    StudentProfileResponse profileResponse = response.body();
                    Log.d(TAG, "Response success: " + profileResponse.isSuccess());

                    if (profileResponse.isSuccess()) {
                        StudentProfile data = profileResponse.getData();
                        if (data != null) {
                            populateProfileData(data);
                        } else {
                            showError("Không có dữ liệu thông tin cá nhân");
                        }
                    } else {
                        showError(profileResponse.getMessage());
                    }
                } else {
                    Log.e(TAG, "Response not successful. Code: " + response.code());
                    showError("Lỗi kết nối server. Mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StudentProfileResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "API call failed", t);

                String errorMessage = "Không thể kết nối đến server";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("ConnectException")) {
                        errorMessage = "Không thể kết nối đến server. Kiểm tra kết nối mạng.";
                    } else if (t.getMessage().contains("SocketTimeoutException")) {
                        errorMessage = "Kết nối bị timeout. Kiểm tra kết nối mạng.";
                    }
                }

                showError(errorMessage);
            }
        });
    }

    private void populateProfileData(StudentProfile profile) {
        contentLayout.setVisibility(View.VISIBLE);

        // Hiển thị thông tin cá nhân
        studentName.setText(profile.getFullName() != null ? profile.getFullName() : "N/A");
        studentCode.setText((profile.getStudentCode() != null ? profile.getStudentCode() : "N/A") + " | Sinh viên");
        studentBirthDate.setText(profile.getBirthDate() != null ? profile.getBirthDate() : "N/A");
        studentAdress.setText(profile.getStudentAddress() != null ? profile.getStudentAddress() : "N/A");
        studentEmail.setText(profile.getStudentEmail() != null ? profile.getStudentEmail() : "N/A");
        studentClass.setText(profile.getClassName() != null ? profile.getClassName() : "N/A");
        studentDepartment.setText(profile.getDepartment() != null ? profile.getDepartment() : "N/A");

        // Set profile image placeholder
        profileImage.setImageResource(R.drawable.ic_profile_placeholder);

        Log.d(TAG, "Profile data populated successfully");
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
        } else {
            loadingLayout.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        Log.e(TAG, "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        showLoading(false);

        // Hiển thị dữ liệu từ session nếu có
        displaySessionData();
    }

    private void displaySessionData() {
        contentLayout.setVisibility(View.VISIBLE);

        // Hiển thị thông tin cơ bản từ session
        String fullName = sessionManager.getStudentFullName();
        String code = sessionManager.getStudentCode();

        if (fullName != null && !fullName.isEmpty()) {
            studentName.setText(fullName);
        }

        if (code != null && !code.isEmpty()) {
            studentCode.setText(code + " | Sinh viên");
        }

        // Các trường khác để trống hoặc hiển thị "Đang cập nhật..."
        studentBirthDate.setText("Đang cập nhật...");
        studentClass.setText("Đang cập nhật...");
        studentDepartment.setText("Đang cập nhật...");
    }

    private void setupLogoutButton() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị dialog xác nhận đăng xuất
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                        .setPositiveButton("Đăng xuất", (dialog, which) -> {
                            // Xóa session
                            sessionManager.logout();
                            // Chuyển về LoginActivity và clear task stack
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }

    @Override
    protected void highlightCurrentNavItem() {
        super.highlightCurrentNavItem();
        // Highlight navigation item cho Profile nếu có
        LinearLayout navProfile = findViewById(R.id.navCaNhan);
        if (navProfile != null) {
            navProfile.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            for (int i = 0; i < navProfile.getChildCount(); i++) {
                if (navProfile.getChildAt(i) instanceof TextView) {
                    TextView textView = (TextView) navProfile.getChildAt(i);
                    textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                }
            }
        }
    }

    // Lớp lắng nghe sự kiện vuốt
    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Vuốt sang phải
                        goToPreviousActivity();
                    } else {
                        // Vuốt sang trái
                        goToNextActivity();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private void goToPreviousActivity() {
        // Vuốt phải: sang LichThiActivity
        Intent intent = new Intent(ProfileActivity.this, LichThiActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    private void goToNextActivity() {
        // Vuốt trái: sang ScheduleActivity
        Intent intent = new Intent(ProfileActivity.this, ScheduleActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}