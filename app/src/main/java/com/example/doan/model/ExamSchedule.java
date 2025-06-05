package com.example.doan.model;

public class ExamSchedule {
    private String date;
    private Session[] sessions;

    public static class Session {
        private int sessionNumber;
        private String time;
        private String subjectCode;
        private String subjectName;
        private String room;

        public int getSessionNumber() {
            return sessionNumber;
        }

        public String getTime() {
            return time;
        }

        public String getSubjectCode() {
            return subjectCode;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public String getRoom() {
            return room;
        }
    }

    public String getDate() {
        return date;
    }

    public Session[] getSessions() {
        return sessions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExamSchedule{date='").append(date).append("', sessions=[");
        if (sessions != null) {
            for (Session session : sessions) {
                sb.append("{sessionNumber=").append(session.sessionNumber)
                  .append(", time='").append(session.time)
                  .append("', subjectCode='").append(session.subjectCode)
                  .append("', subjectName='").append(session.subjectName)
                  .append("', room='").append(session.room)
                  .append("'}, ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
} 