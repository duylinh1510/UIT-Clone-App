package com.example.doan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
//Cách hoạt động:
//Adapter nhận danh sách sinh viên và listener
//RecyclerView yêu cầu tạo ViewHolder cho mỗi item hiển thị
//Dữ liệu được bind vào các view trong ViewHolder
//Khi user click nút Edit/Delete, callback được gọi để xử lý

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentViewHolder> {
    
    private List<AdminStudent> students;
    private OnStudentClickListener listener;

    public interface OnStudentClickListener {
        //Định nghĩa callback interface để xử lý sự kiện click
        //Có 2 phương thức: chỉnh sửa và xóa sinh viên
        void onEditClick(AdminStudent student);
        void onDeleteClick(AdminStudent student);
    }

    //Nhận vào danh sách sinh viên và listener để xử lý sự kiện
    //Lưu trữ thành các biến instance
    public StudentsAdapter(List<AdminStudent> students, OnStudentClickListener listener) {
        this.students = students;
        this.listener = listener;
    }

    //Tạo ViewHolder mới từ layout item_admin_student
    //Được gọi khi RecyclerView cần tạo item view mới
    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_student, parent, false);
        return new StudentViewHolder(view);
    }

    //Gắn dữ liệu sinh viên vào ViewHolder tại vị trí cụ thể
    //Gọi phương thức bind() của ViewHolder
    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        AdminStudent student = students.get(position);
        holder.bind(student);
    }

    //getItemCount():
    //Trả về số lượng item trong danh sách
    @Override
    public int getItemCount() {
        return students.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentCode, tvStudentName, tvEmail, tvDepartment, tvClass;
        private ImageButton btnEdit, btnDelete;
        //Quản lý các view trong một item của danh sách:
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
        //Phương thức bind():
        //Gán dữ liệu từ đối tượng AdminStudent vào các TextView
        //Thiết lập OnClickListener cho 2 nút Edit và Delete
        //Khi click sẽ gọi callback thông qua interface OnStudentClickListener
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