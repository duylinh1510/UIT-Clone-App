package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Teacher implements Serializable {
    @SerializedName("teacher_id")
    private int teacherId;
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("teacher_full_name")
    private String teacherFullName;
    
    @SerializedName("date_of_birth")
    private String dateOfBirth;
    
    @SerializedName("teacher_email")
    private String teacherEmail;
    
    @SerializedName("department_id")
    private int departmentId;
    
    @SerializedName("department_name")
    private String departmentName;
    
    @SerializedName("username")
    private String username;

    // Constructors
    public Teacher() {}

    // Getters and Setters
    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTeacherFullName() {
        return teacherFullName;
    }

    public void setTeacherFullName(String teacherFullName) {
        this.teacherFullName = teacherFullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        // Trả về tên giáo viên để hiển thị trong Spinner
        return teacherFullName != null ? teacherFullName : "Giáo viên";
    }
} 