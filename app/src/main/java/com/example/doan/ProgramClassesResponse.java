package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProgramClassesResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private List<ProgramClass> data;
    
    public ProgramClassesResponse() {}
    
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
    
    public List<ProgramClass> getData() {
        return data;
    }
    
    public void setData(List<ProgramClass> data) {
        this.data = data;
    }
} 