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

public class AdminStudentsActivity extends AppCompatActivity {
    private static final String TAG = "AdminStudentsActivity";
    
    private RecyclerView rvStudents;
    private StudentsAdapter adapter;
    private List<AdminStudent> studentsList = new ArrayList<>();
    
    private EditText etSearch;
    private Spinner spDepartments;
    private Button btnPrevious, btnNext;
    private TextView tvPageInfo;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    
    private ApiService apiService;
    private List<Department> departmentsList = new ArrayList<>();
    
    // Pagination
    private int currentPage = 1;
    private int totalPages = 1;
    private final int limit = 10;
    private String currentSearch = "";
    private Integer currentDepartmentId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_students);
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadDepartments();
        loadStudents();
    }

    private void initViews() {
        rvStudents = findViewById(R.id.rvStudents);
        etSearch = findViewById(R.id.etSearch);
        spDepartments = findViewById(R.id.spDepartments);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        tvPageInfo = findViewById(R.id.tvPageInfo);
        progressBar = findViewById(R.id.progressBar);
        fabAdd = findViewById(R.id.fabAdd);
        
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Thiết lập toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý sinh viên");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new StudentsAdapter(studentsList, new StudentsAdapter.OnStudentClickListener() {
            @Override
            public void onEditClick(AdminStudent student) {
                openEditStudent(student);
            }

            @Override
            public void onDeleteClick(AdminStudent student) {
                confirmDeleteStudent(student);
            }
        });
        
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(adapter);
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
                currentPage = 1;
                loadStudents();
            }
        });

        // Department filter
        spDepartments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    currentDepartmentId = null; // Tất cả khoa
                } else {
                    Department selectedDept = departmentsList.get(position - 1);
                    currentDepartmentId = selectedDept.getDepartmentId();
                }
                currentPage = 1;
                loadStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Pagination
        btnPrevious.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadStudents();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadStudents();
            }
        });

        // Add new student
        fabAdd.setOnClickListener(v -> openAddStudent());
    }

    private void loadDepartments() {
        apiService.getDepartments().enqueue(new Callback<DepartmentsResponse>() {
            @Override
            public void onResponse(Call<DepartmentsResponse> call, Response<DepartmentsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    departmentsList = response.body().getData();
                    setupDepartmentSpinner();
                } else {
                    Toast.makeText(AdminStudentsActivity.this, "Không thể tải danh sách khoa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DepartmentsResponse> call, Throwable t) {
                Log.e(TAG, "Load departments failed", t);
                Toast.makeText(AdminStudentsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDepartmentSpinner() {
        List<String> departmentNames = new ArrayList<>();
        departmentNames.add("Tất cả khoa");
        for (Department dept : departmentsList) {
            departmentNames.add(dept.getDepartmentName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartments.setAdapter(adapter);
    }

    private void loadStudents() {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.getStudents(currentPage, limit, currentSearch, currentDepartmentId)
                .enqueue(new Callback<AdminStudentsResponse>() {
            @Override
            public void onResponse(Call<AdminStudentsResponse> call, Response<AdminStudentsResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AdminStudentsResponse studentsResponse = response.body();
                    studentsList.clear();
                    studentsList.addAll(studentsResponse.getData());
                    adapter.notifyDataSetChanged();
                    
                    // Update pagination info
                    if (studentsResponse.getPagination() != null) {
                        totalPages = studentsResponse.getPagination().getPages();
                        updatePaginationUI();
                    }
                } else {
                    Toast.makeText(AdminStudentsActivity.this, "Không thể tải danh sách sinh viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminStudentsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load students failed", t);
                Toast.makeText(AdminStudentsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePaginationUI() {
        tvPageInfo.setText("Trang " + currentPage + " / " + totalPages);
        btnPrevious.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private void openAddStudent() {
        Intent intent = new Intent(this, AddEditStudentActivity.class);
        intent.putExtra("mode", "add");
        startActivityForResult(intent, 100);
    }

    private void openEditStudent(AdminStudent student) {
        Intent intent = new Intent(this, AddEditStudentActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("student", student);
        startActivityForResult(intent, 101);
    }

    private void confirmDeleteStudent(AdminStudent student) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sinh viên " + student.getStudentFullName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteStudent(student))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteStudent(AdminStudent student) {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.deleteStudent(student.getStudentId()).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminStudentsActivity.this, "Đã xóa sinh viên thành công", Toast.LENGTH_SHORT).show();
                    loadStudents(); // Reload list
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể xóa sinh viên";
                    Toast.makeText(AdminStudentsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Delete student failed", t);
                Toast.makeText(AdminStudentsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadStudents(); // Reload list after add/edit
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 