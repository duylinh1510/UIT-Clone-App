package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SubjectsResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private List<Subject> data;
    
    @SerializedName("message")
    private String message;

    // Constructors
    public SubjectsResponse() {}

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Subject> getData() {
        return data;
    }

    public void setData(List<Subject> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 