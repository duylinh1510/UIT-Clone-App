package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DepartmentsResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private List<Department> data;

    // Constructors
    public DepartmentsResponse() {}

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Department> getData() {
        return data;
    }

    public void setData(List<Department> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DepartmentsResponse{" +
                "success=" + success +
                ", data=" + data +
                '}';
    }
} 