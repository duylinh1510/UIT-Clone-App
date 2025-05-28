package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Department implements Serializable {
    @SerializedName("department_id")
    private int departmentId;
    
    @SerializedName("department_name")
    private String departmentName;
    
    @SerializedName("department_code")
    private String departmentCode;

    // Constructors
    public Department() {}

    public Department(int departmentId, String departmentName, String departmentCode) {
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
        return departmentName; // Để hiển thị trong Spinner
    }
} 