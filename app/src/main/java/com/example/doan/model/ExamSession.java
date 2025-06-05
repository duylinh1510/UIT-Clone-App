package com.example.doan.model;

public class ExamSession {
    private int sessionNumber;
    private String time;
    private String subjectCode;
    private String subjectName;
    private String room;

    public ExamSession(int sessionNumber, String time, String subjectCode, String subjectName, String room) {
        this.sessionNumber = sessionNumber;
        this.time = time;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.room = room;
    }

    public int getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(int sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
} 