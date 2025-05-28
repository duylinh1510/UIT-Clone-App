package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class StudentProfileResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private StudentProfile data;

    // Constructors
    public StudentProfileResponse() {}

    public StudentProfileResponse(boolean success, String message, StudentProfile data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

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

    public StudentProfile getData() {
        return data;
    }

    public void setData(StudentProfile data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "StudentProfileResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
} 