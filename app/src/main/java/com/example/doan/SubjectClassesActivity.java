package com.example.doan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class SubjectClassesActivity extends AppCompatActivity {
    private static final String TAG = "SubjectClassesActivity";
    
    private RecyclerView rvSubjectClasses;
    private SubjectClassesAdapter adapter;
    private List<SubjectClass> subjectClassesList = new ArrayList<>();
    
    private ProgressBar progressBar;
    private TextView tvSubjectInfo;
    private FloatingActionButton fabAdd;
    
    private ApiService apiService;
    private Subject subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_classes);
        
        // Nhận subject từ intent
        subject = (Subject) getIntent().getSerializableExtra("subject");
        if (subject == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin môn học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadSubjectClasses();
    }

    private void initViews() {
        rvSubjectClasses = findViewById(R.id.rvSubjectClasses);
        progressBar = findViewById(R.id.progressBar);
        tvSubjectInfo = findViewById(R.id.tvSubjectInfo);
        fabAdd = findViewById(R.id.fabAdd);
        
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Thiết lập toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Các lớp môn học");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Hiển thị thông tin môn học
        tvSubjectInfo.setText(String.format("%s - %s (%d tín chỉ)", 
            subject.getSubjectCode(), subject.getName(), subject.getCredits()));
    }

    private void setupRecyclerView() {
        adapter = new SubjectClassesAdapter(subjectClassesList, new SubjectClassesAdapter.OnSubjectClassClickListener() {
            @Override
            public void onEditClassClick(SubjectClass subjectClass) {
                openEditSubjectClass(subjectClass);
            }

            @Override
            public void onDeleteClassClick(SubjectClass subjectClass) {
                confirmDeleteSubjectClass(subjectClass);
            }
        });
        
        rvSubjectClasses.setLayoutManager(new LinearLayoutManager(this));
        rvSubjectClasses.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Add new subject class
        fabAdd.setOnClickListener(v -> openAddSubjectClass());
    }

    private void loadSubjectClasses() {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.getSubjectClasses(null, subject.getSubjectId()).enqueue(new Callback<SubjectClassesResponse>() {
            @Override
            public void onResponse(Call<SubjectClassesResponse> call, Response<SubjectClassesResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    subjectClassesList.clear();
                    subjectClassesList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    
                    if (subjectClassesList.isEmpty()) {
                        showEmptyState();
                    }
                } else {
                    Toast.makeText(SubjectClassesActivity.this, "Không thể tải danh sách lớp học", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SubjectClassesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load subject classes failed", t);
                Toast.makeText(SubjectClassesActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        Toast.makeText(this, "Môn học này chưa có lớp nào được mở", Toast.LENGTH_SHORT).show();
    }

    private void openAddSubjectClass() {
        Intent intent = new Intent(this, AddEditSubjectClassActivity.class);
        intent.putExtra("mode", "add");
        intent.putExtra("subject", subject);
        startActivityForResult(intent, 100);
    }

    private void openEditSubjectClass(SubjectClass subjectClass) {
        Intent intent = new Intent(this, AddEditSubjectClassActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("subject", subject);
        intent.putExtra("subject_class", subjectClass);
        startActivityForResult(intent, 101);
    }

    private void confirmDeleteSubjectClass(SubjectClass subjectClass) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa lớp " + subjectClass.getSubjectClassCode() + "?\n\nCảnh báo: Tất cả điểm và thời khóa biểu liên quan sẽ bị ảnh hưởng.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteSubjectClass(subjectClass))
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteSubjectClass(SubjectClass subjectClass) {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.deleteSubjectClass(subjectClass.getSubjectClassId()).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(SubjectClassesActivity.this, "Xóa lớp học thành công", Toast.LENGTH_SHORT).show();
                    loadSubjectClasses(); // Reload list
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể xóa lớp học";
                    Toast.makeText(SubjectClassesActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Delete subject class failed", t);
                Toast.makeText(SubjectClassesActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadSubjectClasses(); // Reload list after add/edit
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 