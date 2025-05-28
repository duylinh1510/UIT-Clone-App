package com.example.doan;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TeachersResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("data")
    private List<Teacher> data;
    
    @SerializedName("pagination")
    private Pagination pagination;
    
    @SerializedName("message")
    private String message;

    // Constructors
    public TeachersResponse() {}

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Teacher> getData() {
        return data;
    }

    public void setData(List<Teacher> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Inner class for pagination
    public static class Pagination {
        @SerializedName("total")
        private int total;
        
        @SerializedName("page")
        private int page;
        
        @SerializedName("limit")
        private int limit;
        
        @SerializedName("totalPages")
        private int totalPages;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }
} 