package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Subject implements Serializable {
    @SerializedName("subject_id")
    private int subjectId;
    
    @SerializedName("subject_code")
    private String subjectCode;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("credits")
    private int credits;
    
    @SerializedName("department_id")
    private int departmentId;
    
    @SerializedName("department_name")
    private String departmentName;

    // Constructors
    public Subject() {}

    public Subject(int subjectId, String subjectCode, String name, int credits,
                  int departmentId, String departmentName) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.name = name;
        this.credits = credits;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    // Getters and Setters
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "subjectId=" + subjectId +
                ", subjectCode='" + subjectCode + '\'' +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                ", departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
} 