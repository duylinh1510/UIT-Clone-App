package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminGradesResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private List<AdminGrade> data;

    // Constructors
    public AdminGradesResponse() {}

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<AdminGrade> getData() {
        return data;
    }

    public void setData(List<AdminGrade> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AdminGradesResponse{" +
                "success=" + success +
                ", data=" + data +
                '}';
    }
} 