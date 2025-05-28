package com.example.doan;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class AddStudentToTimetableActivity extends AppCompatActivity implements AddStudentAdapter.OnStudentSelectListener {
    private static final String TAG = "AddStudentToTimetableActivity";

    private TextView tvTimetableInfo;
    private EditText etSearch;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvNoData;

    private ApiService apiService;
    private AddStudentAdapter adapter;
    private List<TimetableStudent> availableStudentsList = new ArrayList<>();
    private AdminTimetable currentTimetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student_to_timetable);

        // Get timetable from intent
        currentTimetable = (AdminTimetable) getIntent().getSerializableExtra("timetable");
        if (currentTimetable == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin thời khóa biểu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupSearch();
        setupClickListeners();

        apiService = ApiClient.getClient().create(ApiService.class);
        loadAvailableStudents("");
    }

    private void initViews() {
        tvTimetableInfo = findViewById(R.id.tvTimetableInfo);
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        tvNoData = findViewById(R.id.tvNoData);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Thêm sinh viên vào lớp");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new AddStudentAdapter(this, availableStudentsList);
        adapter.setOnStudentSelectListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                loadAvailableStudents(s.toString());
            }
        });
    }

    private void setupClickListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadAvailableStudents(etSearch.getText().toString());
        });
        
        // Update timetable info
        updateTimetableInfo();
    }

    private void updateTimetableInfo() {
        String info = currentTimetable.getSubjectInfo() + "\n" +
                     "Lớp: " + currentTimetable.getSubjectClassCode() + " - " + currentTimetable.getSemester() + "\n" +
                     currentTimetable.getDayName() + " - " + currentTimetable.getPeriodText() + " (" + currentTimetable.getTimeRange() + ")\n" +
                     "GV: " + currentTimetable.getTeacherFullName();
        tvTimetableInfo.setText(info);
    }

    private void loadAvailableStudents(String search) {
        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);

        apiService.getAvailableStudents(currentTimetable.getSubjectClassId(), search).enqueue(new Callback<AvailableStudentsResponse>() {
            @Override
            public void onResponse(Call<AvailableStudentsResponse> call, Response<AvailableStudentsResponse> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        availableStudentsList.clear();
                        if (response.body().getData() != null) {
                            availableStudentsList.addAll(response.body().getData());
                        }
                        updateUI();
                    } else {
                        showError("Lỗi: " + response.body().getMessage());
                    }
                } else {
                    showError("Không thể tải danh sách sinh viên");
                }
            }

            @Override
            public void onFailure(Call<AvailableStudentsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Load available students failed", t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void updateUI() {
        if (availableStudentsList.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.updateStudents(availableStudentsList);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStudentSelected(TimetableStudent student) {
        progressBar.setVisibility(View.VISIBLE);

        AddStudentToTimetableRequest request = new AddStudentToTimetableRequest(
                currentTimetable.getTimetableId(), 
                student.getStudentId()
        );

        apiService.addStudentToTimetable(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(AddStudentToTimetableActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        showError("Lỗi: " + response.body().getMessage());
                    }
                } else {
                    showError("Không thể thêm sinh viên");
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Add student failed", t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 