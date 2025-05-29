package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class UpdateSubjectClassTeacherRequest {
    @SerializedName("subject_class_id")
    private int subjectClassId;
    
    @SerializedName("teacher_id")
    private int teacherId;

    public UpdateSubjectClassTeacherRequest() {}

    public UpdateSubjectClassTeacherRequest(int subjectClassId, int teacherId) {
        this.subjectClassId = subjectClassId;
        this.teacherId = teacherId;
    }

    public int getSubjectClassId() {
        return subjectClassId;
    }

    public void setSubjectClassId(int subjectClassId) {
        this.subjectClassId = subjectClassId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }
} 