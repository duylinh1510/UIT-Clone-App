package com.example.doan;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class AddEditSubjectActivity extends AppCompatActivity {
    private static final String TAG = "AddEditSubjectActivity";
    
    private TextInputLayout tilSubjectCode, tilSubjectName, tilCredits;
    private TextInputEditText etSubjectCode, etSubjectName, etCredits;
    private Spinner spDepartment;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private String mode; // "add" hoặc "edit"
    private Subject editingSubject;
    private List<Department> departmentsList = new ArrayList<>();
    private int selectedDepartmentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_subject);
        
        initViews();
        setupToolbar();
        getIntentData();
        setupClickListeners();
        loadDepartments();
    }

    private void initViews() {
        tilSubjectCode = findViewById(R.id.tilSubjectCode);
        tilSubjectName = findViewById(R.id.tilSubjectName);
        tilCredits = findViewById(R.id.tilCredits);
        
        etSubjectCode = findViewById(R.id.etSubjectCode);
        etSubjectName = findViewById(R.id.etSubjectName);
        etCredits = findViewById(R.id.etCredits);
        
        spDepartment = findViewById(R.id.spDepartment);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
        
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getIntentData() {
        mode = getIntent().getStringExtra("mode");
        
        if ("edit".equals(mode)) {
            editingSubject = (Subject) getIntent().getSerializableExtra("subject");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Sửa thông tin môn học");
            }
            btnSave.setText("Cập nhật");
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thêm môn học mới");
            }
            btnSave.setText("Thêm");
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                if ("edit".equals(mode)) {
                    updateSubject();
                } else {
                    createSubject();
                }
            }
        });

        btnCancel.setOnClickListener(v -> onBackPressed());
    }

    private void loadDepartments() {
        apiService.getDepartments().enqueue(new Callback<DepartmentsResponse>() {
            @Override
            public void onResponse(Call<DepartmentsResponse> call, Response<DepartmentsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    departmentsList.clear();
                    departmentsList.addAll(response.body().getData());
                    setupDepartmentSpinner();
                    
                    // Nếu đang edit, set selected department
                    if ("edit".equals(mode) && editingSubject != null) {
                        populateFormForEdit();
                    }
                } else {
                    Toast.makeText(AddEditSubjectActivity.this, "Không thể tải danh sách khoa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DepartmentsResponse> call, Throwable t) {
                Log.e(TAG, "Load departments failed", t);
                Toast.makeText(AddEditSubjectActivity.this, "Lỗi kết nối khi tải khoa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDepartmentSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Chọn khoa");
        
        for (Department dept : departmentsList) {
            spinnerItems.add(dept.getDepartmentName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartment.setAdapter(adapter);
        
        spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedDepartmentId = departmentsList.get(position - 1).getDepartmentId();
                } else {
                    selectedDepartmentId = -1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDepartmentId = -1;
            }
        });
    }

    private void populateFormForEdit() {
        if (editingSubject == null) return;
        
        etSubjectCode.setText(editingSubject.getSubjectCode());
        etSubjectName.setText(editingSubject.getName());
        etCredits.setText(String.valueOf(editingSubject.getCredits()));
        
        // Set selected department
        for (int i = 0; i < departmentsList.size(); i++) {
            if (departmentsList.get(i).getDepartmentId() == editingSubject.getDepartmentId()) {
                spDepartment.setSelection(i + 1); // +1 vì có "Chọn khoa" ở vị trí 0
                break;
            }
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Reset errors
        tilSubjectCode.setError(null);
        tilSubjectName.setError(null);
        tilCredits.setError(null);

        // Validate subject code
        String subjectCode = etSubjectCode.getText().toString().trim();
        if (TextUtils.isEmpty(subjectCode)) {
            tilSubjectCode.setError("Vui lòng nhập mã môn học");
            etSubjectCode.requestFocus();
            isValid = false;
        }

        // Validate subject name
        String subjectName = etSubjectName.getText().toString().trim();
        if (TextUtils.isEmpty(subjectName)) {
            tilSubjectName.setError("Vui lòng nhập tên môn học");
            etSubjectName.requestFocus();
            isValid = false;
        }

        // Validate credits
        String creditsStr = etCredits.getText().toString().trim();
        if (TextUtils.isEmpty(creditsStr)) {
            tilCredits.setError("Vui lòng nhập số tín chỉ");
            etCredits.requestFocus();
            isValid = false;
        } else {
            try {
                int credits = Integer.parseInt(creditsStr);
                if (credits < 1 || credits > 10) {
                    tilCredits.setError("Số tín chỉ phải từ 1-10");
                    etCredits.requestFocus();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilCredits.setError("Số tín chỉ không hợp lệ");
                etCredits.requestFocus();
                isValid = false;
            }
        }



        // Validate department
        if (selectedDepartmentId == -1) {
            Toast.makeText(this, "Vui lòng chọn khoa", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void createSubject() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        String subjectCode = etSubjectCode.getText().toString().trim();
        String subjectName = etSubjectName.getText().toString().trim();
        int credits = Integer.parseInt(etCredits.getText().toString().trim());

        CreateSubjectRequest request = new CreateSubjectRequest(
                subjectCode, subjectName, credits, selectedDepartmentId
        );

        apiService.createSubject(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditSubjectActivity.this, "Thêm môn học thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể thêm môn học";
                    Toast.makeText(AddEditSubjectActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Create subject failed", t);
                Toast.makeText(AddEditSubjectActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSubject() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        String subjectCode = etSubjectCode.getText().toString().trim();
        String subjectName = etSubjectName.getText().toString().trim();
        int credits = Integer.parseInt(etCredits.getText().toString().trim());

        UpdateSubjectRequest request = new UpdateSubjectRequest(
                editingSubject.getSubjectId(), subjectCode, subjectName, 
                credits, selectedDepartmentId
        );

        apiService.updateSubject(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditSubjectActivity.this, "Cập nhật môn học thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể cập nhật môn học";
                    Toast.makeText(AddEditSubjectActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Update subject failed", t);
                Toast.makeText(AddEditSubjectActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 