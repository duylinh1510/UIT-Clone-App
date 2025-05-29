package com.example.doan;

import java.util.List;

public class ApiResponse {
    private boolean success;
    private List<ExamSchedule> data;
    public boolean isSuccess()          {
        return success;
    }
    public List<ExamSchedule> getData() {
        return data;
    }
}
