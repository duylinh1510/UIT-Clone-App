package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class StudentProfile {
    @SerializedName("student_id")
    private int studentId;
    @SerializedName("student_code")
    private String studentCode;
    @SerializedName("full_name")
    private String full_name;
    @SerializedName("birth_date")
    private String birthDate;

    @SerializedName("student_email")
    private String studentEmail;

    @SerializedName("student_address")
    private String studentAddress;

    @SerializedName("class_name")
    private String className;
    @SerializedName("department")
    private String department;

    // Constructors
    public StudentProfile() {}

    public StudentProfile(int studentId, String studentCode, String full_name, String birthDate, String studentEmail, String studentAddress , String className, String department) {
        this.studentId = studentId;
        this.studentCode = studentCode;
        this.full_name = full_name;
        this.birthDate = birthDate;
        this.studentEmail = studentEmail;
        this.studentAddress = studentAddress;
        this.className = className;
        this.department = department;
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

    public String getFullName() {
        return full_name;
    }

    public void setStudentFullName(String full_name) {
        this.full_name = full_name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getStudentEmail(){
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail){
        this.studentEmail = studentEmail;
    }

    public String getStudentAddress(){
        return studentAddress;
    }

    public void setStudentAddress(String studentAddress){
        this.studentAddress =  studentAddress;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "StudentProfile{" +
                "studentId=" + studentId +
                ", studentCode='" + studentCode + '\'' +
                ", studentFullName='" + full_name + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", className='" + className + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}