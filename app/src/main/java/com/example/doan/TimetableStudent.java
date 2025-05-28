package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TimetableStudent implements Serializable {
    @SerializedName("student_id")
    private int studentId;
    
    @SerializedName("student_code")
    private String studentCode;
    
    @SerializedName("student_full_name")
    private String studentFullName;
    
    @SerializedName("student_email")
    private String studentEmail;
    
    @SerializedName("program_class_code")
    private String programClassCode;
    
    @SerializedName("department_name")
    private String departmentName;
    
    @SerializedName("enrollment_id")
    private int enrollmentId;
    
    @SerializedName("enrollment_date")
    private String enrollmentDate;
    
    @SerializedName("status")
    private String status;

    // Constructors
    public TimetableStudent() {}

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getStudentFullName() {
        return studentFullName;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getProgramClassCode() {
        return programClassCode;
    }

    public void setProgramClassCode(String programClassCode) {
        this.programClassCode = programClassCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 