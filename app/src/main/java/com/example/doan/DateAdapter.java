package com.example.doan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private List<String> dates;
    private OnDateClickListener onDateClickListener;

    public DateAdapter(List<String> dates, OnDateClickListener listener) {
        this.dates = dates;
        this.onDateClickListener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        holder.bind(dates.get(position));
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        private TextView dateText;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.txtDate);
        }

        public void bind(String date) {
            dateText.setText(date);
            itemView.setOnClickListener(v -> onDateClickListener.onDateClicked(String.valueOf(dateText))); // Đảm bảo truyền đúng TextView
        }
    }

    public interface OnDateClickListener {
        void onDateClicked(String selectedDate);
    }
}


