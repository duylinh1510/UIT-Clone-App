package com.example.doan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DepartmentClassesAdapter extends RecyclerView.Adapter<DepartmentClassesAdapter.ClassViewHolder> {

    private List<ProgramClass> classesList;
    private OnClassClickListener listener;

    public interface OnClassClickListener {
        void onEditClick(ProgramClass programClass);
        void onDeleteClick(ProgramClass programClass);
    }

    public DepartmentClassesAdapter(List<ProgramClass> classesList, OnClassClickListener listener) {
        this.classesList = classesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_department_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ProgramClass programClass = classesList.get(position);
        holder.bind(programClass, listener);
    }

    @Override
    public int getItemCount() {
        return classesList.size();
    }

    public void updateList(List<ProgramClass> newList) {
        this.classesList = newList;
        notifyDataSetChanged();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView tvClassCode, tvYear, tvTeacherName, tvStudentCount;
        private ImageButton btnEdit, btnDelete;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassCode = itemView.findViewById(R.id.tvClassCode);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvTeacherName = itemView.findViewById(R.id.tvTeacherName);
            tvStudentCount = itemView.findViewById(R.id.tvStudentCount);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(ProgramClass programClass, OnClassClickListener listener) {
            tvClassCode.setText(programClass.getProgramClassCode());
            tvYear.setText("Năm: " + programClass.getYear());
            
            // Hiển thị tên giáo viên chủ nhiệm nếu có
            if (programClass.getTeacherName() != null && !programClass.getTeacherName().isEmpty()) {
                tvTeacherName.setText("GVCN: " + programClass.getTeacherName());
                tvTeacherName.setVisibility(View.VISIBLE);
            } else {
                tvTeacherName.setVisibility(View.GONE);
            }
            
            // Hiển thị số lượng sinh viên nếu có
            if (programClass.getStudentCount() > 0) {
                tvStudentCount.setText("Sĩ số: " + programClass.getStudentCount() + " sinh viên");
                tvStudentCount.setVisibility(View.VISIBLE);
            } else {
                tvStudentCount.setVisibility(View.GONE);
            }

            // Set click listeners for buttons
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(programClass);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(programClass);
                }
            });
        }
    }
} 