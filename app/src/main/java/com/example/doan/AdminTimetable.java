package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AdminTimetable implements Serializable {
    @SerializedName("timetable_id")
    private int timetableId;
    
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
    
    @SerializedName("subject_class_code")
    private String subjectClassCode;
    
    @SerializedName("semester")
    private String semester;
    
    @SerializedName("subject_id")
    private int subjectId;
    
    @SerializedName("subject_code")
    private String subjectCode;
    
    @SerializedName("subject_name")
    private String subjectName;
    
    @SerializedName("credits")
    private int credits;
    
    @SerializedName("teacher_id")
    private int teacherId;
    
    @SerializedName("teacher_full_name")
    private String teacherFullName;
    
    @SerializedName("department_name")
    private String departmentName;
    
    @SerializedName("day_name")
    private String dayName;

    // Constructors
    public AdminTimetable() {}

    // Getters and Setters
    public int getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(int timetableId) {
        this.timetableId = timetableId;
    }

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

    public String getSubjectClassCode() {
        return subjectClassCode;
    }

    public void setSubjectClassCode(String subjectClassCode) {
        this.subjectClassCode = subjectClassCode;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherFullName() {
        return teacherFullName;
    }

    public void setTeacherFullName(String teacherFullName) {
        this.teacherFullName = teacherFullName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    // Helper methods
    public String getTimeRange() {
        return startTime + " - " + endTime;
    }

    public String getSubjectInfo() {
        return subjectCode + " - " + subjectName;
    }

    public String getPeriodText() {
        return "Tiết " + period;
    }
} 