package com.example.doan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AddStudentAdapter extends RecyclerView.Adapter<AddStudentAdapter.StudentViewHolder> {
    
    private List<TimetableStudent> studentsList;
    private Context context;
    private OnStudentSelectListener listener;

    public interface OnStudentSelectListener {
        void onStudentSelected(TimetableStudent student);
    }

    public AddStudentAdapter(Context context, List<TimetableStudent> studentsList) {
        this.context = context;
        this.studentsList = studentsList;
    }

    public void setOnStudentSelectListener(OnStudentSelectListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_add_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        TimetableStudent student = studentsList.get(position);
        
        holder.tvStudentCode.setText(student.getStudentCode());
        holder.tvStudentName.setText(student.getStudentFullName());
        holder.tvStudentEmail.setText(student.getStudentEmail());
        holder.tvProgramClass.setText("Lớp: " + student.getProgramClassCode());
        holder.tvDepartment.setText("Khoa: " + student.getDepartmentName());

        // Set click listener for add button
        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStudentSelected(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public void updateStudents(List<TimetableStudent> newStudents) {
        this.studentsList = newStudents;
        notifyDataSetChanged();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentCode, tvStudentName, tvStudentEmail, tvProgramClass, tvDepartment;
        Button btnAdd;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentCode = itemView.findViewById(R.id.tvStudentCode);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvProgramClass = itemView.findViewById(R.id.tvProgramClass);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
} 