package com.example.doan;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LichThiActivity extends AppCompatActivity {
    private LinearLayout container;
    private int userId = 1;               // gán user_id hiện tại
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lich_thi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLichThi), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        container = findViewById(R.id.examListContainer);
        loadData();
    }

    private void loadData() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getExamSchedule(userId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> resp) {
                // 1. In toàn bộ body ra Log để kiểm tra
                if (resp.body() != null) {
                    Log.d("LichThiActivity", "API response = "
                            + new Gson().toJson(resp.body()));
                } else {
                    Log.e("LichThiActivity", "Response body null, code=" + resp.code());
                }

                // 2. Kiểm tra success và data
                if (resp.isSuccessful() && resp.body() != null && resp.body().isSuccess()) {
                    List<ExamSchedule> list = resp.body().getData();
                    Log.d("LichThiActivity", "Received " + list.size() + " items");
                    // 3. Nếu có data, thêm vào view
                    for (ExamSchedule e : list) {
                        addCard(e);
                    }
                } else {
                    Toast.makeText(LichThiActivity.this,
                            "Không có dữ liệu hoặc success=false",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("LichThiActivity", "onFailure: ", t);
                Toast.makeText(LichThiActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addCard(ExamSchedule e) {
        View v = getLayoutInflater().inflate(R.layout.item_exam, container, false);

        ((TextView)v.findViewById(R.id.tvNgayThi))
                .setText("Ngày thi: "+e.getExamDate());
        ((TextView)v.findViewById(R.id.tvMonThi))
                .setText("Môn: "+e.getSubjectName());
        ((TextView)v.findViewById(R.id.tvThoiGianBatDau))
                .setText("Bắt đầu: "+e.getStartTime());
        ((TextView)v.findViewById(R.id.tvThoiGianKetThuc))
                .setText("Kết thúc: "+e.getEndTime());
        ((TextView)v.findViewById(R.id.tvPhongThi))
                .setText("Phòng: "+e.getExamRoom());
        container.addView(v);
    }
}