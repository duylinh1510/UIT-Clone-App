package com.example.doan;

import android.content.Context;
import android.content.SharedPreferences;

//Chức năng chính:
//1. Lưu trữ thông tin đăng nhập
//
//Lưu ID sinh viên, mã sinh viên, họ tên và trạng thái đăng nhập vào bộ nhớ cục bộ (SharedPreferences)
//Dữ liệu được lưu trữ persistent, tức là vẫn tồn tại ngay cả khi đóng/mở lại app
//
//2. Tạo phiên đăng nhập mới
//
//Phương thức createSession() lưu thông tin sinh viên khi đăng nhập thành công
//Đánh dấu trạng thái đã đăng nhập
//
//3. Truy xuất thông tin sinh viên
//
//getStudentId(): Lấy ID sinh viên
//getStudentCode(): Lấy mã sinh viên
//getFullName(): Lấy họ tên sinh viên
//isLoggedIn(): Kiểm tra trạng thái đăng nhập
//
//4. Đăng xuất
//
//Phương thức logout() xóa toàn bộ dữ liệu phiên và đặt lại trạng thái chưa đăng nhập
public class SessionManager {
    private static final String PREF_NAME = "StudentSession";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_STUDENT_CODE = "student_code";
    private static final String KEY_FULL_NAME = "student_full_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createSession(int userId, String username, String role, int studentId, String studentCode, String studentFullName) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);
        editor.putInt(KEY_STUDENT_ID, studentId);
        editor.putString(KEY_STUDENT_CODE, studentCode);
        editor.putString(KEY_FULL_NAME, studentFullName);
        editor.commit();
    }

    public void createAdminSession(int userId, String username, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);
        editor.commit();
    }

    public int getStudentId() {
        return pref.getInt(KEY_STUDENT_ID, 0);
    }

    public String getStudentCode() {
        return pref.getString(KEY_STUDENT_CODE, "");
    }

    public String getStudentFullName() {
        return pref.getString(KEY_FULL_NAME, "");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, 0);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, null);
    }

    public boolean isAdmin() {
        return "admin".equals(getRole());
    }

    public boolean isStudent() {
        return "student".equals(getRole());
    }

    public boolean isTeacher() {
        return "teacher".equals(getRole());
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}