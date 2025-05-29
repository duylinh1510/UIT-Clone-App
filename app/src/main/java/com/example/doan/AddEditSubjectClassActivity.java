package com.example.doan;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class AddEditSubjectClassActivity extends AppCompatActivity {
    private static final String TAG = "AddEditSubjectClassActivity";
    
    private EditText etSubjectClassCode, etSemester;
    private Spinner spTeacher;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private List<Teacher> teachersList = new ArrayList<>();
    
    private String mode; // "add" hoặc "edit"
    private Subject subject;
    private SubjectClass subjectClass; // Chỉ sử dụng khi edit
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_subject_class);
        
        // Nhận dữ liệu từ intent
        mode = getIntent().getStringExtra("mode");
        subject = (Subject) getIntent().getSerializableExtra("subject");
        if ("edit".equals(mode)) {
            subjectClass = (SubjectClass) getIntent().getSerializableExtra("subject_class");
        }
        
        if (subject == null || mode == null) {
            Toast.makeText(this, "Lỗi: Thiếu dữ liệu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        Log.d(TAG, "Mode: " + mode);
        if (subjectClass != null) {
            Log.d(TAG, "SubjectClass: " + subjectClass.getSubjectClassCode() + ", Teacher ID: " + subjectClass.getTeacherId());
        }
        
        initViews();
        setupClickListeners();
        loadTeachers();
    }
    
    private void initViews() {
        etSubjectClassCode = findViewById(R.id.etSubjectClassCode);
        etSemester = findViewById(R.id.etSemester);
        spTeacher = findViewById(R.id.spTeacher);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
        
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Thiết lập toolbar
        if (getSupportActionBar() != null) {
            String title = mode.equals("add") ? "Thêm lớp học" : "Chỉnh sửa lớp học";
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Hiển thị thông tin môn học
        TextView tvSubjectInfo = findViewById(R.id.tvSubjectInfo);
        tvSubjectInfo.setText(String.format("Môn học: %s - %s", 
            subject.getSubjectCode(), subject.getName()));
    }
    
    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveSubjectClass());
        btnCancel.setOnClickListener(v -> finish());
    }
    
    private void loadTeachers() {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.getTeachers(1, 1000, "", null).enqueue(new Callback<TeachersResponse>() {
            @Override
            public void onResponse(Call<TeachersResponse> call, Response<TeachersResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    teachersList.clear();
                    teachersList.addAll(response.body().getData());
                    setupTeacherSpinner();
                    
                    if (mode.equals("edit") && subjectClass != null) {
                        fillFormWithData();
                    }
                } else {
                    Toast.makeText(AddEditSubjectClassActivity.this, 
                        "Không thể tải danh sách giáo viên", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<TeachersResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load teachers failed", t);
                Toast.makeText(AddEditSubjectClassActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupTeacherSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Chọn giáo viên (Tùy chọn)");
        
        for (Teacher teacher : teachersList) {
            spinnerItems.add(teacher.getTeacherFullName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTeacher.setAdapter(adapter);
    }
    
    private void fillFormWithData() {
        if (subjectClass == null) return;
        
        etSubjectClassCode.setText(subjectClass.getSubjectClassCode());
        etSemester.setText(subjectClass.getSemester());
        
        // Chọn giáo viên trong spinner
        if (subjectClass.getTeacherId() > 0) {
            for (int i = 0; i < teachersList.size(); i++) {
                if (teachersList.get(i).getTeacherId() == subjectClass.getTeacherId()) {
                    spTeacher.setSelection(i + 1); // +1 vì có item "Chọn giáo viên" ở đầu
                    break;
                }
            }
        }
    }
    
    private void saveSubjectClass() {
        Log.d(TAG, "saveSubjectClass called, mode: " + mode);
        
        // Validate input
        String subjectClassCode = etSubjectClassCode.getText().toString().trim();
        String semester = etSemester.getText().toString().trim();
        
        Log.d(TAG, "Input - Code: " + subjectClassCode + ", Semester: " + semester);
        
        if (TextUtils.isEmpty(subjectClassCode)) {
            etSubjectClassCode.setError("Vui lòng nhập mã lớp");
            etSubjectClassCode.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(semester)) {
            etSemester.setError("Vui lòng nhập học kỳ");
            etSemester.requestFocus();
            return;
        }
        
        // Lấy teacher_id từ spinner
        Integer teacherId = null;
        int selectedTeacherIndex = spTeacher.getSelectedItemPosition();
        Log.d(TAG, "Selected teacher index: " + selectedTeacherIndex + ", Teachers list size: " + teachersList.size());
        
        if (selectedTeacherIndex > 0 && selectedTeacherIndex <= teachersList.size()) { 
            teacherId = teachersList.get(selectedTeacherIndex - 1).getTeacherId();
            Log.d(TAG, "Selected teacher ID: " + teacherId);
        }
        
        if ("add".equals(mode)) {
            createSubjectClass(subjectClassCode, semester, teacherId);
        } else if ("edit".equals(mode)) {
            updateSubjectClass(subjectClassCode, semester, teacherId);
        }
    }
    
    private void createSubjectClass(String subjectClassCode, String semester, Integer teacherId) {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        CreateSubjectClassRequest request = new CreateSubjectClassRequest(
            subjectClassCode, semester, subject.getSubjectId(), teacherId);
        
        apiService.createSubjectClass(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditSubjectClassActivity.this, 
                        "Thêm lớp học thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Thêm lớp học thất bại";
                    Toast.makeText(AddEditSubjectClassActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Create subject class failed", t);
                Toast.makeText(AddEditSubjectClassActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateSubjectClass(String subjectClassCode, String semester, Integer teacherId) {
        Log.d(TAG, "updateSubjectClass called with: Code=" + subjectClassCode + ", Semester=" + semester + ", TeacherID=" + teacherId);
        
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        UpdateSubjectClassRequest request = new UpdateSubjectClassRequest(
            subjectClass.getSubjectClassId(), subjectClassCode, semester, teacherId);
        
        Log.d(TAG, "Sending update request for subject class ID: " + subjectClass.getSubjectClassId());
        
        apiService.updateSubjectClass(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                
                Log.d(TAG, "Update response received - Success: " + response.isSuccessful());
                if (response.body() != null) {
                    Log.d(TAG, "Response body success: " + response.body().isSuccess());
                    Log.d(TAG, "Response message: " + response.body().getMessage());
                }
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditSubjectClassActivity.this, 
                        "Cập nhật lớp học thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Cập nhật lớp học thất bại";
                    Toast.makeText(AddEditSubjectClassActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Update subject class failed", t);
                Toast.makeText(AddEditSubjectClassActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 