package com.example.doan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.model.ExamDay;
import com.example.doan.model.ExamSession;

import java.util.List;

public class ExamScheduleAdapter extends RecyclerView.Adapter<ExamScheduleAdapter.ExamDayViewHolder> {
    private List<ExamDay> examDays;

    public ExamScheduleAdapter(List<ExamDay> examDays) {
        this.examDays = examDays;
    }

    @NonNull
    @Override
    public ExamDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_day, parent, false);
        return new ExamDayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamDayViewHolder holder, int position) {
        ExamDay examDay = examDays.get(position);
        holder.bind(examDay);
    }

    @Override
    public int getItemCount() {
        return examDays.size();
    }

    static class ExamDayViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTextView;
        private ViewGroup sessionsContainer;

        public ExamDayViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            sessionsContainer = itemView.findViewById(R.id.sessionsContainer);
        }

        public void bind(ExamDay examDay) {
            dateTextView.setText(examDay.getDate());
            sessionsContainer.removeAllViews();

            for (ExamSession session : examDay.getSessions()) {
                View sessionView = LayoutInflater.from(itemView.getContext())
                        .inflate(R.layout.item_exam_session, sessionsContainer, false);

                TextView sessionNumberTextView = sessionView.findViewById(R.id.sessionNumberTextView);
                TextView timeTextView = sessionView.findViewById(R.id.timeTextView);
                TextView subjectCodeTextView = sessionView.findViewById(R.id.subjectCodeTextView);
                TextView subjectNameTextView = sessionView.findViewById(R.id.subjectNameTextView);
                TextView roomTextView = sessionView.findViewById(R.id.roomTextView);

                sessionNumberTextView.setText("Ca " + session.getSessionNumber());
                timeTextView.setText(session.getTime());
                subjectCodeTextView.setText(session.getSubjectCode());
                subjectNameTextView.setText(session.getSubjectName());
                roomTextView.setText(session.getRoom());

                sessionsContainer.addView(sessionView);
            }
        }
    }
} 