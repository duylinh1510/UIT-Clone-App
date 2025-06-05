package com.example.doan;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GradeActivity extends BaseActivity {

    private static final String TAG = "GradeActivity";

    private LinearLayout semesterContainer;
    private LinearLayout emptyStateLayout;
    private LinearLayout loadingLayout;
    private ApiService apiService;
    private SessionManager sessionManager;
    private int studentId;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);

        initViews(); //Ánh xạ các View
        initData();  // Khởi tạo API service, session
        setupNavigation(); // Cấu hình thanh điều hướng (từ lớp cha BaseActivity)
        loadStudentGrades(); // Gọi API để lấy điểm

        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
        findViewById(R.id.gradeLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void initViews() {
        semesterContainer = findViewById(R.id.semesterContainer);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
    }

    private void initData() {
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);
        studentId = sessionManager.getStudentId();

        Log.d(TAG, "Student ID: " + studentId);
        Log.d(TAG, "Is logged in: " + sessionManager.isLoggedIn());
    }

    private void loadStudentGrades() {
        // Kiểm tra xem có student ID không
        if (studentId == 0) {
            showError("Không tìm thấy thông tin sinh viên. Vui lòng đăng nhập lại.");
            return;
        }

        // Show loading state
        showLoading(true);

        Log.d(TAG, "Loading grades for student ID: " + studentId);

        //Gọi API bất đồng bộ để lấy điểm của sinh viên (sử dụng Retrofit).
        //
        //Hiển thị trạng thái loading.
        //
        //Xử lý kết quả trả về từ server hoặc lỗi mạng.
        Call<GradeResponse> call = apiService.getStudentGrades(studentId);
        call.enqueue(new Callback<GradeResponse>() {
            @Override
            public void onResponse(Call<GradeResponse> call, Response<GradeResponse> response) {
                showLoading(false);
                
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    GradeResponse gradeResponse = response.body();
                    Log.d(TAG, "Response success: " + gradeResponse.isSuccess());
                    Log.d(TAG, "Response message: " + gradeResponse.getMessage());

                    if (gradeResponse.isSuccess()) {
                        List<SemesterGrade> data = gradeResponse.getData();
                        Log.d(TAG, "Number of semesters: " + (data != null ? data.size() : 0));
                        
                        if (data != null && !data.isEmpty()) {
                        populateGrades(data);
                        } else {
                            showEmptyState();
                        }
                    } else {
                        showError(gradeResponse.getMessage());
                    }
                } else {
                    Log.e(TAG, "Response not successful. Code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    showError("Lỗi kết nối server. Mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GradeResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "API call failed", t);
                Log.e(TAG, "Error message: " + t.getMessage());

                String errorMessage = "Không thể kết nối đến server";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("ConnectException")) {
                        errorMessage = "Không thể kết nối đến server. Kiểm tra địa chỉ IP và kết nối mạng.";
                    } else if (t.getMessage().contains("SocketTimeoutException")) {
                        errorMessage = "Kết nối bị timeout. Kiểm tra kết nối mạng.";
                    } else if (t.getMessage().contains("UnknownHostException")) {
                        errorMessage = "Không tìm thấy server. Kiểm tra địa chỉ IP.";
                    }
                }

                showError(errorMessage);
            }
        });
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingLayout.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
            semesterContainer.setVisibility(View.GONE);
        } else {
            loadingLayout.setVisibility(View.GONE);
        }
    }

    private void showEmptyState() {
        emptyStateLayout.setVisibility(View.VISIBLE);
        semesterContainer.setVisibility(View.GONE);
    }

    // Với mỗi học kỳ, tạo một view con (item_semester_grade) và thêm vào semesterContainer.
    //
    // Mỗi học kỳ gồm nhiều môn học, mỗi dòng hiển thị mã môn, mã lớp,
    // điểm quá trình, điểm thực hành, điểm giữa kỳ, điểm cuối kỳ, điểm trung bình.
    private void populateGrades(List<SemesterGrade> semesterGrades) {
        emptyStateLayout.setVisibility(View.GONE);
        semesterContainer.setVisibility(View.VISIBLE);
        semesterContainer.removeAllViews();

        for (SemesterGrade semesterGrade : semesterGrades) {
            createSemesterView(semesterGrade);
        }
    }

    private void createSemesterView(SemesterGrade semesterGrade) {
        //Dùng LayoutInflater để ép kiểu XML item_semester_grade.xml thành một đối tượng View.
        //Không gắn ngay vào semesterContainer (attachToRoot = false), vì ta sẽ xử lý thêm trước khi thêm vào.
        LayoutInflater inflater = LayoutInflater.from(this);
        View semesterView = inflater.inflate(R.layout.item_semester_grade, semesterContainer, false);

        // Lấy TextView tiêu đề học kỳ từ item_semester_grade.xml.
        //
        //Gán tên học kỳ, ví dụ: 🎓 Điểm: HK2 2024-2025.
        TextView semesterTitle = semesterView.findViewById(R.id.semesterTitle);
        semesterTitle.setText("🎓 Điểm: " + semesterGrade.getSemester());

        // Lấy LinearLayout là nơi sẽ chứa từng dòng điểm (grade row).
        LinearLayout gradeTableContainer = semesterView.findViewById(R.id.gradeTableContainer);

        // Duyệt danh sách môn học và thêm từng dòng điểm
        //Nếu có điểm:
        //Duyệt danh sách Grade, gọi addGradeRow(...) để thêm dòng điểm.
        //Đồng thời tính thống kê:
        //totalCredits: tổng số tín chỉ
        //totalGradePoints: tổng tích số (average * tín chỉ)
        //validGradeCount: tổng tín chỉ được tính GPA
        //Nếu không có điểm:
        List<Grade> grades = semesterGrade.getGrades();
        if (grades != null && !grades.isEmpty()) {
            int totalCredits = 0;
            float totalGradePoints = 0;
            int validGradeCount = 0;

            for (int i = 0; i < grades.size(); i++) {
                Grade grade = grades.get(i);
                addGradeRow(gradeTableContainer, grade, i);
                
                // Tính toán thống kê
                totalCredits += grade.getCredits();
                if (grade.getAverageGrade() != null && grade.getAverageGrade() > 0) {
                    totalGradePoints += grade.getAverageGrade() * grade.getCredits();
                    validGradeCount += grade.getCredits();
            }
            }

            // Hiển thị thống kê
            // Tính GPA = totalGradePoints / validGradeCount.
            // Hiển thị số tín chỉ và GPA của học kỳ ở cuối bảng.
            showSemesterStats(semesterView, totalCredits, totalGradePoints, validGradeCount);
        } else {
            // Gọi addEmptyRow(...) để hiển thị dòng “Không có dữ liệu”
            addEmptyRow(gradeTableContainer);
        }
        //Thêm View của học kỳ (đã có tiêu đề, bảng điểm, thống kê)
        //vào semesterContainer (trong layout chính activity_grade.xml).
        semesterContainer.addView(semesterView);
    }

    //Tạo một hàng (row) điểm cho một môn học và thêm vào bảng điểm (LinearLayout container).
    private void addGradeRow(LinearLayout container, Grade grade, int rowIndex) {
        //Tạo một LinearLayout mới theo chiều ngang để chứa các ô dữ liệu.
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(12, 8, 12, 8); //Padding tạo khoảng cách trong hàng.
        row.setWeightSum(8); ////weightSum = 8 là tổng trọng số dùng để phân chia chiều rộng giữa các ô.

        // Nếu rowIndex là số chẵn → dùng background màu A (thường sáng).
        //Nếu lẻ → dùng background màu B (thường tối hơn).
        if (rowIndex % 2 == 0) {
            row.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_table_row_even));
        } else {
            row.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_table_row_odd));
        }

        // Mã môn
        TextView subjectCode = createTableCell(
                grade.getSubjectCode() != null ? grade.getSubjectCode() : "", 1.2f);
        row.addView(subjectCode);

        // Mã lớp
        TextView classCode = createTableCell(
                grade.getClassCode() != null ? grade.getClassCode() : "", 1.2f);
        row.addView(classCode);

        // Tín chỉ
        TextView credits = createTableCell(String.valueOf(grade.getCredits()), 0.8f);
        row.addView(credits);

        // Process Grade (QT)
        TextView processGrade = createGradeCell(grade.getProcessGrade(), 0.8f);
        row.addView(processGrade);

        // Practice Grade (TH)
        TextView practiceGrade = createGradeCell(grade.getPracticeGrade(), 0.8f);
        row.addView(practiceGrade);

        // Midterm Grade (GK)
        TextView midtermGrade = createGradeCell(grade.getMidtermGrade(), 0.8f);
        row.addView(midtermGrade);

        // Final Grade (CK)
        TextView finalGrade = createGradeCell(grade.getFinalGrade(), 0.8f);
        row.addView(finalGrade);

        // Average Grade (TB)
        TextView averageGrade = createGradeCell(grade.getAverageGrade(), 1.0f);
        //Làm nổi bật cột điểm trung bình bằng cách bôi đậm (bold).
        averageGrade.setTypeface(null, android.graphics.Typeface.BOLD);
        row.addView(averageGrade);

        container.addView(row);
    }

    // tạo và trả về một ô (TextView) dùng để hiển thị dữ liệu
    // dạng văn bản trong bảng điểm, với trọng số chiều rộng (weight) xác định.
    private TextView createTableCell(String text, float weight) {
        TextView textView = new TextView(this);
        textView.setText(text != null ? text : "");
        textView.setPadding(4, 8, 4, 8);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setTextSize(11);
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        textView.setSingleLine(true); //Không cho xuống dòng, nếu nội dung dài sẽ bị cắt.

        //width = 0 + weight = X: chiều rộng sẽ phân chia theo weight trong LinearLayout có weightSum.
        //height = WRAP_CONTENT: chiều cao tự động theo nội dung.
        //Dùng để chia ô như bảng tính, cân đối từng cột.
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                weight
        );
        textView.setLayoutParams(params);

        return textView;
    }

    //Tạo một ô điểm (TextView) trong bảng điểm, với màu nền và màu chữ thay đổi tùy theo giá trị điểm số.
    private TextView createGradeCell(Float grade, float weight) {
        //Gọi lại hàm createTableCell(...) để tạo TextView cơ bản.
        //Dùng formatGrade(grade) để chuyển Float thành chuỗi (ví dụ: định dạng 8.5 → "8.50").
        //weight dùng để chia tỷ lệ chiều rộng cột.
        TextView textView = createTableCell(formatGrade(grade), weight);
        
        // Áp dụng màu sắc dựa trên điểm
        if (grade != null && grade > 0) {
            // trả về Drawable tùy điểm số (ví dụ: màu xanh nếu >=8, vàng nếu >=6.5...).
            Drawable background = getGradeBackground(grade);
            if (background != null) {
                textView.setBackground(background);
                //màu chữ phù hợp để đảm bảo độ tương phản với nền.
                textView.setTextColor(getGradeTextColor(grade));
                //Cập nhật lại padding để căn chỉnh đẹp hơn với nền màu.
                textView.setPadding(8, 6, 8, 6);
            }
        }
        
        return textView;
    }

    //Thêm màu nền cho các loại điểm
    private Drawable getGradeBackground(float grade) {
        if (grade >= 8.5f) {
            return ContextCompat.getDrawable(this, R.drawable.bg_grade_excellent);
        } else if (grade >= 7.0f) {
            return ContextCompat.getDrawable(this, R.drawable.bg_grade_good);
        } else if (grade >= 5.0f) {
            return ContextCompat.getDrawable(this, R.drawable.bg_grade_average);
        } else if (grade > 0) {
            return ContextCompat.getDrawable(this, R.drawable.bg_grade_poor);
        }
        return null;
    }

    //Thêm màu cho text các loại điểm
    private int getGradeTextColor(float grade) {
        if (grade >= 8.5f) {
            return ContextCompat.getColor(this, R.color.grade_excellent_text);
        } else if (grade >= 7.0f) {
            return ContextCompat.getColor(this, R.color.grade_good_text);
        } else if (grade >= 5.0f) {
            return ContextCompat.getColor(this, R.color.grade_average_text);
        } else if (grade > 0) {
            return ContextCompat.getColor(this, R.color.grade_poor_text);
        }
        return ContextCompat.getColor(this, android.R.color.black);
    }

    //thêm một dòng thông báo khi không có dữ liệu điểm cho học kỳ.
    private void addEmptyRow(LinearLayout container) {
        TextView emptyText = new TextView(this);
        emptyText.setText("Chưa có dữ liệu điểm cho học kỳ này");
        emptyText.setTextSize(14);
        emptyText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        emptyText.setPadding(16, 24, 16, 24);
        emptyText.setGravity(android.view.Gravity.CENTER);
        emptyText.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_table_row_even));
        
        container.addView(emptyText);
    }


    private void showSemesterStats(View semesterView, int totalCredits, float totalGradePoints, int validGradeCount) {
        LinearLayout footerStats = semesterView.findViewById(R.id.footerStats);
        TextView txtTotalCredits = semesterView.findViewById(R.id.txtTotalCredits);
        TextView txtGPA = semesterView.findViewById(R.id.txtGPA);

        if (totalCredits > 0) {
            footerStats.setVisibility(View.VISIBLE);
            txtTotalCredits.setText("Tổng TC: " + totalCredits);
            
            if (validGradeCount > 0) {
                float gpa = totalGradePoints / validGradeCount;
                txtGPA.setText(String.format("GPA: %.2f", gpa));
                
                // Màu GPA
                if (gpa >= 8.5f) {
                    txtGPA.setTextColor(ContextCompat.getColor(this, R.color.grade_excellent_text));
                } else if (gpa >= 7.0f) {
                    txtGPA.setTextColor(ContextCompat.getColor(this, R.color.grade_good_text));
                } else if (gpa >= 5.0f) {
                    txtGPA.setTextColor(ContextCompat.getColor(this, R.color.grade_average_text));
                } else {
                    txtGPA.setTextColor(ContextCompat.getColor(this, R.color.grade_poor_text));
                }
            } else {
                txtGPA.setText("GPA: N/A");
            }
        }
    }

    private String formatGrade(Float grade) {
        if (grade == null || grade == 0.0f) {
            return "";
        }
        return String.format("%.1f", grade);
    }

    private void showError(String message) {
        Log.e(TAG, "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        showEmptyState();
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        goToPreviousActivity();
                    } else {
                        goToNextActivity();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private void goToPreviousActivity() {
        // Grade -> Schedule (vuốt phải)
        Intent intent = new Intent(GradeActivity.this, LichThiActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    private void goToNextActivity() {
        // Grade -> LichThi (vuốt trái)
        Intent intent = new Intent(GradeActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}