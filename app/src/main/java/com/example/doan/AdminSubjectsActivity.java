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

public class AdminSubjectsActivity extends AppCompatActivity {
    private static final String TAG = "AdminSubjectsActivity";
    
    private RecyclerView rvSubjects;
    private SubjectsAdapter adapter;
    private List<Subject> subjectsList = new ArrayList<>();
    
    private EditText etSearch;
    private Spinner spDepartmentFilter;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    
    private ApiService apiService;
    private List<Department> departmentsList = new ArrayList<>();
    
    private String currentSearch = "";
    private Integer currentDepartmentFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_subjects);
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadDepartments();
        loadSubjects();
    }

    private void initViews() {
        rvSubjects = findViewById(R.id.rvSubjects);
        etSearch = findViewById(R.id.etSearch);
        spDepartmentFilter = findViewById(R.id.spDepartmentFilter);
        progressBar = findViewById(R.id.progressBar);
        fabAdd = findViewById(R.id.fabAdd);
        
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Thiết lập toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý môn học");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new SubjectsAdapter(subjectsList, new SubjectsAdapter.OnSubjectClickListener() {
            @Override
            public void onEditClick(Subject subject) {
                openEditSubject(subject);
            }

            @Override
            public void onDeleteClick(Subject subject) {
                confirmDeleteSubject(subject);
            }

            @Override
            public void onViewClassesClick(Subject subject) {
                openSubjectClasses(subject);
            }
        });
        
        rvSubjects.setLayoutManager(new LinearLayoutManager(this));
        rvSubjects.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearch = s.toString().trim();
                loadSubjects();
            }
        });

        // Department filter
        spDepartmentFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    currentDepartmentFilter = null;
                } else {
                    Department selected = departmentsList.get(position - 1);
                    currentDepartmentFilter = selected.getDepartmentId();
                }
                loadSubjects();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Add new subject
        fabAdd.setOnClickListener(v -> openAddSubject());
    }

    private void loadDepartments() {
        apiService.getDepartments().enqueue(new Callback<DepartmentsResponse>() {
            @Override
            public void onResponse(Call<DepartmentsResponse> call, Response<DepartmentsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    departmentsList.clear();
                    departmentsList.addAll(response.body().getData());
                    setupDepartmentSpinner();
                }
            }

            @Override
            public void onFailure(Call<DepartmentsResponse> call, Throwable t) {
                Log.e(TAG, "Load departments failed", t);
            }
        });
    }

    private void setupDepartmentSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Tất cả khoa");
        
        for (Department dept : departmentsList) {
            spinnerItems.add(dept.getDepartmentName());
        }
        
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartmentFilter.setAdapter(spinnerAdapter);
    }

    private void loadSubjects() {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.getSubjects(currentSearch, currentDepartmentFilter).enqueue(new Callback<SubjectsResponse>() {
            @Override
            public void onResponse(Call<SubjectsResponse> call, Response<SubjectsResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    subjectsList.clear();
                    subjectsList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminSubjectsActivity.this, "Không thể tải danh sách môn học", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SubjectsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load subjects failed", t);
                Toast.makeText(AdminSubjectsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAddSubject() {
        Intent intent = new Intent(this, AddEditSubjectActivity.class);
        intent.putExtra("mode", "add");
        startActivityForResult(intent, 100);
    }

    private void openEditSubject(Subject subject) {
        Intent intent = new Intent(this, AddEditSubjectActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("subject", subject);
        startActivityForResult(intent, 101);
    }

    private void confirmDeleteSubject(Subject subject) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa môn học " + subject.getName() + "?\n\nCảnh báo: Tất cả điểm và lớp học liên quan sẽ bị ảnh hưởng.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteSubject(subject))
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteSubject(Subject subject) {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.deleteSubject(subject.getSubjectId()).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminSubjectsActivity.this, "Xóa môn học thành công", Toast.LENGTH_SHORT).show();
                    loadSubjects(); // Reload list
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể xóa môn học";
                    Toast.makeText(AdminSubjectsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Delete subject failed", t);
                Toast.makeText(AdminSubjectsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openSubjectClasses(Subject subject) {
        Intent intent = new Intent(this, SubjectClassesActivity.class);
        intent.putExtra("subject", subject);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadSubjects(); // Reload list after add/edit
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 