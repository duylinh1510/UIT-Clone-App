package com.example.doan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TimetableStudentsAdapter extends RecyclerView.Adapter<TimetableStudentsAdapter.StudentViewHolder> {
    
    private List<TimetableStudent> studentsList;
    private List<TimetableStudent> studentsListFiltered;
    private Context context;
    private OnStudentActionListener listener;

    public interface OnStudentActionListener {
        void onRemoveStudent(TimetableStudent student);
    }

    public TimetableStudentsAdapter(Context context, List<TimetableStudent> studentsList) {
        this.context = context;
        this.studentsList = studentsList;
        this.studentsListFiltered = new ArrayList<>(studentsList);
    }

    public void setOnStudentActionListener(OnStudentActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timetable_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        TimetableStudent student = studentsListFiltered.get(position);
        
        holder.tvStudentCode.setText(student.getStudentCode());
        holder.tvStudentName.setText(student.getStudentFullName());
        holder.tvStudentEmail.setText(student.getStudentEmail());
        holder.tvProgramClass.setText("Lớp: " + student.getProgramClassCode());
        holder.tvDepartment.setText("Khoa: " + student.getDepartmentName());

        // Set click listener for remove button
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveStudent(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentsListFiltered.size();
    }

    public void updateStudents(List<TimetableStudent> newStudents) {
        this.studentsList = newStudents;
        this.studentsListFiltered = new ArrayList<>(newStudents);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        studentsListFiltered.clear();
        
        if (query.isEmpty()) {
            studentsListFiltered.addAll(studentsList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (TimetableStudent student : studentsList) {
                if (student.getStudentCode().toLowerCase().contains(lowerCaseQuery) ||
                    student.getStudentFullName().toLowerCase().contains(lowerCaseQuery) ||
                    student.getProgramClassCode().toLowerCase().contains(lowerCaseQuery)) {
                    studentsListFiltered.add(student);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentCode, tvStudentName, tvStudentEmail, tvProgramClass, tvDepartment;
        ImageButton btnRemove;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentCode = itemView.findViewById(R.id.tvStudentCode);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvProgramClass = itemView.findViewById(R.id.tvProgramClass);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
} 