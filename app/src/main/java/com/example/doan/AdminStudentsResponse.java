package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminStudentsResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private List<AdminStudent> data;
    
    @SerializedName("pagination")
    private Pagination pagination;

    public static class Pagination {
        @SerializedName("page")
        private int page;
        
        @SerializedName("limit")
        private int limit;
        
        @SerializedName("total")
        private int total;
        
        @SerializedName("pages")
        private int pages;

        // Getters and Setters
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        
        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }
        
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        
        public int getPages() { return pages; }
        public void setPages(int pages) { this.pages = pages; }
    }

    // Constructors
    public AdminStudentsResponse() {}

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<AdminStudent> getData() {
        return data;
    }

    public void setData(List<AdminStudent> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    @Override
    public String toString() {
        return "AdminStudentsResponse{" +
                "success=" + success +
                ", data=" + data +
                ", pagination=" + pagination +
                '}';
    }
} 