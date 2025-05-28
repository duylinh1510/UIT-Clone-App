package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ProgramClass implements Serializable {
    @SerializedName("program_class_id")
    private int classId;
    
    @SerializedName("program_class_code")
    private String classCode;
    
    @SerializedName("year")
    private String year;
    
    @SerializedName("teacher_id")
    private int teacherId;
    
    @SerializedName("teacher_name")
    private String teacherName;
    
    @SerializedName("department_id")
    private int departmentId;
    
    @SerializedName("student_count")
    private int studentCount;
    
    // Sử dụng getter để className cũng return classCode
    private String className;

    // Constructors
    public ProgramClass() {}

    public ProgramClass(int classId, String classCode, String className) {
        this.classId = classId;
        this.classCode = classCode;
        this.className = className;
    }

    // Getters and Setters
    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
        // Đồng bộ className với classCode
        this.className = classCode;
    }

    public String getClassName() {
        // Trả về classCode vì trong database chỉ có program_class_code
        return classCode != null ? classCode : className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getProgramClassCode() {
        return classCode;
    }

    public void setProgramClassCode(String programClassCode) {
        this.classCode = programClassCode;
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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    @Override
    public String toString() {
        return getClassName(); // Để hiển thị trong Spinner
    }
} 