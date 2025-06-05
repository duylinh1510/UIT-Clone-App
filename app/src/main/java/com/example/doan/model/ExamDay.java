package com.example.doan.model;

import java.util.List;

public class ExamDay {
    private String date;
    private List<ExamSession> sessions;

    public ExamDay(String date, List<ExamSession> sessions) {
        this.date = date;
        this.sessions = sessions;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ExamSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<ExamSession> sessions) {
        this.sessions = sessions;
    }
} 