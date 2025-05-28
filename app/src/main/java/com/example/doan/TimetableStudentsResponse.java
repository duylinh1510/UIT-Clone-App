package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TimetableStudentsResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("timetable_info")
    private AdminTimetable timetableInfo;
    
    @SerializedName("students")
    private List<TimetableStudent> students;
    
    @SerializedName("total_students")
    private int totalStudents;

    // Constructors
    public TimetableStudentsResponse() {}

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

    public AdminTimetable getTimetableInfo() {
        return timetableInfo;
    }

    public void setTimetableInfo(AdminTimetable timetableInfo) {
        this.timetableInfo = timetableInfo;
    }

    public List<TimetableStudent> getStudents() {
        return students;
    }

    public void setStudents(List<TimetableStudent> students) {
        this.students = students;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }
} 