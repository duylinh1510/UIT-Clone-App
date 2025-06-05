package com.example.doan;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.List;

public class ScheduleActivity extends BaseActivity {

    private static final String TAG = "ScheduleActivity";

    private TextView txtMonth;
    private TextView[] dayTextViews;
    private TextView selectedDateTextView;
    private LinearLayout scheduleLayout;
    private ScheduleApiService apiService;
    private List<WeekUtils.DayInfo> weekDaysData;
    private GestureDetector gestureDetector;
    private SessionManager sessionManager;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoikhoabieu);

        // Khởi tạo views
        initViews();
        
        // Khởi tạo session manager
        sessionManager = new SessionManager(this);
        
        // Lấy student ID từ session
        studentId = sessionManager.getStudentId();
        
        Log.i(TAG, "onCreate: studentId = " + studentId);
        Log.i(TAG, "onCreate: studentCode = " + sessionManager.getStudentCode());
        Log.i(TAG, "onCreate: studentName = " + sessionManager.getStudentFullName());
        
        if (studentId == 0) {
            Log.e(TAG, "Student ID is 0 - session problem!");
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin sinh viên", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Setup Retrofit
        setupRetrofit();

        // Setup calendar
        setupCalendar();
        
        // Setup navigation từ BaseActivity
        setupNavigation();

        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
        findViewById(R.id.scheduleLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void initViews() {
        txtMonth = findViewById(R.id.txtMonth);
        scheduleLayout = findViewById(R.id.scheduleLayout);

        // Khởi tạo mảng TextView cho các ngày
        //Tạo 1 mảng có 7 phần tu
        dayTextViews = new TextView[7];
        dayTextViews[0] = findViewById(R.id.sunday);
        dayTextViews[1] = findViewById(R.id.monday);
        dayTextViews[2] = findViewById(R.id.tuesday);
        dayTextViews[3] = findViewById(R.id.wednesday);
        dayTextViews[4] = findViewById(R.id.thursday);
        dayTextViews[5] = findViewById(R.id.friday);
        dayTextViews[6] = findViewById(R.id.saturday);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.5/DoAnAndroid/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ScheduleApiService.class);
    }

    private void setupCalendar() {
        // Set tên tháng
        txtMonth.setText(WeekUtils.getCurrentMonthName());

        // Lấy thông tin các ngày trong tuần
        weekDaysData = WeekUtils.getCurrentWeekDays();

        // Cập nhật từng TextView
        for (int i = 0; i < weekDaysData.size() && i < dayTextViews.length; i++) {
            WeekUtils.DayInfo dayInfo = weekDaysData.get(i);
            TextView dayTextView = dayTextViews[i];
            final int dayIndex = i;

            if (dayTextView != null) {
                // Set số ngày
                dayTextView.setText(dayInfo.dayNumber);

                // Highlight ngày hôm nay
                if (dayInfo.isToday) {
                    highlightToday(dayTextView);
                    // Auto load schedule for today
                    loadScheduleForDay(dayIndex + 1, dayTextView);
                } else {
                    // Reset về style mặc định
                    resetDayStyle(dayTextView);
                }

                // Set click listener
                dayTextView.setOnClickListener(v -> {
                    onDayClicked(dayIndex, dayTextView, dayInfo);
                });
            }
        }
    }

    private void onDayClicked(int dayIndex, TextView clickedTextView, WeekUtils.DayInfo dayInfo) {
        // Reset style cho ngày trước đó
        if (selectedDateTextView != null && selectedDateTextView != clickedTextView) {
            resetDayStyle(selectedDateTextView);
        }

        // Highlight ngày được chọn
        highlightSelectedDay(clickedTextView);
        selectedDateTextView = clickedTextView;

        // Load schedule cho ngày được chọn
        int dayOfWeek = dayIndex + 1; // Calendar: Sunday=1, Monday=2, ...
        loadScheduleForDay(dayOfWeek, clickedTextView);

        // Show toast cho user feedback
        Toast.makeText(this, "Đã chọn: " + dayInfo.fullDate, Toast.LENGTH_SHORT).show();
    }

    private void loadScheduleForDay(int dayOfWeek, TextView dayTextView) {
        Log.i(TAG, "loadScheduleForDay: dayOfWeek = " + dayOfWeek + ", studentId = " + studentId);
        
        //Hiển thị trạng thái loading
        scheduleLayout.removeAllViews();
        TextView loadingText = new TextView(this);
        loadingText.setText("Đang tải thời khóa biểu...");
        loadingText.setTextSize(16);
        loadingText.setPadding(16, 16, 16, 16);
        scheduleLayout.addView(loadingText);

        // API call
        apiService.getSchedule(dayOfWeek, studentId).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                scheduleLayout.removeAllViews(); // Remove loading text

                // thêm logcat để debug
                Log.i(TAG, "API Response: success = " + response.isSuccessful());
                Log.i(TAG, "Response body: " + (response.body() != null ? response.body().toString() : "null"));
                
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Response success = " + response.body().success);
                    Log.i(TAG, "Response data size = " + (response.body().data != null ? response.body().data.size() : 0));
                    
                    if (response.body().success) {
                        if (response.body().data != null && !response.body().data.isEmpty()) {
                            Log.i(TAG, "Updating UI with " + response.body().data.size() + " schedules");
                            updateScheduleUI(response.body().data);
                        } else {
                            Log.i(TAG, "No schedule data for this day");
                            showEmptySchedule();
                        }
                    } else {
                        Log.e(TAG, "Server returned success=false");
                        showErrorMessage("Server error: " + (response.body().message != null ? response.body().message : "Unknown error"));
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                    showErrorMessage("Không thể tải thời khóa biểu (HTTP " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                scheduleLayout.removeAllViews();
                Log.e(TAG, "API call failed", t);
                showErrorMessage("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    //hàm update giao diện của TKB
    private void updateScheduleUI(List<Schedule> schedules) {
        for (Schedule schedule : schedules) {
            // Tạo CardView
            //Mỗi 1 thời khóa biểu là 1 cardview
            CardView cardView = new CardView(this); //tạo 1 cardview mới ở activity hiện tại
            cardView.setCardElevation(8); //thiết lập đổ bóng
            cardView.setRadius(12); //độ bo góc
            cardView.setUseCompatPadding(true); // bật padding tương thích cho cardview, giúp đổ bóng chuẩn
            cardView.setContentPadding(16, 16, 16, 16); //Thiết lập padding bên trong CardView: trái, trên, phải, dưới (đơn vị dp).
            cardView.setCardBackgroundColor(Color.WHITE); //màu nền

            // Tạo layout bên trong CardView
            LinearLayout cardContent = new LinearLayout(this);
            cardContent.setOrientation(LinearLayout.VERTICAL);

            // Thông tin tiết học
            TextView periodText = new TextView(this);
            periodText.setText("Tiết: " + schedule.period);
            periodText.setTextSize(16);
            periodText.setTypeface(null, Typeface.BOLD);
            periodText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            //Môn học
            TextView subjectText = new TextView(this);
            subjectText.setText("Môn: " + schedule.subject_name + " (" + schedule.subject_class + ")");
            subjectText.setTextSize(14);
            subjectText.setTextColor(Color.BLACK);
            subjectText.setPadding(0, 8, 0, 4);
            //Thời gian học
            TextView timeText = new TextView(this);
            timeText.setText("Thời gian: " + schedule.start_time + " - " + schedule.end_time);
            timeText.setTextSize(14);
            timeText.setTextColor(Color.GRAY);

            // Thêm các TextView vào CardView
            cardContent.addView(periodText);
            cardContent.addView(subjectText);
            cardContent.addView(timeText);

            cardView.addView(cardContent);

            // Thêm CardView vào layout chính
            scheduleLayout.addView(cardView);
        }
    }
    //Nếu mà không có lịch học thì gọi hàm này
    private void showEmptySchedule() {
        TextView emptyText = new TextView(this);
        emptyText.setText("📅 Không có lịch học cho ngày này");
        emptyText.setTextSize(16);
        emptyText.setTextColor(Color.GRAY);
        emptyText.setPadding(16, 32, 16, 32);
        emptyText.setGravity(android.view.Gravity.CENTER);
        scheduleLayout.addView(emptyText);
    }

    //Nếu bị lỗi thì gọi hàm này
    private void showErrorMessage(String message) {
        TextView errorText = new TextView(this);
        errorText.setText("⚠️ " + message);
        errorText.setTextSize(16);
        errorText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        errorText.setPadding(16, 32, 16, 32);
        errorText.setGravity(android.view.Gravity.CENTER);
        scheduleLayout.addView(errorText);
    }

    //hàm để hightlight TextView
    private void highlightToday(TextView textView) {
        // Tạo background tròn màu xanh cho ngày hôm nay
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_light));
        drawable.setStroke(2, Color.WHITE);

        textView.setBackground(drawable);
        textView.setTextColor(Color.WHITE);
    }

    //highlight ngày được chọn
    private void highlightSelectedDay(TextView textView) {
        // Tạo background tròn màu xanh đậm cho ngày được chọn
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        drawable.setStroke(2, Color.WHITE);

        textView.setBackground(drawable);
        textView.setTextColor(Color.WHITE);
    }

    //reset màu của ngày được chọn
    private void resetDayStyle(TextView textView) {
        // Reset về style mặc định
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        textView.setBackgroundResource(outValue.resourceId);
        textView.setTextColor(Color.BLACK);
    }

    // Method để refresh calendar (gọi khi cần cập nhật)
    public void refreshCalendar() {
        setupCalendar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh calendar mỗi khi activity resume
        refreshCalendar();
    }


    //không sử dụng được
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
                        goToPreviousActivity();
                    } else {
                        goToNextActivity();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private void goToPreviousActivity() {
        // Vuốt phải: sang ProfileActivity
        Intent intent = new Intent(ScheduleActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    private void goToNextActivity() {
        // Vuốt trái: sang LichThiActivity
        // Schedule -> Grade (vuốt trái)
        Intent intent = new Intent(ScheduleActivity.this, GradeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}