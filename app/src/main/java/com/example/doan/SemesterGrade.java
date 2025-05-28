package com.example.doan;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SemesterGrade {
    @SerializedName("semester")
    private String semester;

    @SerializedName("grades")
    private List<Grade> grades;

    public SemesterGrade() {}

    public SemesterGrade(String semester, List<Grade> grades) {
        this.semester = semester;
        this.grades = grades;
    }

    // Getters and Setters
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
}