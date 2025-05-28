package com.example.doan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TimetablesAdapter extends RecyclerView.Adapter<TimetablesAdapter.TimetableViewHolder> {
    
    private List<AdminTimetable> timetablesList;
    private Context context;
    private OnTimetableActionListener listener;

    public interface OnTimetableActionListener {
        void onEditTimetable(AdminTimetable timetable);
        void onDeleteTimetable(AdminTimetable timetable);
        void onViewStudents(AdminTimetable timetable);
    }

    public TimetablesAdapter(Context context, List<AdminTimetable> timetablesList) {
        this.context = context;
        this.timetablesList = timetablesList;
    }

    public void setOnTimetableActionListener(OnTimetableActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timetable, parent, false);
        return new TimetableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableViewHolder holder, int position) {
        AdminTimetable timetable = timetablesList.get(position);
        
        holder.tvDayPeriod.setText(timetable.getDayName() + " - " + timetable.getPeriodText());
        holder.tvTimeRange.setText(timetable.getTimeRange());
        holder.tvSubjectInfo.setText(timetable.getSubjectInfo());
        holder.tvSubjectClass.setText("Lớp: " + timetable.getSubjectClassCode() + " - " + timetable.getSemester());
        holder.tvTeacher.setText("GV: " + timetable.getTeacherFullName());
        holder.tvDepartment.setText("Khoa: " + timetable.getDepartmentName());

        // Set click listener for entire item to view students
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewStudents(timetable);
            }
        });

        // Set click listeners for action buttons
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditTimetable(timetable);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteTimetable(timetable);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timetablesList.size();
    }

    public void updateTimetables(List<AdminTimetable> newTimetables) {
        this.timetablesList = newTimetables;
        notifyDataSetChanged();
    }

    static class TimetableViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayPeriod, tvTimeRange, tvSubjectInfo, tvSubjectClass, tvTeacher, tvDepartment;
        ImageButton btnEdit, btnDelete;

        public TimetableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayPeriod = itemView.findViewById(R.id.tvDayPeriod);
            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            tvSubjectInfo = itemView.findViewById(R.id.tvSubjectInfo);
            tvSubjectClass = itemView.findViewById(R.id.tvSubjectClass);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 