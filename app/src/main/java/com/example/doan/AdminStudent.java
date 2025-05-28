package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AdminStudent implements Serializable {
    @SerializedName("student_id")
    private int studentId;
    
    @SerializedName("student_code")
    private String studentCode;
    
    @SerializedName("student_full_name")
    private String studentFullName;
    
    @SerializedName("date_of_birth")
    private String dateOfBirth;
    
    @SerializedName("student_email")
    private String studentEmail;
    
    @SerializedName("student_address")
    private String studentAddress;
    
    @SerializedName("program_class_code")
    private String programClassCode;
    
    @SerializedName("department_name")
    private String departmentName;
    
    @SerializedName("username")
    private String username;

    // Constructors
    public AdminStudent() {}

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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentAddress() {
        return studentAddress;
    }

    public void setStudentAddress(String studentAddress) {
        this.studentAddress = studentAddress;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "AdminStudent{" +
                "studentId=" + studentId +
                ", studentCode='" + studentCode + '\'' +
                ", studentFullName='" + studentFullName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", studentEmail='" + studentEmail + '\'' +
                ", studentAddress='" + studentAddress + '\'' +
                ", programClassCode='" + programClassCode + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
} 