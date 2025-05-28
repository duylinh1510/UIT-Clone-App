package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class CreateTimetableRequest {
    @SerializedName("subject_class_id")
    private int subjectClassId;
    
    @SerializedName("day_of_week")
    private int dayOfWeek;
    
    @SerializedName("period")
    private int period;
    
    @SerializedName("start_time")
    private String startTime;
    
    @SerializedName("end_time")
    private String endTime;

    // Constructors
    public CreateTimetableRequest() {}

    public CreateTimetableRequest(int subjectClassId, int dayOfWeek, int period, String startTime, String endTime) {
        this.subjectClassId = subjectClassId;
        this.dayOfWeek = dayOfWeek;
        this.period = period;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public int getSubjectClassId() {
        return subjectClassId;
    }

    public void setSubjectClassId(int subjectClassId) {
        this.subjectClassId = subjectClassId;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
} 