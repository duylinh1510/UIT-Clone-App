package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class CreateSubjectClassRequest {
    @SerializedName("subject_class_code")
    private String subjectClassCode;
    
    @SerializedName("semester")
    private String semester;
    
    @SerializedName("subject_id")
    private int subjectId;
    
    @SerializedName("teacher_id")
    private Integer teacherId; // Optional, có thể null

    public CreateSubjectClassRequest(String subjectClassCode, String semester, int subjectId, Integer teacherId) {
        this.subjectClassCode = subjectClassCode;
        this.semester = semester;
        this.subjectId = subjectId;
        this.teacherId = teacherId;
    }

    // Getters and Setters
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

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }
} 