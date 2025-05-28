package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AvailableStudentsResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private List<TimetableStudent> data;
    
    @SerializedName("total")
    private int total;

    // Constructors
    public AvailableStudentsResponse() {}

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TimetableStudent> getData() {
        return data;
    }

    public void setData(List<TimetableStudent> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
} 