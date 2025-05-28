package com.example.doan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TeachersAdapter extends RecyclerView.Adapter<TeachersAdapter.TeacherViewHolder> {

    private List<Teacher> teachersList;
    private OnTeacherClickListener listener;

    public interface OnTeacherClickListener {
        void onEditClick(Teacher teacher);
        void onDeleteClick(Teacher teacher);
    }

    public TeachersAdapter(List<Teacher> teachersList, OnTeacherClickListener listener) {
        this.teachersList = teachersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = teachersList.get(position);
        holder.bind(teacher, listener);
    }

    @Override
    public int getItemCount() {
        return teachersList.size();
    }

    static class TeacherViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTeacherCode, tvTeacherName, tvTeacherEmail, tvDepartmentName, tvUsername;
        private ImageButton btnEdit, btnDelete;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTeacherCode = itemView.findViewById(R.id.tvTeacherCode);
            tvTeacherName = itemView.findViewById(R.id.tvTeacherName);
            tvTeacherEmail = itemView.findViewById(R.id.tvTeacherEmail);
            tvDepartmentName = itemView.findViewById(R.id.tvDepartmentName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Teacher teacher, OnTeacherClickListener listener) {
            tvTeacherCode.setText("ID: " + teacher.getTeacherId()); // Hiển thị ID thay vì code
            tvTeacherName.setText(teacher.getTeacherFullName());
            tvTeacherEmail.setText(teacher.getTeacherEmail());
            tvDepartmentName.setText(teacher.getDepartmentName() != null ? teacher.getDepartmentName() : "Chưa có khoa");
            tvUsername.setText("@" + (teacher.getUsername() != null ? teacher.getUsername() : "N/A"));

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(teacher);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(teacher);
                }
            });
        }
    }
} 