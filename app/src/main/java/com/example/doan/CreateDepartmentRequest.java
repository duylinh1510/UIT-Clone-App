package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class CreateDepartmentRequest {
    @SerializedName("department_name")
    private String departmentName;
    
    @SerializedName("department_code")
    private String departmentCode;

    // Constructors
    public CreateDepartmentRequest() {}

    public CreateDepartmentRequest(String departmentName, String departmentCode) {
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
    }

    // Getters and Setters
    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    @Override
    public String toString() {
        return "CreateDepartmentRequest{" +
                "departmentName='" + departmentName + '\'' +
                ", departmentCode='" + departmentCode + '\'' +
                '}';
    }
} 