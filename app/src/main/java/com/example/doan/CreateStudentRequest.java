package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class CreateStudentRequest {
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
    
    @SerializedName("program_class_id")
    private int programClassId;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("password")
    private String password;

    // Constructors
    public CreateStudentRequest() {}

    public CreateStudentRequest(String studentCode, String studentFullName, String dateOfBirth, 
                               String studentEmail, String studentAddress, int programClassId, 
                               String username, String password) {
        this.studentCode = studentCode;
        this.studentFullName = studentFullName;
        this.dateOfBirth = dateOfBirth;
        this.studentEmail = studentEmail;
        this.studentAddress = studentAddress;
        this.programClassId = programClassId;
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
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

    public int getProgramClassId() {
        return programClassId;
    }

    public void setProgramClassId(int programClassId) {
        this.programClassId = programClassId;
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
        return "CreateStudentRequest{" +
                "studentCode='" + studentCode + '\'' +
                ", studentFullName='" + studentFullName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", studentEmail='" + studentEmail + '\'' +
                ", studentAddress='" + studentAddress + '\'' +
                ", programClassId=" + programClassId +
                ", username='" + username + '\'' +
                ", password='[HIDDEN]'" +
                '}';
    }
} 