package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.adapter.ExamScheduleAdapter;
import com.example.doan.model.ExamDay;
import com.example.doan.model.ExamSession;
import com.example.doan.model.ExamSchedule;
import com.example.doan.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class LichThiActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ExamScheduleAdapter adapter;
    private List<ExamDay> examDays = new ArrayList<>();
    private ProgressBar progressBar;
    private ApiService apiService;
    private SessionManager sessionManager;
    private static final String TAG = "LichThiActivity";
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLichThi), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(this);

        // Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // Khởi tạo views
        recyclerView = findViewById(R.id.recyclerViewExamSchedule);
        progressBar = findViewById(R.id.progressBar);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExamScheduleAdapter(examDays);
        recyclerView.setAdapter(adapter);

        // Setup navigation từ BaseActivity
        setupNavigation();

        // Thiết lập gesture detector cho vuốt
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
        findViewById(R.id.mainLichThi).setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        // Load dữ liệu
        loadExamSchedule();
    }

    private void loadExamSchedule() {
        progressBar.setVisibility(View.VISIBLE);

        // Lấy student_id từ SessionManager
        int studentId = sessionManager.getStudentId();
        Log.d(TAG, "Loading exam schedule for student ID: " + studentId);

        if (studentId <= 0) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Không tìm thấy thông tin sinh viên", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API lấy lịch thi
        Call<ApiResponse> call = apiService.getExamSchedule(studentId);
        Log.d(TAG, "API call URL: " + call.request().url());
        Log.d(TAG, "API call method: " + call.request().method());
        Log.d(TAG, "API call headers: " + call.request().headers());
        
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                Log.d(TAG, "API Response code: " + response.code());
                if (!response.isSuccessful()) {
                    Log.e(TAG, "API Error: " + response.message());
                    try {
                        Log.e(TAG, "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Could not read error body", e);
                    }
                    Toast.makeText(LichThiActivity.this, "Lỗi khi tải dữ liệu: " + response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Log.d(TAG, "Raw API Response: " + new Gson().toJson(apiResponse));
                    Log.d(TAG, "API Response success: " + apiResponse.isSuccess());
                    if (apiResponse.isSuccess()) {
                        List<ExamSchedule> examSchedules = apiResponse.getData();
                        Log.d(TAG, "Received " + (examSchedules != null ? examSchedules.size() : 0) + " exam schedules");
                        if (examSchedules != null && !examSchedules.isEmpty()) {
                            processExamSchedules(examSchedules);
                        } else {
                            Log.e(TAG, "Exam schedules list is null or empty");
                            Toast.makeText(LichThiActivity.this, "Không có dữ liệu lịch thi", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "API returned success=false");
                        String errorMessage = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Lỗi khi tải dữ liệu";
                        Toast.makeText(LichThiActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Response body is null");
                    Toast.makeText(LichThiActivity.this, "Lỗi khi tải dữ liệu: Response body is null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "API call failed", t);
                Toast.makeText(LichThiActivity.this, "Lỗi kết nối server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processExamSchedules(List<ExamSchedule> examSchedules) {
        Log.d(TAG, "Starting to process " + examSchedules.size() + " exam schedules");
        examDays.clear();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.US);

        for (ExamSchedule schedule : examSchedules) {
            try {
                // Log để debug
                Log.d(TAG, "Processing exam schedule: " + schedule.toString());
                
                String dateStr = schedule.getDate();
                if (dateStr == null || dateStr.isEmpty()) {
                    Log.e(TAG, "Empty exam date, skipping record");
                    continue;
                }

                Date date = inputFormat.parse(dateStr);
                if (date == null) {
                    Log.e(TAG, "Failed to parse date: " + dateStr);
                    continue;
                }

                String formattedDate = outputFormat.format(date);
                Log.d(TAG, "Formatted date: " + formattedDate);

                List<ExamSession> sessions = new ArrayList<>();
                for (ExamSchedule.Session apiSession : schedule.getSessions()) {
                    ExamSession session = new ExamSession(
                        apiSession.getSessionNumber(),
                        apiSession.getTime(),
                        apiSession.getSubjectCode(),
                        apiSession.getSubjectName(),
                        apiSession.getRoom()
                    );
                    sessions.add(session);
                }

                examDays.add(new ExamDay(formattedDate, sessions));
                Log.d(TAG, "Added exam day for date " + formattedDate + " with " + sessions.size() + " sessions");
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Error processing exam schedule: " + e.getMessage());
            }
        }

        Log.d(TAG, "Final exam days count: " + examDays.size());
        if (examDays.isEmpty()) {
            Toast.makeText(this, "Không có lịch thi", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }

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
        // Vuốt phải: sang GradeActivity
        Intent intent = new Intent(LichThiActivity.this, GradeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    private void goToNextActivity() {
        // Vuốt trái: sang ProfileActivity
        Intent intent = new Intent(LichThiActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}