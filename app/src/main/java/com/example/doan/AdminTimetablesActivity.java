package com.example.doan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class AdminTimetablesActivity extends AppCompatActivity implements TimetablesAdapter.OnTimetableActionListener {
    private static final String TAG = "AdminTimetablesActivity";
    private static final int REQUEST_ADD_TIMETABLE = 100;
    private static final int REQUEST_EDIT_TIMETABLE = 101;

    private EditText etSearch;
    private Spinner spDayFilter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private FloatingActionButton fabAdd;

    private ApiService apiService;
    private TimetablesAdapter adapter;
    private List<AdminTimetable> timetablesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_timetables);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupSearch();
        setupClickListeners();

        apiService = ApiClient.getClient().create(ApiService.class);
        loadTimetables();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        spDayFilter = findViewById(R.id.spDayFilter);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        tvNoData = findViewById(R.id.tvNoData);
        fabAdd = findViewById(R.id.fabAdd);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý Thời khóa biểu");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new TimetablesAdapter(this, timetablesList);
        adapter.setOnTimetableActionListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        // Setup day filter spinner
        List<String> dayOptions = new ArrayList<>();
        dayOptions.add("Tất cả các ngày");
        dayOptions.add("Chủ nhật");
        dayOptions.add("Thứ hai");
        dayOptions.add("Thứ ba");
        dayOptions.add("Thứ tư");
        dayOptions.add("Thứ năm");
        dayOptions.add("Thứ sáu");
        dayOptions.add("Thứ bảy");

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dayOptions);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDayFilter.setAdapter(dayAdapter);

        // Search text watcher
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchTimetables();
            }
        });

        // Day filter listener
        spDayFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchTimetables();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupClickListeners() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditTimetableActivity.class);
            intent.putExtra("mode", "add");
            startActivityForResult(intent, REQUEST_ADD_TIMETABLE);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadTimetables();
        });
    }

    private void searchTimetables() {
        String searchText = etSearch.getText().toString().trim();
        Integer dayOfWeek = null;
        
        int selectedDayPosition = spDayFilter.getSelectedItemPosition();
        if (selectedDayPosition > 0) { // 0 = "Tất cả các ngày"
            dayOfWeek = selectedDayPosition; // 1=Chủ nhật, 2=Thứ hai, ...
        }

        loadTimetablesWithFilters(searchText, dayOfWeek, null);
    }

    private void loadTimetables() {
        loadTimetablesWithFilters("", null, null);
    }

    private void loadTimetablesWithFilters(String search, Integer dayOfWeek, Integer subjectClassId) {
        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);

        apiService.getTimetables(search, dayOfWeek, subjectClassId).enqueue(new Callback<AdminTimetablesResponse>() {
            @Override
            public void onResponse(Call<AdminTimetablesResponse> call, Response<AdminTimetablesResponse> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        timetablesList.clear();
                        if (response.body().getData() != null) {
                            timetablesList.addAll(response.body().getData());
                        }
                        updateUI();
                    } else {
                        showError("Lỗi: " + response.body().getMessage());
                    }
                } else {
                    showError("Không thể tải dữ liệu thời khóa biểu");
                }
            }

            @Override
            public void onFailure(Call<AdminTimetablesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Load timetables failed", t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void updateUI() {
        if (timetablesList.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.updateTimetables(timetablesList);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditTimetable(AdminTimetable timetable) {
        Intent intent = new Intent(this, AddEditTimetableActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("timetable", timetable);
        startActivityForResult(intent, REQUEST_EDIT_TIMETABLE);
    }

    @Override
    public void onDeleteTimetable(AdminTimetable timetable) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa lịch học:\n" +
                        timetable.getDayName() + " - " + timetable.getPeriodText() + "\n" +
                        timetable.getSubjectInfo() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteTimetable(timetable.getTimetableId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onViewStudents(AdminTimetable timetable) {
        Intent intent = new Intent(this, TimetableStudentsActivity.class);
        intent.putExtra("timetable", timetable);
        startActivity(intent);
    }

    private void deleteTimetable(int timetableId) {
        progressBar.setVisibility(View.VISIBLE);

        apiService.deleteTimetable(timetableId).enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(AdminTimetablesActivity.this, "Xóa thời khóa biểu thành công", Toast.LENGTH_SHORT).show();
                        loadTimetables(); // Reload data
                    } else {
                        showError("Lỗi: " + response.body().getMessage());
                    }
                } else {
                    showError("Không thể xóa thời khóa biểu");
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Delete timetable failed", t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == REQUEST_ADD_TIMETABLE || requestCode == REQUEST_EDIT_TIMETABLE)) {
            loadTimetables(); // Reload data after add/edit
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 