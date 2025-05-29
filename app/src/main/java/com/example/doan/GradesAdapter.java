package com.example.doan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.GradeViewHolder> {

    private List<AdminGrade> gradesList;
    private OnGradeClickListener listener;

    public interface OnGradeClickListener {
        void onEditClick(AdminGrade grade);
        void onDeleteClick(AdminGrade grade);
    }

    public GradesAdapter(List<AdminGrade> gradesList, OnGradeClickListener listener) {
        this.gradesList = gradesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_grade, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        AdminGrade grade = gradesList.get(position);
        holder.bind(grade, listener);
    }

    @Override
    public int getItemCount() {
        return gradesList.size();
    }

    static class GradeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentInfo;
        private TextView tvSubject;
        private TextView tvSemester;
        private TextView tvProcessGrade;
        private TextView tvPracticeGrade;
        private TextView tvMidtermGrade;
        private TextView tvFinalGrade;
        private TextView tvAverageGrade;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentInfo = itemView.findViewById(R.id.tvStudentInfo);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvSemester = itemView.findViewById(R.id.tvSemester);
            tvProcessGrade = itemView.findViewById(R.id.tvProcessGrade);
            tvPracticeGrade = itemView.findViewById(R.id.tvPracticeGrade);
            tvMidtermGrade = itemView.findViewById(R.id.tvMidtermGrade);
            tvFinalGrade = itemView.findViewById(R.id.tvFinalGrade);
            tvAverageGrade = itemView.findViewById(R.id.tvAverageGrade);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(AdminGrade grade, OnGradeClickListener listener) {
            // Student info
            tvStudentInfo.setText(String.format("%s - %s", 
                grade.getStudentCode(), grade.getStudentName()));
            
            // Subject and semester
            String subjectText = grade.getSubjectName();
            if (grade.getSubjectClassCode() != null && !grade.getSubjectClassCode().isEmpty()) {
                subjectText += " (" + grade.getSubjectClassCode() + ")";
            }
            tvSubject.setText(subjectText);
            tvSemester.setText(grade.getSemester());
            
            // Individual grades
            tvProcessGrade.setText(formatGrade(grade.getProcessGrade()));
            tvPracticeGrade.setText(formatGrade(grade.getPracticeGrade()));
            tvMidtermGrade.setText(formatGrade(grade.getMidtermGrade()));
            tvFinalGrade.setText(formatGrade(grade.getFinalGrade()));
            
            // Average grade
            Double avgGrade = grade.getAverageGrade();
            if (avgGrade != null) {
                tvAverageGrade.setText(String.format(Locale.getDefault(), "%.2f", avgGrade));
                // Set color based on grade
                if (avgGrade >= 8.0) {
                    tvAverageGrade.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
                } else if (avgGrade >= 6.5) {
                    tvAverageGrade.setTextColor(itemView.getContext().getColor(android.R.color.holo_blue_dark));
                } else if (avgGrade >= 5.0) {
                    tvAverageGrade.setTextColor(itemView.getContext().getColor(android.R.color.holo_orange_dark));
                } else {
                    tvAverageGrade.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
                }
            } else {
                tvAverageGrade.setText("--");
                tvAverageGrade.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
            }
            
            // Click listeners
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(grade);
                }
            });
            
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(grade);
                }
            });
        }
        
        private String formatGrade(Double grade) {
            if (grade == null) {
                return "--";
            }
            return String.format(Locale.getDefault(), "%.1f", grade);
        }
    }
} 