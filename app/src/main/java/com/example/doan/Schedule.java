package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class Schedule {
    @SerializedName("schedule_id")
    public int scheduleId;
    
    @SerializedName("subject_class_id")
    public int subjectClassId;
    
    @SerializedName("day_of_week")
    public int dayOfWeek;
    
    @SerializedName("period")
    public int period;
    
    @SerializedName("start_time")
    public String start_time;
    
    @SerializedName("end_time")
    public String end_time;
    
    @SerializedName("classroom")
    public String classroom;
    
    @SerializedName("subject_code")
    public String subjectCode;
    
    @SerializedName("subject_name")
    public String subject_name;
    
    @SerializedName("credits")
    public int credits;
    
    @SerializedName("subject_class")
    public String subject_class;
    
    @SerializedName("teacher_full_name")
    public String teacherName;
    
    @SerializedName("department_name")
    public String departmentName;
}
