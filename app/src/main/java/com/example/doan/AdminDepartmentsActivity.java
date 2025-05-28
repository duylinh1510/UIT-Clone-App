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

public class AdminDepartmentsActivity extends AppCompatActivity {
    private static final String TAG = "AdminDepartmentsActivity";
    
    private RecyclerView rvDepartments;
    private DepartmentsAdapter adapter;
    private List<Department> departmentsList = new ArrayList<>();
    
    private EditText etSearch;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    
    private ApiService apiService;
    private String currentSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_departments);
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadDepartments();
    }

    private void initViews() {
        rvDepartments = findViewById(R.id.rvDepartments);
        etSearch = findViewById(R.id.etSearch);
        progressBar = findViewById(R.id.progressBar);
        fabAdd = findViewById(R.id.fabAdd);
        
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Thiết lập toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý khoa");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new DepartmentsAdapter(departmentsList, new DepartmentsAdapter.OnDepartmentClickListener() {
            @Override
            public void onEditClick(Department department) {
                openEditDepartment(department);
            }

            @Override
            public void onDeleteClick(Department department) {
                confirmDeleteDepartment(department);
            }

            @Override
            public void onViewClassesClick(Department department) {
                openDepartmentClasses(department);
            }
        });
        
        rvDepartments.setLayoutManager(new LinearLayoutManager(this));
        rvDepartments.setAdapter(adapter);
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
                filterDepartments();
            }
        });

        // Add new department
        fabAdd.setOnClickListener(v -> openAddDepartment());
    }

    private void loadDepartments() {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.getDepartments().enqueue(new Callback<DepartmentsResponse>() {
            @Override
            public void onResponse(Call<DepartmentsResponse> call, Response<DepartmentsResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    departmentsList.clear();
                    departmentsList.addAll(response.body().getData());
                    filterDepartments();
                } else {
                    Toast.makeText(AdminDepartmentsActivity.this, "Không thể tải danh sách khoa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DepartmentsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load departments failed", t);
                Toast.makeText(AdminDepartmentsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterDepartments() {
        List<Department> filteredList = new ArrayList<>();
        
        if (currentSearch.isEmpty()) {
            filteredList.addAll(departmentsList);
        } else {
            for (Department dept : departmentsList) {
                if (dept.getDepartmentName().toLowerCase().contains(currentSearch.toLowerCase()) ||
                    dept.getDepartmentCode().toLowerCase().contains(currentSearch.toLowerCase())) {
                    filteredList.add(dept);
                }
            }
        }
        
        adapter.updateList(filteredList);
        
        if (filteredList.isEmpty() && !departmentsList.isEmpty()) {
            showEmptySearchState();
        }
    }

    private void showEmptySearchState() {
        Toast.makeText(this, "Không tìm thấy khoa nào", Toast.LENGTH_SHORT).show();
    }

    private void openAddDepartment() {
        Intent intent = new Intent(this, AddEditDepartmentActivity.class);
        intent.putExtra("mode", "add");
        startActivityForResult(intent, 100);
    }

    private void openEditDepartment(Department department) {
        Intent intent = new Intent(this, AddEditDepartmentActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("department", department);
        startActivityForResult(intent, 101);
    }

    private void openDepartmentClasses(Department department) {
        Intent intent = new Intent(this, DepartmentClassesActivity.class);
        intent.putExtra("department", department);
        startActivity(intent);
    }

    private void confirmDeleteDepartment(Department department) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa khoa " + department.getDepartmentName() + "?\n\nCảnh báo: Tất cả sinh viên thuộc khoa này sẽ bị ảnh hưởng.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteDepartment(department))
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteDepartment(Department department) {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.deleteDepartment(department.getDepartmentId()).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminDepartmentsActivity.this, "Xóa khoa thành công", Toast.LENGTH_SHORT).show();
                    loadDepartments(); // Reload list
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể xóa khoa";
                    Toast.makeText(AdminDepartmentsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Delete department failed", t);
                Toast.makeText(AdminDepartmentsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadDepartments(); // Reload list after add/edit
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 