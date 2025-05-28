package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class UpdateProgramClassRequest {
    @SerializedName("program_class_id")
    private int programClassId;
    
    @SerializedName("program_class_code")
    private String programClassCode;
    
    @SerializedName("year")
    private String year;
    
    @SerializedName("teacher_id")
    private int teacherId;
    
    @SerializedName("department_id")
    private int departmentId;

    public UpdateProgramClassRequest() {}

    public UpdateProgramClassRequest(int programClassId, String programClassCode, String year, int teacherId, int departmentId) {
        this.programClassId = programClassId;
        this.programClassCode = programClassCode;
        this.year = year;
        this.teacherId = teacherId;
        this.departmentId = departmentId;
    }

    // Getters and Setters
    public int getProgramClassId() {
        return programClassId;
    }

    public void setProgramClassId(int programClassId) {
        this.programClassId = programClassId;
    }

    public String getProgramClassCode() {
        return programClassCode;
    }

    public void setProgramClassCode(String programClassCode) {
        this.programClassCode = programClassCode;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }
} 