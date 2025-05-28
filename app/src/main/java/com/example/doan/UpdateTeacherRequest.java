package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class UpdateTeacherRequest {
    @SerializedName("teacher_id")
    private int teacherId;
    
    @SerializedName("teacher_full_name")
    private String teacherFullName;
    
    @SerializedName("date_of_birth")
    private String dateOfBirth;
    
    @SerializedName("teacher_email")
    private String teacherEmail;
    
    @SerializedName("department_id")
    private int departmentId;

    // Constructors
    public UpdateTeacherRequest() {}

    public UpdateTeacherRequest(int teacherId, String teacherFullName, 
                               String dateOfBirth, String teacherEmail, int departmentId) {
        this.teacherId = teacherId;
        this.teacherFullName = teacherFullName;
        this.dateOfBirth = dateOfBirth;
        this.teacherEmail = teacherEmail;
        this.departmentId = departmentId;
    }

    // Getters and Setters
    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
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

    @Override
    public String toString() {
        return "UpdateTeacherRequest{" +
                "teacherId=" + teacherId +
                ", teacherFullName='" + teacherFullName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", teacherEmail='" + teacherEmail + '\'' +
                ", departmentId=" + departmentId +
                '}';
    }
} 