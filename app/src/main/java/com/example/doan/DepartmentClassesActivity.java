package com.example.doan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class DepartmentClassesActivity extends AppCompatActivity {
    private static final String TAG = "DepartmentClassesActivity";
    
    private RecyclerView rvClasses;
    private TextView tvDepartmentInfo, tvEmptyState;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    private DepartmentClassesAdapter adapter;
    private List<ProgramClass> classesList = new ArrayList<>();
    
    private ApiService apiService;
    private Department department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_classes);
        
        // Lấy thông tin khoa từ intent
        department = (Department) getIntent().getSerializableExtra("department");
        if (department == null) {
            Toast.makeText(this, "Không tìm thấy thông tin khoa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadDepartmentClasses();
    }

    private void initViews() {
        rvClasses = findViewById(R.id.rvClasses);
        tvDepartmentInfo = findViewById(R.id.tvDepartmentInfo);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        progressBar = findViewById(R.id.progressBar);
        fabAdd = findViewById(R.id.fabAdd);
        
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Thiết lập toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lớp thuộc khoa");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Hiển thị thông tin khoa
        tvDepartmentInfo.setText(String.format("Khoa: %s (%s)", 
            department.getDepartmentName(), department.getDepartmentCode()));
    }

    private void setupRecyclerView() {
        adapter = new DepartmentClassesAdapter(classesList, new DepartmentClassesAdapter.OnClassClickListener() {
            @Override
            public void onEditClick(ProgramClass programClass) {
                openEditClass(programClass);
            }

            @Override
            public void onDeleteClick(ProgramClass programClass) {
                confirmDeleteClass(programClass);
            }
        });
        rvClasses.setLayoutManager(new LinearLayoutManager(this));
        rvClasses.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Add new class
        fabAdd.setOnClickListener(v -> openAddClass());
    }

    private void loadDepartmentClasses() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
        
        apiService.getProgramClassesByDepartment(department.getDepartmentId()).enqueue(new Callback<ProgramClassesResponse>() {
            @Override
            public void onResponse(Call<ProgramClassesResponse> call, Response<ProgramClassesResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    classesList.clear();
                    classesList.addAll(response.body().getData());
                    adapter.updateList(classesList);
                    
                    if (classesList.isEmpty()) {
                        showEmptyState();
                    }
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể tải danh sách lớp";
                    Toast.makeText(DepartmentClassesActivity.this, message, Toast.LENGTH_SHORT).show();
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<ProgramClassesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load department classes failed", t);
                Toast.makeText(DepartmentClassesActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }

    private void openAddClass() {
        Intent intent = new Intent(this, AddEditProgramClassActivity.class);
        intent.putExtra("mode", "add");
        intent.putExtra("department", department);
        startActivityForResult(intent, 100);
    }

    private void openEditClass(ProgramClass programClass) {
        Intent intent = new Intent(this, AddEditProgramClassActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("department", department);
        intent.putExtra("program_class", programClass);
        startActivityForResult(intent, 101);
    }

    private void confirmDeleteClass(ProgramClass programClass) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa lớp " + programClass.getProgramClassCode() + "?\n\nCảnh báo: Tất cả sinh viên thuộc lớp này sẽ bị ảnh hưởng.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteClass(programClass))
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteClass(ProgramClass programClass) {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.deleteProgramClass(programClass.getClassId()).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(DepartmentClassesActivity.this, "Xóa lớp thành công", Toast.LENGTH_SHORT).show();
                    loadDepartmentClasses(); // Reload list
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể xóa lớp";
                    Toast.makeText(DepartmentClassesActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Delete class failed", t);
                Toast.makeText(DepartmentClassesActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        tvEmptyState.setVisibility(View.VISIBLE);
        tvEmptyState.setText(String.format("Khoa %s chưa có lớp nào", department.getDepartmentName()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadDepartmentClasses(); // Reload list after add/edit
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 