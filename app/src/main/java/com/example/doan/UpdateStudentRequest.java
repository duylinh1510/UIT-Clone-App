package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class UpdateStudentRequest {
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
    
    @SerializedName("program_class_id")
    private int programClassId;

    // Constructors
    public UpdateStudentRequest() {}

    public UpdateStudentRequest(int studentId, String studentCode, String studentFullName, 
                               String dateOfBirth, String studentEmail, String studentAddress, 
                               int programClassId) {
        this.studentId = studentId;
        this.studentCode = studentCode;
        this.studentFullName = studentFullName;
        this.dateOfBirth = dateOfBirth;
        this.studentEmail = studentEmail;
        this.studentAddress = studentAddress;
        this.programClassId = programClassId;
    }

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

    public int getProgramClassId() {
        return programClassId;
    }

    public void setProgramClassId(int programClassId) {
        this.programClassId = programClassId;
    }

    @Override
    public String toString() {
        return "UpdateStudentRequest{" +
                "studentId=" + studentId +
                ", studentCode='" + studentCode + '\'' +
                ", studentFullName='" + studentFullName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", studentEmail='" + studentEmail + '\'' +
                ", studentAddress='" + studentAddress + '\'' +
                ", programClassId=" + programClassId +
                '}';
    }
} 