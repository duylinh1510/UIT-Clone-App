package com.example.doan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder> {

    private List<Subject> subjectsList;
    private OnSubjectClickListener listener;

    public interface OnSubjectClickListener {
        void onEditClick(Subject subject);
        void onDeleteClick(Subject subject);
    }

    public SubjectsAdapter(List<Subject> subjectsList, OnSubjectClickListener listener) {
        this.subjectsList = subjectsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectsList.get(position);
        holder.bind(subject, listener);
    }

    @Override
    public int getItemCount() {
        return subjectsList.size();
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubjectCode, tvSubjectName, tvCredits, tvHours, tvDepartmentName;
        private ImageButton btnEdit, btnDelete;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectCode = itemView.findViewById(R.id.tvSubjectCode);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvCredits = itemView.findViewById(R.id.tvCredits);
            tvHours = itemView.findViewById(R.id.tvHours);
            tvDepartmentName = itemView.findViewById(R.id.tvDepartmentName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Subject subject, OnSubjectClickListener listener) {
            tvSubjectCode.setText(subject.getSubjectCode());
            tvSubjectName.setText(subject.getName());
            tvCredits.setText(subject.getCredits() + " tín chỉ");
            
            // Ẩn tvHours vì không có thông tin giờ học
            tvHours.setVisibility(View.GONE);
            
            tvDepartmentName.setText(subject.getDepartmentName() != null ? subject.getDepartmentName() : "Chưa có khoa");

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(subject);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(subject);
                }
            });
        }
    }
} 