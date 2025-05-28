package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class UpdateDepartmentRequest {
    @SerializedName("department_id")
    private int departmentId;
    
    @SerializedName("department_name")
    private String departmentName;
    
    @SerializedName("department_code")
    private String departmentCode;

    // Constructors
    public UpdateDepartmentRequest() {}

    public UpdateDepartmentRequest(int departmentId, String departmentName, String departmentCode) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
    }

    // Getters and Setters
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

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    @Override
    public String toString() {
        return "UpdateDepartmentRequest{" +
                "departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", departmentCode='" + departmentCode + '\'' +
                '}';
    }
} 