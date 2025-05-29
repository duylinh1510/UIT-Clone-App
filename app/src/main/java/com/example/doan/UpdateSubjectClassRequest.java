package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class UpdateSubjectClassRequest {
    @SerializedName("subject_class_id")
    private int subjectClassId;
    
    @SerializedName("subject_class_code")
    private String subjectClassCode;
    
    @SerializedName("semester")
    private String semester;
    
    @SerializedName("teacher_id")
    private Integer teacherId; // Optional, có thể null

    public UpdateSubjectClassRequest(int subjectClassId, String subjectClassCode, String semester, Integer teacherId) {
        this.subjectClassId = subjectClassId;
        this.subjectClassCode = subjectClassCode;
        this.semester = semester;
        this.teacherId = teacherId;
    }

    // Getters and Setters
    public int getSubjectClassId() {
        return subjectClassId;
    }

    public void setSubjectClassId(int subjectClassId) {
        this.subjectClassId = subjectClassId;
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

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }
} 