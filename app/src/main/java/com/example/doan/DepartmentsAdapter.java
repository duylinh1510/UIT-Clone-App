package com.example.doan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DepartmentsAdapter extends RecyclerView.Adapter<DepartmentsAdapter.DepartmentViewHolder> {

    private List<Department> departmentsList;
    private OnDepartmentClickListener listener;

    public interface OnDepartmentClickListener {
        void onEditClick(Department department);
        void onDeleteClick(Department department);
        void onViewClassesClick(Department department);
    }

    public DepartmentsAdapter(List<Department> departmentsList, OnDepartmentClickListener listener) {
        this.departmentsList = departmentsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DepartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_department, parent, false);
        return new DepartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentViewHolder holder, int position) {
        Department department = departmentsList.get(position);
        holder.bind(department, listener);
    }

    @Override
    public int getItemCount() {
        return departmentsList.size();
    }

    public void updateList(List<Department> newList) {
        this.departmentsList = newList;
        notifyDataSetChanged();
    }

    static class DepartmentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDepartmentCode, tvDepartmentName;
        private ImageButton btnEdit, btnDelete;

        public DepartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDepartmentCode = itemView.findViewById(R.id.tvDepartmentCode);
            tvDepartmentName = itemView.findViewById(R.id.tvDepartmentName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Department department, OnDepartmentClickListener listener) {
            tvDepartmentCode.setText(department.getDepartmentCode());
            tvDepartmentName.setText(department.getDepartmentName());

            // Click vào toàn bộ item để xem các lớp thuộc khoa
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewClassesClick(department);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(department);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(department);
                }
            });
        }
    }
} 