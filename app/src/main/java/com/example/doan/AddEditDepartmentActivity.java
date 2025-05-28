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

import java.io.Serializable;

public class AddEditDepartmentActivity extends AppCompatActivity {
    private static final String TAG = "AddEditDepartmentActivity";
    
    private TextInputLayout tilDepartmentCode, tilDepartmentName;
    private TextInputEditText etDepartmentCode, etDepartmentName;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private String mode; // "add" hoặc "edit"
    private Department editingDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_department);
        
        initViews();
        setupToolbar();
        getIntentData();
        setupClickListeners();
    }

    private void initViews() {
        tilDepartmentCode = findViewById(R.id.tilDepartmentCode);
        tilDepartmentName = findViewById(R.id.tilDepartmentName);
        etDepartmentCode = findViewById(R.id.etDepartmentCode);
        etDepartmentName = findViewById(R.id.etDepartmentName);
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
            editingDepartment = (Department) getIntent().getSerializableExtra("department");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Sửa thông tin khoa");
            }
            btnSave.setText("Cập nhật");
            populateFormForEdit();
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thêm khoa mới");
            }
            btnSave.setText("Thêm");
        }
    }

    private void populateFormForEdit() {
        if (editingDepartment == null) return;
        
        etDepartmentCode.setText(editingDepartment.getDepartmentCode());
        etDepartmentName.setText(editingDepartment.getDepartmentName());
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                if ("edit".equals(mode)) {
                    updateDepartment();
                } else {
                    createDepartment();
                }
            }
        });

        btnCancel.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Reset errors
        tilDepartmentCode.setError(null);
        tilDepartmentName.setError(null);

        // Validate department code
        String departmentCode = etDepartmentCode.getText().toString().trim();
        if (TextUtils.isEmpty(departmentCode)) {
            tilDepartmentCode.setError("Vui lòng nhập mã khoa");
            etDepartmentCode.requestFocus();
            isValid = false;
        } else if (departmentCode.length() < 2 || departmentCode.length() > 10) {
            tilDepartmentCode.setError("Mã khoa phải từ 2-10 ký tự");
            etDepartmentCode.requestFocus();
            isValid = false;
        }

        // Validate department name
        String departmentName = etDepartmentName.getText().toString().trim();
        if (TextUtils.isEmpty(departmentName)) {
            tilDepartmentName.setError("Vui lòng nhập tên khoa");
            etDepartmentName.requestFocus();
            isValid = false;
        } else if (departmentName.length() < 5 || departmentName.length() > 100) {
            tilDepartmentName.setError("Tên khoa phải từ 5-100 ký tự");
            etDepartmentName.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void createDepartment() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        String departmentCode = etDepartmentCode.getText().toString().trim();
        String departmentName = etDepartmentName.getText().toString().trim();

        CreateDepartmentRequest request = new CreateDepartmentRequest(departmentName, departmentCode);

        apiService.createDepartment(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditDepartmentActivity.this, "Thêm khoa thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể thêm khoa";
                    Toast.makeText(AddEditDepartmentActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Create department failed", t);
                Toast.makeText(AddEditDepartmentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDepartment() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        String departmentCode = etDepartmentCode.getText().toString().trim();
        String departmentName = etDepartmentName.getText().toString().trim();

        UpdateDepartmentRequest request = new UpdateDepartmentRequest(
                editingDepartment.getDepartmentId(),
                departmentName,
                departmentCode
        );

        apiService.updateDepartment(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditDepartmentActivity.this, "Cập nhật khoa thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể cập nhật khoa";
                    Toast.makeText(AddEditDepartmentActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Update department failed", t);
                Toast.makeText(AddEditDepartmentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 