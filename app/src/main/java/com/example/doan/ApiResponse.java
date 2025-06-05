package com.example.doan;

import com.example.doan.model.ExamSchedule;
import java.util.List;

public class ApiResponse {
    private boolean success;
    private String message;
    private List<ExamSchedule> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<ExamSchedule> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + (data != null ? data.size() : "null") +
                '}';
    }
}
