package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class CreateTeacherRequest {
    @SerializedName("teacher_full_name")
    private String teacherFullName;
    
    @SerializedName("date_of_birth")
    private String dateOfBirth;
    
    @SerializedName("teacher_email")
    private String teacherEmail;
    
    @SerializedName("department_id")
    private int departmentId;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("password")
    private String password;

    // Constructors
    public CreateTeacherRequest() {}

    public CreateTeacherRequest(String teacherFullName, String dateOfBirth,
                               String teacherEmail, int departmentId, String username, String password) {
        this.teacherFullName = teacherFullName;
        this.dateOfBirth = dateOfBirth;
        this.teacherEmail = teacherEmail;
        this.departmentId = departmentId;
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "CreateTeacherRequest{" +
                "teacherFullName='" + teacherFullName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", teacherEmail='" + teacherEmail + '\'' +
                ", departmentId=" + departmentId +
                ", username='" + username + '\'' +
                '}';
    }
} 