package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class Grade {
    @SerializedName("id")
    private int id;

    @SerializedName("student_id")
    private int studentId;

    @SerializedName("subject_class_id")
    private int subjectClassId;

    @SerializedName("subject_code")
    private String subjectCode;

    @SerializedName("subject_name")
    private String subjectName;

    @SerializedName("class_code")
    private String classCode;

    @SerializedName("credits")
    private int credits;

    @SerializedName("process_grade")
    private Float processGrade;

    @SerializedName("practice_grade")
    private Float practiceGrade;

    @SerializedName("midterm_grade")
    private Float midtermGrade;

    @SerializedName("final_grade")
    private Float finalGrade;

    @SerializedName("average_grade")
    private Float averageGrade;

    @SerializedName("semester")
    private String semester;

    // Constructors
    public Grade() {}

    public Grade(int id, int studentId, int subjectClassId, String subjectCode,
                 String subjectName, String classCode, int credits, Float processGrade,
                 Float practiceGrade, Float midtermGrade, Float finalGrade,
                 Float averageGrade, String semester) {
        this.id = id;
        this.studentId = studentId;
        this.subjectClassId = subjectClassId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.classCode = classCode;
        this.credits = credits;
        this.processGrade = processGrade;
        this.practiceGrade = practiceGrade;
        this.midtermGrade = midtermGrade;
        this.finalGrade = finalGrade;
        this.averageGrade = averageGrade;
        this.semester = semester;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getSubjectClassId() { return subjectClassId; }
    public void setSubjectClassId(int subjectClassId) { this.subjectClassId = subjectClassId; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public Float getProcessGrade() { return processGrade; }
    public void setProcessGrade(Float processGrade) { this.processGrade = processGrade; }

    public Float getPracticeGrade() { return practiceGrade; }
    public void setPracticeGrade(Float practiceGrade) { this.practiceGrade = practiceGrade; }

    public Float getMidtermGrade() { return midtermGrade; }
    public void setMidtermGrade(Float midtermGrade) { this.midtermGrade = midtermGrade; }

    public Float getFinalGrade() { return finalGrade; }
    public void setFinalGrade(Float finalGrade) { this.finalGrade = finalGrade; }

    public Float getAverageGrade() { return averageGrade; }
    public void setAverageGrade(Float averageGrade) { this.averageGrade = averageGrade; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}
