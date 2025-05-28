package com.example.doan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEditTeacherActivity extends AppCompatActivity {
    private static final String TAG = "AddEditTeacherActivity";
    
    private TextInputLayout tilTeacherName, tilDateOfBirth, tilEmail, tilUsername, tilPassword;
    private TextInputEditText etTeacherName, etDateOfBirth, etEmail, etUsername, etPassword;
    private Spinner spDepartment;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private String mode; // "add" hoặc "edit"
    private Teacher editingTeacher;
    private List<Department> departmentsList = new ArrayList<>();
    private int selectedDepartmentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_teacher);
        
        initViews();
        setupToolbar();
        getIntentData();
        setupClickListeners();
        loadDepartments();
    }

    private void initViews() {
        tilTeacherName = findViewById(R.id.tilTeacherName);
        tilDateOfBirth = findViewById(R.id.tilDateOfBirth);
        tilEmail = findViewById(R.id.tilEmail);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        
        etTeacherName = findViewById(R.id.etTeacherName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        
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
            editingTeacher = (Teacher) getIntent().getSerializableExtra("teacher");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Sửa thông tin giáo viên");
            }
            btnSave.setText("Cập nhật");
            
            // Ẩn trường password khi edit
            tilPassword.setVisibility(View.GONE);
            
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thêm giáo viên mới");
            }
            btnSave.setText("Thêm");
        }
    }

    private void setupClickListeners() {
        // Date picker
        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                if ("edit".equals(mode)) {
                    updateTeacher();
                } else {
                    createTeacher();
                }
            }
        });

        btnCancel.setOnClickListener(v -> onBackPressed());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    etDateOfBirth.setText(date);
                }, year, month, day);

        datePickerDialog.show();
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
                    if ("edit".equals(mode) && editingTeacher != null) {
                        populateFormForEdit();
                    }
                } else {
                    Toast.makeText(AddEditTeacherActivity.this, "Không thể tải danh sách khoa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DepartmentsResponse> call, Throwable t) {
                Log.e(TAG, "Load departments failed", t);
                Toast.makeText(AddEditTeacherActivity.this, "Lỗi kết nối khi tải khoa", Toast.LENGTH_SHORT).show();
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
        if (editingTeacher == null) return;
        
        etTeacherName.setText(editingTeacher.getTeacherFullName());
        etDateOfBirth.setText(editingTeacher.getDateOfBirth());
        etEmail.setText(editingTeacher.getTeacherEmail());
        etUsername.setText(editingTeacher.getUsername());
        
        // Set selected department
        for (int i = 0; i < departmentsList.size(); i++) {
            if (departmentsList.get(i).getDepartmentId() == editingTeacher.getDepartmentId()) {
                spDepartment.setSelection(i + 1); // +1 vì có "Chọn khoa" ở vị trí 0
                break;
            }
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Reset errors
        tilTeacherName.setError(null);
        tilEmail.setError(null);
        tilUsername.setError(null);
        tilPassword.setError(null);

        // Validate teacher name
        String teacherName = etTeacherName.getText().toString().trim();
        if (TextUtils.isEmpty(teacherName)) {
            tilTeacherName.setError("Vui lòng nhập họ tên giáo viên");
            etTeacherName.requestFocus();
            isValid = false;
        }

        // Validate email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            isValid = false;
        }

        // Validate username
        String username = etUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Vui lòng nhập tên đăng nhập");
            etUsername.requestFocus();
            isValid = false;
        }

        // Validate password (chỉ khi thêm mới)
        if ("add".equals(mode)) {
            String password = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                tilPassword.setError("Vui lòng nhập mật khẩu");
                etPassword.requestFocus();
                isValid = false;
            } else if (password.length() < 6) {
                tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
                etPassword.requestFocus();
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

    private void createTeacher() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        String teacherName = etTeacherName.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        CreateTeacherRequest request = new CreateTeacherRequest(
                teacherName, dateOfBirth, email, selectedDepartmentId, username, password
        );

        apiService.createTeacher(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditTeacherActivity.this, "Thêm giáo viên thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể thêm giáo viên";
                    Toast.makeText(AddEditTeacherActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Create teacher failed", t);
                Toast.makeText(AddEditTeacherActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTeacher() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        String teacherName = etTeacherName.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        UpdateTeacherRequest request = new UpdateTeacherRequest(
                editingTeacher.getTeacherId(), teacherName, dateOfBirth,
                email, selectedDepartmentId
        );

        apiService.updateTeacher(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditTeacherActivity.this, "Cập nhật giáo viên thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể cập nhật giáo viên";
                    Toast.makeText(AddEditTeacherActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Update teacher failed", t);
                Toast.makeText(AddEditTeacherActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 