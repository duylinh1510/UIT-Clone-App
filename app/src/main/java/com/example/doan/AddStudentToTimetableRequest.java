package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class AddStudentToTimetableRequest {
    @SerializedName("timetable_id")
    private int timetableId;
    
    @SerializedName("student_id")
    private int studentId;

    // Constructors
    public AddStudentToTimetableRequest() {}

    public AddStudentToTimetableRequest(int timetableId, int studentId) {
        this.timetableId = timetableId;
        this.studentId = studentId;
    }

    // Getters and Setters
    public int getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(int timetableId) {
        this.timetableId = timetableId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
} 