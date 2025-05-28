package com.example.doan;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private LoginData data;

    public static class LoginData {
        @SerializedName("user_id")
        private int userId;

        @SerializedName("username")
        private String username;

        @SerializedName("role")
        private String role;

        @SerializedName("profile")
        private StudentProfile profile; // Có thể là AdminProfile hoặc StudentProfile

        // Getters and Setters
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public StudentProfile getProfile() { return profile; }
        public void setProfile(StudentProfile profile) { this.profile = profile; }
    }

    public LoginResponse() {}

    public LoginResponse(boolean success, String message, LoginData data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LoginData getData() { return data; }
    public void setData(LoginData data) { this.data = data; }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
