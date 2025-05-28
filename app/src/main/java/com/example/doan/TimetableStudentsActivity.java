package com.example.doan;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class TimetableStudentsActivity extends AppCompatActivity implements TimetableStudentsAdapter.OnStudentActionListener {
    private static final String TAG = "TimetableStudentsActivity";

    private TextView tvTimetableInfo;
    private EditText etSearch;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private FloatingActionButton fabAdd;

    private ApiService apiService;
    private TimetableStudentsAdapter adapter;
    private List<TimetableStudent> studentsList = new ArrayList<>();
    private AdminTimetable currentTimetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_students);

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
        loadTimetableStudents();
    }

    private void initViews() {
        tvTimetableInfo = findViewById(R.id.tvTimetableInfo);
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        tvNoData = findViewById(R.id.tvNoData);
        fabAdd = findViewById(R.id.fabAdd);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sinh viên trong lớp");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new TimetableStudentsAdapter(this, studentsList);
        adapter.setOnStudentActionListener(this);
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
                filterStudents(s.toString());
            }
        });
    }

    private void setupClickListeners() {
        fabAdd.setOnClickListener(v -> showAddStudentDialog());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadTimetableStudents();
        });
    }

    private void loadTimetableStudents() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);

        apiService.getTimetableStudents(currentTimetable.getTimetableId()).enqueue(new Callback<TimetableStudentsResponse>() {
            @Override
            public void onResponse(Call<TimetableStudentsResponse> call, Response<TimetableStudentsResponse> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        // Update timetable info
                        updateTimetableInfo(response.body().getTimetableInfo());
                        
                        // Update students list
                        studentsList.clear();
                        if (response.body().getStudents() != null) {
                            studentsList.addAll(response.body().getStudents());
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
            public void onFailure(Call<TimetableStudentsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Load students failed", t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void updateTimetableInfo(AdminTimetable timetableInfo) {
        if (timetableInfo != null) {
            String info = timetableInfo.getSubjectInfo() + "\n" +
                         "Lớp: " + timetableInfo.getSubjectClassCode() + " - " + timetableInfo.getSemester() + "\n" +
                         timetableInfo.getDayName() + " - " + timetableInfo.getPeriodText() + " (" + timetableInfo.getTimeRange() + ")\n" +
                         "GV: " + timetableInfo.getTeacherFullName();
            tvTimetableInfo.setText(info);
        }
    }

    private void filterStudents(String query) {
        adapter.filter(query);
    }

    private void updateUI() {
        if (studentsList.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.updateStudents(studentsList);
        }
    }

    private void showAddStudentDialog() {
        Intent intent = new Intent(this, AddStudentToTimetableActivity.class);
        intent.putExtra("timetable", currentTimetable);
        startActivityForResult(intent, 100);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveStudent(TimetableStudent student) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sinh viên " + student.getStudentFullName() + 
                           " (" + student.getStudentCode() + ") khỏi lớp học này?")
                .setPositiveButton("Xóa", (dialog, which) -> removeStudent(student.getEnrollmentId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void removeStudent(int enrollmentId) {
        progressBar.setVisibility(View.VISIBLE);

        apiService.removeStudentFromTimetable(enrollmentId).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(TimetableStudentsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        loadTimetableStudents(); // Reload data
                    } else {
                        showError("Lỗi: " + response.body().getMessage());
                    }
                } else {
                    showError("Không thể xóa sinh viên");
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Remove student failed", t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            loadTimetableStudents(); // Reload data after adding student
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 