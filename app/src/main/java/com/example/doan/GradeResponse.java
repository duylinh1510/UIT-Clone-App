package com.example.doan;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class GradeResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<SemesterGrade> data;

    public GradeResponse() {}

    public GradeResponse(boolean success, String message, List<SemesterGrade> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<SemesterGrade> getData() { return data; }
    public void setData(List<SemesterGrade> data) { this.data = data; }
}