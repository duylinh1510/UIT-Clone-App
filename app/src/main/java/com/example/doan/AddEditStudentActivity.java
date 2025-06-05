package com.example.doan;

import android.app.DatePickerDialog;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditStudentActivity extends AppCompatActivity {
    private static final String TAG = "AddEditStudentActivity";
    
    private TextInputLayout tilStudentCode, tilFullName, tilEmail, tilAddress, tilUsername, tilPassword;
    private TextInputEditText etStudentCode, etFullName, etEmail, etAddress, etUsername, etPassword;
    private TextView tvBirthDate;
    private Spinner spDepartments, spClasses;
    private Button btnSave, btnCancel, btnSelectDate;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private List<Department> departmentsList = new ArrayList<>();
    private List<ProgramClass> classesList = new ArrayList<>();
    
    private String mode; // "add" hoặc "edit"
    private AdminStudent editingStudent;
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_student);

        initViews();           // Gán view từ XML
        setupToolbar();        // Gắn nút quay lại
        getIntentData();       // Lấy dữ liệu nếu ở chế độ edit
        loadDepartments();     // Gọi API lấy danh sách khoa
        setupClickListeners(); // Gắn sự kiện cho nút
    }

    private void initViews() {
        tilStudentCode = findViewById(R.id.tilStudentCode);
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilAddress = findViewById(R.id.tilAddress);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        
        etStudentCode = findViewById(R.id.etStudentCode);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        
        tvBirthDate = findViewById(R.id.tvBirthDate);
        spDepartments = findViewById(R.id.spDepartments);
        spClasses = findViewById(R.id.spClasses);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        progressBar = findViewById(R.id.progressBar);
        
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //Kiểm tra chế độ add hay edit
    private void getIntentData() {
        mode = getIntent().getStringExtra("mode");
        
        if ("edit".equals(mode)) {
            editingStudent = (AdminStudent) getIntent().getSerializableExtra("student");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Sửa thông tin sinh viên");
            }
            btnSave.setText("Cập nhật");
            tilPassword.setVisibility(View.GONE); // Không cho phép đổi mật khẩu khi edit
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thêm sinh viên mới");
            }
            btnSave.setText("Thêm");
        }
    }

    //Gọi API lấy danh sách khoa
    private void loadDepartments() {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.getDepartments().enqueue(new Callback<DepartmentsResponse>() {
            @Override
            public void onResponse(Call<DepartmentsResponse> call, Response<DepartmentsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    departmentsList = response.body().getData();
                    setupDepartmentSpinner();
                    
                    if ("edit".equals(mode) && editingStudent != null) {
                        populateFormForEdit();
                    }
                } else {
                    Toast.makeText(AddEditStudentActivity.this, "Không thể tải danh sách khoa", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<DepartmentsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load departments failed", t);
                Toast.makeText(AddEditStudentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cập nhật spinner khoa và xử lý sự kiện khi chọn (Tên khoa được đưa vào spinner.)
    private void setupDepartmentSpinner() {
        List<String> departmentNames = new ArrayList<>();
        departmentNames.add("Chọn khoa");
        for (Department dept : departmentsList) {
            departmentNames.add(dept.getDepartmentName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartments.setAdapter(adapter);
        
        spDepartments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Department selectedDept = departmentsList.get(position - 1);
                    loadClassesByDepartment(selectedDept.getDepartmentId());
                } else {
                    classesList.clear();
                    setupClassSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadClassesByDepartment(int departmentId) {
        // Gọi API để lấy danh sách lớp theo khoa
        apiService.getProgramClassesByDepartment(departmentId).enqueue(new Callback<ProgramClassesResponse>() {
            @Override
            public void onResponse(Call<ProgramClassesResponse> call, Response<ProgramClassesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    classesList.clear();
                    classesList.addAll(response.body().getData());
                    setupClassSpinner();
                } else {
                    // Fallback với dữ liệu mặc định nếu API lỗi
                    classesList.clear();
                    if (departmentId == 2) { // Hệ thống thông tin
                        classesList.add(new ProgramClass(3, "HTTT2022.1", "HTTT2022.1"));
                        classesList.add(new ProgramClass(4, "Hệ thống thông tin 2022.1", "Hệ thống thông tin 2022.1"));
                    } else { // Khoa CNTT
                        classesList.add(new ProgramClass(1, "CNTT2025A", "CNTT2025A"));
                        classesList.add(new ProgramClass(2, "CNTT2025B", "CNTT2025B"));
                    }
                    setupClassSpinner();
                }
            }

            @Override
            public void onFailure(Call<ProgramClassesResponse> call, Throwable t) {
                Log.e(TAG, "Load classes failed", t);
                // Fallback với dữ liệu mặc định
                classesList.clear();
                if (departmentId == 2) { // Hệ thống thông tin
                    classesList.add(new ProgramClass(3, "HTTT2022.1", "HTTT2022.1"));
                    classesList.add(new ProgramClass(4, "Hệ thống thông tin 2022.1", "Hệ thống thông tin 2022.1"));
                } else { // Khoa CNTT
                    classesList.add(new ProgramClass(1, "CNTT2025A", "CNTT2025A"));
                    classesList.add(new ProgramClass(2, "CNTT2025B", "CNTT2025B"));
                }
                setupClassSpinner();
            }
        });
    }
    
    private void loadClassesByDepartmentAndSelect(int departmentId, String classCode) {
        // Gọi API để lấy danh sách lớp theo khoa và select class hiện tại
        apiService.getProgramClassesByDepartment(departmentId).enqueue(new Callback<ProgramClassesResponse>() {
            @Override
            public void onResponse(Call<ProgramClassesResponse> call, Response<ProgramClassesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    classesList.clear();
                    classesList.addAll(response.body().getData());
                    setupClassSpinner();
                    
                    // Select current class
                    for (int i = 0; i < classesList.size(); i++) {
                        if (classesList.get(i).getClassCode().equals(classCode)) {
                            spClasses.setSelection(i + 1);
                            break;
                        }
                    }
                } else {
                    // Fallback
                    loadClassesByDepartment(departmentId);
                }
            }

            @Override
            public void onFailure(Call<ProgramClassesResponse> call, Throwable t) {
                Log.e(TAG, "Load classes failed", t);
                // Fallback
                loadClassesByDepartment(departmentId);
            }
        });
    }

    private void setupClassSpinner() {
        List<String> classNames = new ArrayList<>();
        classNames.add("Chọn lớp");
        for (ProgramClass cls : classesList) {
            classNames.add(cls.getClassName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClasses.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        
        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                if ("add".equals(mode)) {
                    createStudent();
                } else {
                    updateStudent();
                }
            }
        });
        
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    //Khi người dùng chọn ngày, cập nhật vào TextView.
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                updateDateDisplay();
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvBirthDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void populateFormForEdit() {
        if (editingStudent == null) return;
        
        etStudentCode.setText(editingStudent.getStudentCode());
        etFullName.setText(editingStudent.getStudentFullName());
        etEmail.setText(editingStudent.getStudentEmail());
        etAddress.setText(editingStudent.getStudentAddress());
        etUsername.setText(editingStudent.getUsername());
        
        // Set birth date
        if (!TextUtils.isEmpty(editingStudent.getDateOfBirth())) {
            tvBirthDate.setText(editingStudent.getDateOfBirth());
        }
        
        // Set department and load classes
        String deptName = editingStudent.getDepartmentName();
        for (int i = 0; i < departmentsList.size(); i++) {
            if (departmentsList.get(i).getDepartmentName().equals(deptName)) {
                spDepartments.setSelection(i + 1);
                // Load classes for this department then select current class
                loadClassesByDepartmentAndSelect(departmentsList.get(i).getDepartmentId(), editingStudent.getProgramClassCode());
                break;
            }
        }
    }

    //Điền dữ liệu vào các ô input.
    //
    //Chọn khoa và lớp tương ứng.
    private boolean validateForm() {
        boolean isValid = true;
        
        // Validate student code
        String studentCode = etStudentCode.getText().toString().trim();
        if (TextUtils.isEmpty(studentCode)) {
            tilStudentCode.setError("Vui lòng nhập mã sinh viên");
            isValid = false;
        } else {
            tilStudentCode.setError(null);
        }
        
        // Validate full name
        String fullName = etFullName.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Vui lòng nhập họ tên");
            isValid = false;
        } else {
            tilFullName.setError(null);
        }
        
        // Validate email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không hợp lệ");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }
        
        // Validate username
        String username = etUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Vui lòng nhập tên đăng nhập");
            isValid = false;
        } else {
            tilUsername.setError(null);
        }
        
        // Validate password (only for add mode)
        if ("add".equals(mode)) {
            String password = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                tilPassword.setError("Vui lòng nhập mật khẩu");
                isValid = false;
            } else if (password.length() < 6) {
                tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
                isValid = false;
            } else {
                tilPassword.setError(null);
            }
        }
        
        // Validate department
        if (spDepartments.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Vui lòng chọn khoa", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        // Validate class
        if (spClasses.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Vui lòng chọn lớp", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        // Validate birth date
        if (TextUtils.isEmpty(tvBirthDate.getText())) {
            Toast.makeText(this, "Vui lòng chọn ngày sinh", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        return isValid;
    }

    //Gọi API createStudent() với CreateStudentRequest.
    // Nếu thành công, hiện toast và finish()
    private void createStudent() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        ProgramClass selectedClass = classesList.get(spClasses.getSelectedItemPosition() - 1);
        
        CreateStudentRequest request = new CreateStudentRequest(
            etStudentCode.getText().toString().trim(),
            etFullName.getText().toString().trim(),
            formatDateForAPI(tvBirthDate.getText().toString()),
            etEmail.getText().toString().trim(),
            etAddress.getText().toString().trim(),
            selectedClass.getClassId(),
            etUsername.getText().toString().trim(),
            etPassword.getText().toString().trim()
        );
        
        apiService.createStudent(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditStudentActivity.this, "Thêm sinh viên thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể thêm sinh viên";
                    Toast.makeText(AddEditStudentActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Create student failed", t);
                Toast.makeText(AddEditStudentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Gửi UpdateStudentRequest thông qua API.
    // Nếu thành công, hiển thị toast và finish().
    private void updateStudent() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        ProgramClass selectedClass = classesList.get(spClasses.getSelectedItemPosition() - 1);
        
        UpdateStudentRequest request = new UpdateStudentRequest(
            editingStudent.getStudentId(),
            etStudentCode.getText().toString().trim(),
            etFullName.getText().toString().trim(),
            formatDateForAPI(tvBirthDate.getText().toString()),
            etEmail.getText().toString().trim(),
            etAddress.getText().toString().trim(),
            selectedClass.getClassId()
        );
        
        apiService.updateStudent(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditStudentActivity.this, "Cập nhật sinh viên thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể cập nhật sinh viên";
                    Toast.makeText(AddEditStudentActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Update student failed", t);
                Toast.makeText(AddEditStudentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDateForAPI(String displayDate) {
        // Convert từ dd/MM/yyyy sang yyyy-MM-dd
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return apiFormat.format(displayFormat.parse(displayDate));
        } catch (Exception e) {
            return displayDate;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 