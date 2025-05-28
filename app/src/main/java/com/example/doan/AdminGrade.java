package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AdminGrade implements Serializable {
    @SerializedName("grade_id")
    private int gradeId;
    
    @SerializedName("student_code")
    private String studentCode;
    
    @SerializedName("student_name")
    private String studentName;
    
    @SerializedName("subject_name")
    private String subjectName;
    
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
    public AdminGrade() {}

    // Getters and Setters
    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
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

    // Helper method to calculate final average grade
    public Double getAverageGrade() {
        if (processGrade == null || practiceGrade == null || midtermGrade == null || finalGrade == null) {
            return null;
        }
        // Công thức tính điểm trung bình: 10% quá trình + 20% thực hành + 30% giữa kỳ + 40% cuối kỳ
        return (processGrade * 0.1) + (practiceGrade * 0.2) + (midtermGrade * 0.3) + (finalGrade * 0.4);
    }

    @Override
    public String toString() {
        return "AdminGrade{" +
                "gradeId=" + gradeId +
                ", studentCode='" + studentCode + '\'' +
                ", studentName='" + studentName + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", processGrade=" + processGrade +
                ", practiceGrade=" + practiceGrade +
                ", midtermGrade=" + midtermGrade +
                ", finalGrade=" + finalGrade +
                ", semester='" + semester + '\'' +
                '}';
    }
} 