package com.example.doan;

import android.app.TimePickerDialog;
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

public class AddEditTimetableActivity extends AppCompatActivity {
    private static final String TAG = "AddEditTimetableActivity";
    
    private Spinner spSubjectClass, spDayOfWeek, spPeriod;
    private TextInputLayout tilStartTime, tilEndTime;
    private TextInputEditText etStartTime, etEndTime;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private List<SubjectClass> subjectClassesList = new ArrayList<>();
    
    private String mode; // "add" hoặc "edit"
    private AdminTimetable editingTimetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_timetable);
        
        initViews();
        setupToolbar();
        getIntentData();
        setupSpinners();
        setupClickListeners();
        
        apiService = ApiClient.getClient().create(ApiService.class);
        loadSubjectClasses();
    }

    private void initViews() {
        spSubjectClass = findViewById(R.id.spSubjectClass);
        spDayOfWeek = findViewById(R.id.spDayOfWeek);
        spPeriod = findViewById(R.id.spPeriod);
        
        tilStartTime = findViewById(R.id.tilStartTime);
        tilEndTime = findViewById(R.id.tilEndTime);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getIntentData() {
        mode = getIntent().getStringExtra("mode");
        
        if ("edit".equals(mode)) {
            editingTimetable = (AdminTimetable) getIntent().getSerializableExtra("timetable");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Sửa Thời khóa biểu");
            }
            btnSave.setText("Cập nhật");
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thêm Thời khóa biểu");
            }
            btnSave.setText("Thêm");
        }
    }

    private void setupSpinners() {
        // Setup Day of Week Spinner
        List<String> days = new ArrayList<>();
        days.add("Chọn ngày");
        days.add("Chủ nhật");     // 1
        days.add("Thứ hai");      // 2
        days.add("Thứ ba");       // 3
        days.add("Thứ tư");       // 4
        days.add("Thứ năm");      // 5
        days.add("Thứ sáu");      // 6
        days.add("Thứ bảy");      // 7

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDayOfWeek.setAdapter(dayAdapter);

        // Setup Period Spinner
        List<String> periods = new ArrayList<>();
        periods.add("Chọn tiết");
        for (int i = 1; i <= 12; i++) {
            periods.add("Tiết " + i);
        }

        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periods);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPeriod.setAdapter(periodAdapter);
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                if ("add".equals(mode)) {
                    createTimetable();
                } else {
                    updateTimetable();
                }
            }
        });
        
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // Time picker listeners
        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));
    }

    private void showTimePicker(TextInputEditText editText) {
        String currentTime = editText.getText().toString();
        int hour = 7, minute = 0;
        
        if (!TextUtils.isEmpty(currentTime) && currentTime.contains(":")) {
            String[] parts = currentTime.split(":");
            try {
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                // Use default values
            }
        }
        
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format("%02d:%02d:00", selectedHour, selectedMinute);
                    editText.setText(time);
                }, hour, minute, true);
        
        timePickerDialog.show();
    }

    private void loadSubjectClasses() {
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.getSubjectClasses("", null).enqueue(new Callback<SubjectClassesResponse>() {
            @Override
            public void onResponse(Call<SubjectClassesResponse> call, Response<SubjectClassesResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    subjectClassesList = response.body().getData();
                    setupSubjectClassSpinner();
                    
                    // Populate form for editing after loading data
                    if ("edit".equals(mode) && editingTimetable != null) {
                        populateFormForEdit();
                    }
                } else {
                    Toast.makeText(AddEditTimetableActivity.this, "Không thể tải danh sách lớp môn học", Toast.LENGTH_SHORT).show();
                    setupSubjectClassSpinnerEmpty();
                }
            }

            @Override
            public void onFailure(Call<SubjectClassesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load subject classes failed", t);
                Toast.makeText(AddEditTimetableActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                setupSubjectClassSpinnerEmpty();
            }
        });
    }

    private void setupSubjectClassSpinner() {
        List<String> subjectClassNames = new ArrayList<>();
        subjectClassNames.add("Chọn lớp môn học");
        
        for (SubjectClass sc : subjectClassesList) {
            String displayText = sc.getSubjectCode() + " - " + sc.getSubjectName() + " (" + sc.getSubjectClassCode() + ")";
            subjectClassNames.add(displayText);
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectClassNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubjectClass.setAdapter(adapter);
    }

    private void setupSubjectClassSpinnerEmpty() {
        List<String> emptyList = new ArrayList<>();
        emptyList.add("Không có dữ liệu lớp môn học");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emptyList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubjectClass.setAdapter(adapter);
    }

    private void populateFormForEdit() {
        if (editingTimetable == null) return;
        
        // Set subject class
        for (int i = 0; i < subjectClassesList.size(); i++) {
            if (subjectClassesList.get(i).getSubjectClassId() == editingTimetable.getSubjectClassId()) {
                spSubjectClass.setSelection(i + 1); // +1 because of "Chọn lớp môn học" at index 0
                break;
            }
        }
        
        // Set day of week
        spDayOfWeek.setSelection(editingTimetable.getDayOfWeek());
        
        // Set period
        spPeriod.setSelection(editingTimetable.getPeriod());
        
        // Set times
        etStartTime.setText(editingTimetable.getStartTime());
        etEndTime.setText(editingTimetable.getEndTime());
    }

    private boolean validateForm() {
        boolean isValid = true;
        
        // Validate subject class selection
        if (spSubjectClass.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Vui lòng chọn lớp môn học", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        // Validate day of week selection
        if (spDayOfWeek.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        // Validate period selection
        if (spPeriod.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Vui lòng chọn tiết học", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        // Validate start time
        String startTime = etStartTime.getText().toString().trim();
        if (TextUtils.isEmpty(startTime)) {
            tilStartTime.setError("Vui lòng chọn giờ bắt đầu");
            isValid = false;
        } else {
            tilStartTime.setError(null);
        }
        
        // Validate end time
        String endTime = etEndTime.getText().toString().trim();
        if (TextUtils.isEmpty(endTime)) {
            tilEndTime.setError("Vui lòng chọn giờ kết thúc");
            isValid = false;
        } else {
            tilEndTime.setError(null);
        }
        
        return isValid;
    }

    private void createTimetable() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        int selectedSubjectClassIndex = spSubjectClass.getSelectedItemPosition() - 1;
        int subjectClassId = subjectClassesList.get(selectedSubjectClassIndex).getSubjectClassId();
        int dayOfWeek = spDayOfWeek.getSelectedItemPosition();
        int period = spPeriod.getSelectedItemPosition();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        
        CreateTimetableRequest request = new CreateTimetableRequest(
            subjectClassId, dayOfWeek, period, startTime, endTime
        );
        
        apiService.createTimetable(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditTimetableActivity.this, "Thêm thời khóa biểu thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể thêm thời khóa biểu";
                    Toast.makeText(AddEditTimetableActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Create timetable failed", t);
                Toast.makeText(AddEditTimetableActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTimetable() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        int selectedSubjectClassIndex = spSubjectClass.getSelectedItemPosition() - 1;
        int subjectClassId = subjectClassesList.get(selectedSubjectClassIndex).getSubjectClassId();
        int dayOfWeek = spDayOfWeek.getSelectedItemPosition();
        int period = spPeriod.getSelectedItemPosition();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        
        UpdateTimetableRequest request = new UpdateTimetableRequest(
            editingTimetable.getTimetableId(), subjectClassId, dayOfWeek, period, startTime, endTime
        );
        
        apiService.updateTimetable(request).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddEditTimetableActivity.this, "Cập nhật thời khóa biểu thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Không thể cập nhật thời khóa biểu";
                    Toast.makeText(AddEditTimetableActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Update timetable failed", t);
                Toast.makeText(AddEditTimetableActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 