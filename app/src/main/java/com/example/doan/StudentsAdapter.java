package com.example.doan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentViewHolder> {
    
    private List<AdminStudent> students;
    private OnStudentClickListener listener;

    public interface OnStudentClickListener {
        void onEditClick(AdminStudent student);
        void onDeleteClick(AdminStudent student);
    }

    public StudentsAdapter(List<AdminStudent> students, OnStudentClickListener listener) {
        this.students = students;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        AdminStudent student = students.get(position);
        holder.bind(student);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentCode, tvStudentName, tvEmail, tvDepartment, tvClass;
        private ImageButton btnEdit, btnDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentCode = itemView.findViewById(R.id.tvStudentCode);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvClass = itemView.findViewById(R.id.tvClass);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(AdminStudent student) {
            tvStudentCode.setText(student.getStudentCode());
            tvStudentName.setText(student.getStudentFullName());
            tvEmail.setText(student.getStudentEmail());
            tvDepartment.setText(student.getDepartmentName());
            tvClass.setText(student.getProgramClassCode());

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(student);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(student);
                }
            });
        }
    }
}