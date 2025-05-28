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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class AdminGradesActivity extends AppCompatActivity {
    private static final String TAG = "AdminGradesActivity";
    
    private RecyclerView rvGrades;
    private GradesAdapter adapter;
    private List<AdminGrade> gradesList = new ArrayList<>();
    
    private EditText etSearchStudent;
    private Spinner spSemesters;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    
    private ApiService apiService;
    
    // Filter parameters
    private String currentSearchStudent = "";
    private String currentSemester = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_grades);
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupSemesterSpinner();
        loadGrades();
    }

    private void initViews() {
        rvGrades = findViewById(R.id.rvGrades);
        etSearchStudent = findViewById(R.id.etSearchStudent);
        spSemesters = findViewById(R.id.spSemesters);
        progressBar = findViewById(R.id.progressBar);
        fabAdd = findViewById(R.id.fabAdd);
        
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Thiết lập toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý điểm");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new GradesAdapter(gradesList, new GradesAdapter.OnGradeClickListener() {
            @Override
            public void onEditClick(AdminGrade grade) {
                openEditGrade(grade);
            }

            @Override
            public void onDeleteClick(AdminGrade grade) {
                confirmDeleteGrade(grade);
            }
        });
        
        rvGrades.setLayoutManager(new LinearLayoutManager(this));
        rvGrades.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Search by student
        etSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearchStudent = s.toString().trim();
                loadGrades();
            }
        });

        // Semester filter
        spSemesters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    currentSemester = ""; // Tất cả học kỳ
                } else {
                    currentSemester = getSemesterFromPosition(position);
                }
                loadGrades();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Add new grade
        fabAdd.setOnClickListener(v -> openAddGrade());
    }

    private void setupSemesterSpinner() {
        List<String> semesters = new ArrayList<>();
        semesters.add("Tất cả học kỳ");
        semesters.add("HK1 2023-2024");
        semesters.add("HK2 2023-2024");
        semesters.add("HK1 2024-2025");
        semesters.add("HK2 2024-2025");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSemesters.setAdapter(adapter);
    }

    private String getSemesterFromPosition(int position) {
        switch (position) {
            case 1: return "HK1 2023-2024";
            case 2: return "HK2 2023-2024";
            case 3: return "HK1 2024-2025";
            case 4: return "HK2 2024-2025";
            default: return "";
        }
    }

    private void loadGrades() {
        progressBar.setVisibility(View.VISIBLE);
        
        // Tạm thời để null cho studentId và subjectClassId để lấy tất cả
        Integer studentId = null;
        Integer subjectClassId = null;
        String semester = currentSemester.isEmpty() ? null : currentSemester;
        
        apiService.getGrades(studentId, subjectClassId, semester)
                .enqueue(new Callback<AdminGradesResponse>() {
            @Override
            public void onResponse(Call<AdminGradesResponse> call, Response<AdminGradesResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AdminGradesResponse gradesResponse = response.body();
                    gradesList.clear();
                    
                    // Filter by student search locally
                    List<AdminGrade> allGrades = gradesResponse.getData();
                    if (currentSearchStudent.isEmpty()) {
                        gradesList.addAll(allGrades);
                    } else {
                        for (AdminGrade grade : allGrades) {
                            if (grade.getStudentName().toLowerCase().contains(currentSearchStudent.toLowerCase()) ||
                                grade.getStudentCode().toLowerCase().contains(currentSearchStudent.toLowerCase())) {
                                gradesList.add(grade);
                            }
                        }
                    }
                    
                    adapter.notifyDataSetChanged();
                    
                    if (gradesList.isEmpty()) {
                        showEmptyState();
                    }
                } else {
                    Toast.makeText(AdminGradesActivity.this, "Không thể tải danh sách điểm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminGradesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load grades failed", t);
                Toast.makeText(AdminGradesActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        // TODO: Show empty state view
        Toast.makeText(this, "Không có dữ liệu điểm", Toast.LENGTH_SHORT).show();
    }

    private void openAddGrade() {
        Intent intent = new Intent(this, AddEditGradeActivity.class);
        intent.putExtra("mode", "add");
        startActivityForResult(intent, 100);
    }

    private void openEditGrade(AdminGrade grade) {
        Intent intent = new Intent(this, AddEditGradeActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("grade", grade);
        startActivityForResult(intent, 101);
    }

    private void confirmDeleteGrade(AdminGrade grade) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa điểm của sinh viên " + grade.getStudentName() + 
                           " môn " + grade.getSubjectName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteGrade(grade))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteGrade(AdminGrade grade) {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.deleteGrade(grade.getGradeId()).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminGradesActivity.this, "Xóa điểm thành công", Toast.LENGTH_SHORT).show();
                    loadGrades(); // Reload list
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể xóa điểm";
                    Toast.makeText(AdminGradesActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Delete grade failed", t);
                Toast.makeText(AdminGradesActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadGrades(); // Reload list after add/edit
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 