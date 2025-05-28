package com.example.doan;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class AddEditProgramClassActivity extends AppCompatActivity {
    private static final String TAG = "AddEditProgramClassActivity";
    
    private EditText etClassCode, etYear;
    private Spinner spTeacher;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private String mode; // "add" or "edit"
    private Department department;
    private ProgramClass programClass;
    private List<Teacher> teachersList = new ArrayList<>();
    private ArrayAdapter<Teacher> teachersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_program_class);
        
        // Get data from intent
        mode = getIntent().getStringExtra("mode");
        department = (Department) getIntent().getSerializableExtra("department");
        programClass = (ProgramClass) getIntent().getSerializableExtra("program_class");
        
        if (department == null) {
            Toast.makeText(this, "Không tìm thấy thông tin khoa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupSpinners();
        setupClickListeners();
        loadTeachers();
        
        if ("edit".equals(mode) && programClass != null) {
            populateFields();
        }
    }

    private void initViews() {
        etClassCode = findViewById(R.id.etClassCode);
        etYear = findViewById(R.id.etYear);
        spTeacher = findViewById(R.id.spTeacher);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
        
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Setup toolbar
        if (getSupportActionBar() != null) {
            String title = "add".equals(mode) ? "Thêm lớp mới" : "Sửa thông tin lớp";
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupSpinners() {
        // Setup teachers spinner
        teachersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teachersList);
        teachersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTeacher.setAdapter(teachersAdapter);
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveClass());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadTeachers() {
        progressBar.setVisibility(View.VISIBLE);
        
        Log.d(TAG, "Loading teachers for department: " + department.getDepartmentId());
        
        // Load teachers from the same department
        apiService.getTeachers(1, 1000, "", department.getDepartmentId()).enqueue(new Callback<TeachersResponse>() {
            @Override
            public void onResponse(Call<TeachersResponse> call, Response<TeachersResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                Log.d(TAG, "Teachers API response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Teachers API success: " + response.body().isSuccess());
                    
                    if (response.body().isSuccess()) {
                        teachersList.clear();
                        
                        // Add empty option
                        Teacher emptyTeacher = new Teacher();
                        emptyTeacher.setTeacherId(0);
                        emptyTeacher.setTeacherFullName("-- Chọn giáo viên chủ nhiệm --");
                        teachersList.add(emptyTeacher);
                        
                        // Add teachers from API
                        List<Teacher> apiTeachers = response.body().getData();
                        if (apiTeachers != null) {
                            Log.d(TAG, "Found " + apiTeachers.size() + " teachers");
                            teachersList.addAll(apiTeachers);
                        } else {
                            Log.w(TAG, "Teachers data is null");
                        }
                        
                        teachersAdapter.notifyDataSetChanged();
                        
                        Log.d(TAG, "Total teachers in spinner: " + teachersList.size());
                        
                        if ("edit".equals(mode) && programClass != null) {
                            selectTeacher();
                        }
                    } else {
                        String message = response.body().getMessage();
                        Log.e(TAG, "Teachers API error: " + message);
                        Toast.makeText(AddEditProgramClassActivity.this, "Lỗi API: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Teachers API response not successful");
                    Toast.makeText(AddEditProgramClassActivity.this, "Không thể tải danh sách giáo viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeachersResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load teachers failed", t);
                Toast.makeText(AddEditProgramClassActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                
                // Add at least the empty option so spinner doesn't crash
                teachersList.clear();
                Teacher emptyTeacher = new Teacher();
                emptyTeacher.setTeacherId(0);
                emptyTeacher.setTeacherFullName("-- Không thể tải giáo viên --");
                teachersList.add(emptyTeacher);
                teachersAdapter.notifyDataSetChanged();
            }
        });
    }

    private void populateFields() {
        if (programClass != null) {
            etClassCode.setText(programClass.getProgramClassCode());
            etYear.setText(programClass.getYear());
        }
    }

    private void selectTeacher() {
        if (programClass != null && programClass.getTeacherId() > 0) {
            for (int i = 0; i < teachersList.size(); i++) {
                if (teachersList.get(i).getTeacherId() == programClass.getTeacherId()) {
                    spTeacher.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveClass() {
        if (!validateInput()) {
            return;
        }
        
        String classCode = etClassCode.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        Teacher selectedTeacher = (Teacher) spTeacher.getSelectedItem();
        int teacherId = selectedTeacher != null && selectedTeacher.getTeacherId() > 0 ? selectedTeacher.getTeacherId() : 0;
        
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        if ("add".equals(mode)) {
            CreateProgramClassRequest request = new CreateProgramClassRequest(classCode, year, teacherId, department.getDepartmentId());
            apiService.createProgramClass(request).enqueue(saveCallback);
        } else {
            UpdateProgramClassRequest request = new UpdateProgramClassRequest(programClass.getClassId(), classCode, year, teacherId, department.getDepartmentId());
            apiService.updateProgramClass(request).enqueue(saveCallback);
        }
    }

    private final Callback<AdminResponse> saveCallback = new Callback<AdminResponse>() {
        @Override
        public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
            
            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                String message = "add".equals(mode) ? "Thêm lớp thành công" : "Cập nhật lớp thành công";
                Toast.makeText(AddEditProgramClassActivity.this, message, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                String message = response.body() != null ? response.body().getMessage() : "Có lỗi xảy ra";
                Toast.makeText(AddEditProgramClassActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<AdminResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
            Log.e(TAG, "Save class failed", t);
            Toast.makeText(AddEditProgramClassActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private boolean validateInput() {
        String classCode = etClassCode.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        
        if (classCode.isEmpty()) {
            etClassCode.setError("Vui lòng nhập mã lớp");
            etClassCode.requestFocus();
            return false;
        }
        
        if (year.isEmpty()) {
            etYear.setError("Vui lòng nhập năm học");
            etYear.requestFocus();
            return false;
        }
        
        try {
            int yearInt = Integer.parseInt(year);
            if (yearInt < 2000 || yearInt > 2050) {
                etYear.setError("Năm học không hợp lệ (2000-2050)");
                etYear.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etYear.setError("Năm học phải là số");
            etYear.requestFocus();
            return false;
        }
        
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 