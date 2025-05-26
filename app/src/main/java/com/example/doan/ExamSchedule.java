package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class ExamSchedule {
    @SerializedName("subject_name") private String subjectName;
    @SerializedName("exam_date")    private String examDate;
    @SerializedName("start_time")   private String startTime;
    @SerializedName("end_time")     private String endTime;
    @SerializedName("exam_room")    private String examRoom;

    /* getters */
    public String getSubjectName() { return subjectName; }
    public String getExamDate()    { return examDate; }
    public String getStartTime()   { return startTime; }
    public String getEndTime()     { return endTime; }
    public String getExamRoom()    { return examRoom; }
}