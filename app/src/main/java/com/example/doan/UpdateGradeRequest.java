package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class UpdateGradeRequest {
    @SerializedName("grade_id")
    private int gradeId;
    
    @SerializedName("process_grade")
    private Double processGrade;
    
    @SerializedName("practice_grade")
    private Double practiceGrade;
    
    @SerializedName("midterm_grade")
    private Double midtermGrade;
    
    @SerializedName("final_grade")
    private Double finalGrade;

    // Constructors
    public UpdateGradeRequest() {}

    public UpdateGradeRequest(int gradeId, Double processGrade, Double practiceGrade, 
                             Double midtermGrade, Double finalGrade) {
        this.gradeId = gradeId;
        this.processGrade = processGrade;
        this.practiceGrade = practiceGrade;
        this.midtermGrade = midtermGrade;
        this.finalGrade = finalGrade;
    }

    // Getters and Setters
    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
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

    @Override
    public String toString() {
        return "UpdateGradeRequest{" +
                "gradeId=" + gradeId +
                ", processGrade=" + processGrade +
                ", practiceGrade=" + practiceGrade +
                ", midtermGrade=" + midtermGrade +
                ", finalGrade=" + finalGrade +
                '}';
    }
} 