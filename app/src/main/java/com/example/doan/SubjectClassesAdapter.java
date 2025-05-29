package com.example.doan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectClassesAdapter extends RecyclerView.Adapter<SubjectClassesAdapter.SubjectClassViewHolder> {

    private List<SubjectClass> subjectClassesList;
    private OnSubjectClassClickListener listener;

    public interface OnSubjectClassClickListener {
        void onEditClassClick(SubjectClass subjectClass);
        void onDeleteClassClick(SubjectClass subjectClass);
    }

    public SubjectClassesAdapter(List<SubjectClass> subjectClassesList, OnSubjectClassClickListener listener) {
        this.subjectClassesList = subjectClassesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject_class, parent, false);
        return new SubjectClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectClassViewHolder holder, int position) {
        SubjectClass subjectClass = subjectClassesList.get(position);
        holder.bind(subjectClass, listener);
    }

    @Override
    public int getItemCount() {
        return subjectClassesList.size();
    }

    static class SubjectClassViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubjectClassCode, tvSemester, tvTeacher, tvDepartment;
        private ImageButton btnEditClass, btnDeleteClass;

        public SubjectClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectClassCode = itemView.findViewById(R.id.tvSubjectClassCode);
            tvSemester = itemView.findViewById(R.id.tvSemester);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            btnEditClass = itemView.findViewById(R.id.btnEditClass);
            btnDeleteClass = itemView.findViewById(R.id.btnDeleteClass);
        }

        public void bind(SubjectClass subjectClass, OnSubjectClassClickListener listener) {
            tvSubjectClassCode.setText(subjectClass.getSubjectClassCode());
            tvSemester.setText("Học kỳ: " + subjectClass.getSemester());
            
            String teacherText = subjectClass.getTeacherFullName() != null ? 
                subjectClass.getTeacherFullName() : "Chưa có giáo viên";
            tvTeacher.setText("GV: " + teacherText);
            
            String deptText = subjectClass.getDepartmentName() != null ? 
                subjectClass.getDepartmentName() : "Không xác định";
            tvDepartment.setText("Khoa: " + deptText);

            btnEditClass.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClassClick(subjectClass);
                }
            });

            btnDeleteClass.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClassClick(subjectClass);
                }
            });
        }
    }
} 