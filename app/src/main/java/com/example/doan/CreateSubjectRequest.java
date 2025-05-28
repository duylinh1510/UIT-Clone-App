package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class CreateSubjectRequest {
    @SerializedName("subject_code")
    private String subjectCode;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("credits")
    private int credits;
    
    @SerializedName("department_id")
    private int departmentId;

    // Constructors
    public CreateSubjectRequest() {}

    public CreateSubjectRequest(String subjectCode, String name, int credits, int departmentId) {
        this.subjectCode = subjectCode;
        this.name = name;
        this.credits = credits;
        this.departmentId = departmentId;
    }

    // Getters and Setters
    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        return "CreateSubjectRequest{" +
                "subjectCode='" + subjectCode + '\'' +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                ", departmentId=" + departmentId +
                '}';
    }
} 