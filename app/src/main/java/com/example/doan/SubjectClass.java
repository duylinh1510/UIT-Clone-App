package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class SubjectClass implements Serializable {
    @SerializedName("subject_class_id")
    private int subjectClassId;
    
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

    // Constructors
    public SubjectClass() {}

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

    @Override
    public String toString() {
        return subjectCode + " - " + subjectName + " (" + subjectClassCode + ")";
    }
} 