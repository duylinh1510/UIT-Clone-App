package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SubjectClassesResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private List<SubjectClass> data;
    
    @SerializedName("message")
    private String message;

    // Constructors
    public SubjectClassesResponse() {}

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<SubjectClass> getData() {
        return data;
    }

    public void setData(List<SubjectClass> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 