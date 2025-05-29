package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ScheduleResponse {
    @SerializedName("success")
    public boolean success;
    
    @SerializedName("data")
    public List<Schedule> data;

    @SerializedName("message")
    public String message;

    @SerializedName("day_of_week")
    public int dayOfWeek;
    
    @SerializedName("total_schedules")
    public int totalSchedules;
}

