package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class CreateGradeRequest {
    @SerializedName("student_id")
    private int studentId;
    
    @SerializedName("subject_class_id")
    private int subjectClassId;
    
    @SerializedName("process_grade")
    private Double processGrade;
    
    @SerializedName("practice_grade")
    private Double practiceGrade;
    
    @SerializedName("midterm_grade")
    private Double midtermGrade;
    
    @SerializedName("final_grade")
    private Double finalGrade;
    
    @SerializedName("semester")
    private String semester;

    // Constructors
    public CreateGradeRequest() {}

    public CreateGradeRequest(int studentId, int subjectClassId, Double processGrade, 
                             Double practiceGrade, Double midtermGrade, Double finalGrade, String semester) {
        this.studentId = studentId;
        this.subjectClassId = subjectClassId;
        this.processGrade = processGrade;
        this.practiceGrade = practiceGrade;
        this.midtermGrade = midtermGrade;
        this.finalGrade = finalGrade;
        this.semester = semester;
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSubjectClassId() {
        return subjectClassId;
    }

    public void setSubjectClassId(int subjectClassId) {
        this.subjectClassId = subjectClassId;
    }

    public Double getProcessGrade() {
        return processGrade;
    }

    public void setProcessGrade(Double processGrade) {
        this.processGrade = processGrade;
    }

    public Double getPracticeGrade() {
        return practiceGrade;
    }

    public void setPracticeGrade(Double practiceGrade) {
        this.practiceGrade = practiceGrade;
    }

    public Double getMidtermGrade() {
        return midtermGrade;
    }

    public void setMidtermGrade(Double midtermGrade) {
        this.midtermGrade = midtermGrade;
    }

    public Double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(Double finalGrade) {
        this.finalGrade = finalGrade;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return "CreateGradeRequest{" +
                "studentId=" + studentId +
                ", subjectClassId=" + subjectClassId +
                ", processGrade=" + processGrade +
                ", practiceGrade=" + practiceGrade +
                ", midtermGrade=" + midtermGrade +
                ", finalGrade=" + finalGrade +
                ", semester='" + semester + '\'' +
                '}';
    }
} 