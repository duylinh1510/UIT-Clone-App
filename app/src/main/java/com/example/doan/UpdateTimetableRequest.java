package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class UpdateTimetableRequest {
    @SerializedName("timetable_id")
    private int timetableId;
    
    @SerializedName("subject_class_id")
    private Integer subjectClassId; // Nullable for partial updates
    
    @SerializedName("day_of_week")
    private Integer dayOfWeek; // Nullable for partial updates
    
    @SerializedName("period")
    private Integer period; // Nullable for partial updates
    
    @SerializedName("start_time")
    private String startTime;
    
    @SerializedName("end_time")
    private String endTime;

    // Constructors
    public UpdateTimetableRequest() {}

    public UpdateTimetableRequest(int timetableId, int subjectClassId, int dayOfWeek, int period, String startTime, String endTime) {
        this.timetableId = timetableId;
        this.subjectClassId = subjectClassId;
        this.dayOfWeek = dayOfWeek;
        this.period = period;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public int getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(int timetableId) {
        this.timetableId = timetableId;
    }

    public Integer getSubjectClassId() {
        return subjectClassId;
    }

    public void setSubjectClassId(Integer subjectClassId) {
        this.subjectClassId = subjectClassId;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
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