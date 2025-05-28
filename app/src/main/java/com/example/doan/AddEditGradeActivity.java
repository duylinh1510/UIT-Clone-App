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

public class AddEditGradeActivity extends AppCompatActivity {
    private static final String TAG = "AddEditGradeActivity";
    
    private Spinner spStudents, spSubjects, spSemesters;
    private TextInputLayout tilProcessGrade, tilPracticeGrade, tilMidtermGrade, tilFinalGrade;
    private TextInputEditText etProcessGrade, etPracticeGrade, etMidtermGrade, etFinalGrade;
    private TextView tvAverageGrade;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private List<AdminStudent> studentsList = new ArrayList<>();
    private List<SubjectClass> subjectClassesList = new ArrayList<>();
    
    private String mode; // "add" hoặc "edit"
    private AdminGrade editingGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_grade);
        
        initViews();
        setupToolbar();
        getIntentData();
        setupClickListeners();
        
        // Khác nhau logic load dựa trên mode
        if ("edit".equals(mode)) {
            // Chế độ edit: không cần load danh sách, chỉ populate form
            setupSemesterSpinner(); // Vẫn cần setup trước khi populate
            setupSubjectSpinner();
            setupStudentSpinner();
            populateFormForEdit();
        } else {
            // Chế độ add: load danh sách bình thường
            loadStudents();
            setupSemesterSpinner();
        }
    }

    private void initViews() {
        spStudents = findViewById(R.id.spStudents);
        spSubjects = findViewById(R.id.spSubjects);
        spSemesters = findViewById(R.id.spSemesters);
        
        tilProcessGrade = findViewById(R.id.tilProcessGrade);
        tilPracticeGrade = findViewById(R.id.tilPracticeGrade);
        tilMidtermGrade = findViewById(R.id.tilMidtermGrade);
        tilFinalGrade = findViewById(R.id.tilFinalGrade);
        
        etProcessGrade = findViewById(R.id.etProcessGrade);
        etPracticeGrade = findViewById(R.id.etPracticeGrade);
        etMidtermGrade = findViewById(R.id.etMidtermGrade);
        etFinalGrade = findViewById(R.id.etFinalGrade);
        
        tvAverageGrade = findViewById(R.id.tvAverageGrade);
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
            editingGrade = (AdminGrade) getIntent().getSerializableExtra("grade");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Sửa điểm");
            }
            btnSave.setText("Cập nhật");
            
            // Ở chế độ edit, disable các spinner vì không được phép thay đổi
            spStudents.setEnabled(false);
            spSubjects.setEnabled(false);
            spSemesters.setEnabled(false);
            
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thêm điểm mới");
            }
            btnSave.setText("Thêm");
            
            // Ở chế độ add, enable các spinner
            spStudents.setEnabled(true);
            spSubjects.setEnabled(true);
            spSemesters.setEnabled(true);
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                if ("add".equals(mode)) {
                    createGrade();
                } else {
                    updateGrade();
                }
            }
        });
        
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // Auto calculate average when grades change
        View.OnFocusChangeListener gradeChangeListener = (v, hasFocus) -> {
            if (!hasFocus) {
                calculateAndDisplayAverage();
            }
        };

        etProcessGrade.setOnFocusChangeListener(gradeChangeListener);
        etPracticeGrade.setOnFocusChangeListener(gradeChangeListener);
        etMidtermGrade.setOnFocusChangeListener(gradeChangeListener);
        etFinalGrade.setOnFocusChangeListener(gradeChangeListener);
    }

    private void loadStudents() {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.getStudents(1, 1000, "", null).enqueue(new Callback<AdminStudentsResponse>() {
            @Override
            public void onResponse(Call<AdminStudentsResponse> call, Response<AdminStudentsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    studentsList = response.body().getData();
                    setupStudentSpinner();
                    loadSubjectClasses(); // Load subject classes thật từ API
                    
                    // Không tự động populate nữa vì đã xử lý ở onCreate
                } else {
                    Toast.makeText(AddEditGradeActivity.this, "Không thể tải danh sách sinh viên", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<AdminStudentsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load students failed", t);
                Toast.makeText(AddEditGradeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupStudentSpinner() {
        List<String> studentNames = new ArrayList<>();
        studentNames.add("Chọn sinh viên");
        for (AdminStudent student : studentsList) {
            studentNames.add(student.getStudentCode() + " - " + student.getStudentFullName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, studentNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStudents.setAdapter(adapter);
    }

    private void loadSubjectClasses() {
        apiService.getSubjectClasses("", null).enqueue(new Callback<SubjectClassesResponse>() {
            @Override
            public void onResponse(Call<SubjectClassesResponse> call, Response<SubjectClassesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    subjectClassesList = response.body().getData();
                    setupSubjectSpinner();
                } else {
                    Log.e(TAG, "Failed to load subject classes");
                    // Fallback to mock data if API fails
                    setupSubjectSpinnerMock();
                }
            }

            @Override
            public void onFailure(Call<SubjectClassesResponse> call, Throwable t) {
                Log.e(TAG, "Load subject classes failed", t);
                // Fallback to mock data if API fails
                setupSubjectSpinnerMock();
            }
        });
    }

    private void setupSubjectSpinner() {
        // Load subject classes từ API thật
        List<String> subjects = new ArrayList<>();
        subjects.add("Chọn môn học");
        for (SubjectClass subjectClass : subjectClassesList) {
            subjects.add(subjectClass.getSubjectCode() + " - " + subjectClass.getSubjectName() + 
                        " (" + subjectClass.getSubjectClassCode() + ")");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubjects.setAdapter(adapter);
    }

    private void setupSubjectSpinnerMock() {
        // Fallback mock subjects nếu API fail
        List<String> subjects = new ArrayList<>();
        subjects.add("Chọn môn học");
        subjects.add("Lập trình Android");
        subjects.add("Cơ sở dữ liệu");
        subjects.add("Mạng máy tính");
        subjects.add("Phân tích thiết kế hệ thống");
        subjects.add("Trí tuệ nhân tạo");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubjects.setAdapter(adapter);
    }

    private void setupSemesterSpinner() {
        List<String> semesters = new ArrayList<>();
        semesters.add("Chọn học kỳ");
        semesters.add("HK1 2023-2024");
        semesters.add("HK2 2023-2024");
        semesters.add("HK1 2024-2025");
        semesters.add("HK2 2024-2025");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSemesters.setAdapter(adapter);
    }

    private void populateFormForEdit() {
        if (editingGrade == null) return;
        
        // Trong chế độ edit, override adapter để hiển thị thông tin cố định
        
        // Set student - hiển thị thông tin sinh viên hiện tại
        ArrayAdapter<String> studentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        studentAdapter.add(editingGrade.getStudentCode() + " - " + editingGrade.getStudentName());
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStudents.setAdapter(studentAdapter);
        spStudents.setSelection(0);
        
        // Set subject - hiển thị thông tin môn học hiện tại
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        subjectAdapter.add(editingGrade.getSubjectName());
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubjects.setAdapter(subjectAdapter);
        spSubjects.setSelection(0);
        
        // Set semester - hiển thị thông tin học kỳ hiện tại
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        semesterAdapter.add(editingGrade.getSemester());
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSemesters.setAdapter(semesterAdapter);
        spSemesters.setSelection(0);
        
        // Set grades - focus chính là edit các điểm số
        if (editingGrade.getProcessGrade() != null) {
            etProcessGrade.setText(String.valueOf(editingGrade.getProcessGrade()));
        }
        if (editingGrade.getPracticeGrade() != null) {
            etPracticeGrade.setText(String.valueOf(editingGrade.getPracticeGrade()));
        }
        if (editingGrade.getMidtermGrade() != null) {
            etMidtermGrade.setText(String.valueOf(editingGrade.getMidtermGrade()));
        }
        if (editingGrade.getFinalGrade() != null) {
            etFinalGrade.setText(String.valueOf(editingGrade.getFinalGrade()));
        }
        
        calculateAndDisplayAverage();
    }

    private void calculateAndDisplayAverage() {
        try {
            String processStr = etProcessGrade.getText().toString().trim();
            String practiceStr = etPracticeGrade.getText().toString().trim();
            String midtermStr = etMidtermGrade.getText().toString().trim();
            String finalStr = etFinalGrade.getText().toString().trim();
            
            if (!processStr.isEmpty() && !practiceStr.isEmpty() && 
                !midtermStr.isEmpty() && !finalStr.isEmpty()) {
                
                double process = Double.parseDouble(processStr);
                double practice = Double.parseDouble(practiceStr);
                double midterm = Double.parseDouble(midtermStr);
                double finalGrade = Double.parseDouble(finalStr);
                
                double average = (process * 0.1) + (practice * 0.2) + (midterm * 0.3) + (finalGrade * 0.4);
                tvAverageGrade.setText(String.format("Điểm trung bình: %.2f", average));
                
                // Set color based on grade
                if (average >= 8.0) {
                    tvAverageGrade.setTextColor(getColor(android.R.color.holo_green_dark));
                } else if (average >= 6.5) {
                    tvAverageGrade.setTextColor(getColor(android.R.color.holo_blue_dark));
                } else if (average >= 5.0) {
                    tvAverageGrade.setTextColor(getColor(android.R.color.holo_orange_dark));
                } else {
                    tvAverageGrade.setTextColor(getColor(android.R.color.holo_red_dark));
                }
            } else {
                tvAverageGrade.setText("Điểm trung bình: --");
                tvAverageGrade.setTextColor(getColor(android.R.color.darker_gray));
            }
        } catch (NumberFormatException e) {
            tvAverageGrade.setText("Điểm trung bình: --");
            tvAverageGrade.setTextColor(getColor(android.R.color.darker_gray));
        }
    }

    private boolean validateForm() {
        boolean isValid = true;
        
        // Chỉ validate spinner khi ở chế độ add
        if ("add".equals(mode)) {
            // Validate student selection
            if (spStudents.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Vui lòng chọn sinh viên", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            
            // Validate subject selection
            if (spSubjects.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Vui lòng chọn môn học", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            
            // Validate semester selection
            if (spSemesters.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Vui lòng chọn học kỳ", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }
        
        // Luôn validate grades (cho cả add và edit)
        isValid &= validateGrade(etProcessGrade, tilProcessGrade, "điểm quá trình");
        isValid &= validateGrade(etPracticeGrade, tilPracticeGrade, "điểm thực hành");
        isValid &= validateGrade(etMidtermGrade, tilMidtermGrade, "điểm giữa kỳ");
        isValid &= validateGrade(etFinalGrade, tilFinalGrade, "điểm cuối kỳ");
        
        return isValid;
    }

    private boolean validateGrade(TextInputEditText editText, TextInputLayout inputLayout, String gradeName) {
        String gradeStr = editText.getText().toString().trim();
        
        if (TextUtils.isEmpty(gradeStr)) {
            inputLayout.setError("Vui lòng nhập " + gradeName);
            return false;
        }
        
        try {
            double grade = Double.parseDouble(gradeStr);
            if (grade < 0 || grade > 10) {
                inputLayout.setError(gradeName + " phải từ 0 đến 10");
                return false;
            }
            inputLayout.setError(null);
            return true;
        } catch (NumberFormatException e) {
            inputLayout.setError(gradeName + " không hợp lệ");
            return false;
        }
    }

    private void createGrade() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        AdminStudent selectedStudent = studentsList.get(spStudents.getSelectedItemPosition() - 1);
        String selectedSemester = spSemesters.getSelectedItem().toString();
        
        // Lấy subject class đã chọn từ spinner
        int selectedSubjectIndex = spSubjects.getSelectedItemPosition() - 1; // -1 vì item đầu là "Chọn môn học"
        int subjectClassId;
        
        if (selectedSubjectIndex >= 0 && selectedSubjectIndex < subjectClassesList.size()) {
            // Lấy đúng subject_class_id từ SubjectClass đã chọn
            subjectClassId = subjectClassesList.get(selectedSubjectIndex).getSubjectClassId();
        } else {
            // Fallback nếu dùng mock data
            subjectClassId = 1;
        }
        
        CreateGradeRequest request = new CreateGradeRequest(
            selectedStudent.getStudentId(),
            subjectClassId, // Sử dụng subject được chọn thay vì hardcode
            Double.parseDouble(etProcessGrade.getText().toString()),
            Double.parseDouble(etPracticeGrade.getText().toString()),
            Double.parseDouble(etMidtermGrade.getText().toString()),
            Double.parseDouble(etFinalGrade.getText().toString()),
            selectedSemester
        );
        
        apiService.createGrade(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditGradeActivity.this, "Thêm điểm thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể thêm điểm";
                    Toast.makeText(AddEditGradeActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Create grade failed", t);
                Toast.makeText(AddEditGradeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateGrade() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        UpdateGradeRequest request = new UpdateGradeRequest(
            editingGrade.getGradeId(),
            Double.parseDouble(etProcessGrade.getText().toString()),
            Double.parseDouble(etPracticeGrade.getText().toString()),
            Double.parseDouble(etMidtermGrade.getText().toString()),
            Double.parseDouble(etFinalGrade.getText().toString())
        );
        
        apiService.updateGrade(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditGradeActivity.this, "Cập nhật điểm thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể cập nhật điểm";
                    Toast.makeText(AddEditGradeActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Update grade failed", t);
                Toast.makeText(AddEditGradeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 