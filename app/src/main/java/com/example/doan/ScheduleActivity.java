package com.example.doan;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.doan.ScheduleApiService;
import com.example.doan.Schedule;
import com.example.doan.ScheduleResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScheduleActivity extends AppCompatActivity {

    private TextView selectedDateTextView;
    private LinearLayout scheduleLayout;
    private ScheduleApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoikhoabieu);
        scheduleLayout = findViewById(R.id.scheduleLayout);
        // Thiết lập Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.7/DoAnAndroid/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ScheduleApiService.class);
        // Xử lý sự kiện chọn ngày
        setupClickListeners();
    }

    private void setupClickListeners() {
        setDayClickListener(R.id.sunday);
        setDayClickListener(R.id.monday);
        setDayClickListener(R.id.tuesday);
        setDayClickListener(R.id.wednesday);
        setDayClickListener(R.id.thursday);
        setDayClickListener(R.id.friday);
        setDayClickListener(R.id.saturday);
    }

    private void setDayClickListener(int textViewId) {
        TextView dayTextView = findViewById(textViewId);
        dayTextView.setOnClickListener(v -> {
            if (selectedDateTextView != null) {
                selectedDateTextView.setBackgroundColor(getResources().getColor(R.color.default_day_background));
            }
            dayTextView.setBackgroundColor(getResources().getColor(R.color.selected_day_background));
            selectedDateTextView = dayTextView;

            int dayOfWeek = getDayOfWeekFromId(textViewId);
            loadSchedule(dayOfWeek);
        });
    }

    private int getDayOfWeekFromId(int textViewId) {
        if (textViewId == R.id.sunday) return 1;
        else if (textViewId == R.id.monday) return 2;
        else if (textViewId == R.id.tuesday) return 3;
        else if (textViewId == R.id.wednesday) return 4;
        else if (textViewId == R.id.thursday) return 5;
        else if (textViewId == R.id.friday) return 6;
        else if (textViewId == R.id.saturday) return 7;
        else return -1;
    }

    private void loadSchedule(int dayOfWeek) {
        apiService.getSchedule(dayOfWeek).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                if (response.body() != null && response.body().success) {
                    updateScheduleUI(response.body().data);
                } else {
                    scheduleLayout.removeAllViews();
                    Toast.makeText(ScheduleActivity.this, "Không có lịch học.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                Toast.makeText(ScheduleActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateScheduleUI(List<Schedule> schedules) {
        scheduleLayout.removeAllViews(); // Xóa giao diện cũ trước khi hiển thị mới

        for (Schedule schedule : schedules) {
            // Tạo CardView
            CardView cardView = new CardView(this);
            cardView.setCardElevation(8);
            cardView.setRadius(12);
            cardView.setUseCompatPadding(true);
            cardView.setContentPadding(16, 16, 16, 16);

            // Tạo layout bên trong CardView
            LinearLayout cardContent = new LinearLayout(this);
            cardContent.setOrientation(LinearLayout.VERTICAL);

            // Thông tin tiết học
            TextView periodText = new TextView(this);
            periodText.setText("Tiết: " + schedule.period);
            periodText.setTextSize(16);
            periodText.setTypeface(null, Typeface.BOLD);

            TextView subjectText = new TextView(this);
            subjectText.setText("Môn: " + schedule.subject_name + " (" + schedule.subject_class + ")");
            subjectText.setTextSize(14);

            TextView timeText = new TextView(this);
            timeText.setText("Thời gian: " + schedule.start_time + " - " + schedule.end_time);
            timeText.setTextSize(14);

            // Thêm TextView vào CardView
            cardContent.addView(periodText);
            cardContent.addView(subjectText);
            cardContent.addView(timeText);

            cardView.addView(cardContent);

            // Thêm CardView vào layout chính
            scheduleLayout.addView(cardView);
        }
    }
}