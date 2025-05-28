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

public class AdminTeachersActivity extends AppCompatActivity {
    private static final String TAG = "AdminTeachersActivity";
    
    private RecyclerView rvTeachers;
    private TeachersAdapter adapter;
    private List<Teacher> teachersList = new ArrayList<>();
    private List<Teacher> allTeachers = new ArrayList<>();
    
    private EditText etSearch;
    private Spinner spDepartmentFilter;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    private Button btnPrevious, btnNext;
    private TextView tvPageInfo;
    
    private ApiService apiService;
    private List<Department> departmentsList = new ArrayList<>();
    
    private String currentSearch = "";
    private Integer currentDepartmentFilter = null;
    private int currentPage = 1;
    private final int pageSize = 10;
    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_teachers);
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadDepartments();
        loadTeachers();
    }

    private void initViews() {
        rvTeachers = findViewById(R.id.rvTeachers);
        etSearch = findViewById(R.id.etSearch);
        spDepartmentFilter = findViewById(R.id.spDepartmentFilter);
        progressBar = findViewById(R.id.progressBar);
        fabAdd = findViewById(R.id.fabAdd);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        tvPageInfo = findViewById(R.id.tvPageInfo);
        
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Thiết lập toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý giáo viên");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new TeachersAdapter(teachersList, new TeachersAdapter.OnTeacherClickListener() {
            @Override
            public void onEditClick(Teacher teacher) {
                openEditTeacher(teacher);
            }

            @Override
            public void onDeleteClick(Teacher teacher) {
                confirmDeleteTeacher(teacher);
            }
        });
        
        rvTeachers.setLayoutManager(new LinearLayoutManager(this));
        rvTeachers.setAdapter(adapter);
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
                loadTeachers();
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
                currentPage = 1;
                loadTeachers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Add new teacher
        fabAdd.setOnClickListener(v -> openAddTeacher());

        // Pagination
        btnPrevious.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadTeachers();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadTeachers();
            }
        });
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

    private void loadTeachers() {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.getTeachers(currentPage, pageSize, currentSearch, currentDepartmentFilter)
                .enqueue(new Callback<TeachersResponse>() {
            @Override
            public void onResponse(Call<TeachersResponse> call, Response<TeachersResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    TeachersResponse teachersResponse = response.body();
                    
                    teachersList.clear();
                    teachersList.addAll(teachersResponse.getData());
                    adapter.notifyDataSetChanged();
                    
                    // Update pagination info
                    if (teachersResponse.getPagination() != null) {
                        totalPages = teachersResponse.getPagination().getTotalPages();
                        updatePaginationUI();
                    }
                } else {
                    Toast.makeText(AdminTeachersActivity.this, "Không thể tải danh sách giáo viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeachersResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load teachers failed", t);
                Toast.makeText(AdminTeachersActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePaginationUI() {
        tvPageInfo.setText("Trang " + currentPage + "/" + totalPages);
        btnPrevious.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private void openAddTeacher() {
        Intent intent = new Intent(this, AddEditTeacherActivity.class);
        intent.putExtra("mode", "add");
        startActivityForResult(intent, 100);
    }

    private void openEditTeacher(Teacher teacher) {
        Intent intent = new Intent(this, AddEditTeacherActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("teacher", teacher);
        startActivityForResult(intent, 101);
    }

    private void confirmDeleteTeacher(Teacher teacher) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa giáo viên " + teacher.getTeacherFullName() + "?\n\nTài khoản đăng nhập của giáo viên cũng sẽ bị xóa.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteTeacher(teacher))
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteTeacher(Teacher teacher) {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.deleteTeacher(teacher.getTeacherId()).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminTeachersActivity.this, "Xóa giáo viên thành công", Toast.LENGTH_SHORT).show();
                    loadTeachers(); // Reload list
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể xóa giáo viên";
                    Toast.makeText(AdminTeachersActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Delete teacher failed", t);
                Toast.makeText(AdminTeachersActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadTeachers(); // Reload list after add/edit
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 