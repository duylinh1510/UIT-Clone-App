package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class ExamScheduleRequest {
    @SerializedName("student_id")
    private int studentId;

    public ExamScheduleRequest(int studentId) {
        this.studentId = studentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    @Override
    public String toString() {
        return "ExamScheduleRequest{" +
                "student_id=" + studentId +
                '}';
    }
} 