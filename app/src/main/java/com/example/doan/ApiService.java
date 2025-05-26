package com.example.doan;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("get_exam_schedule.php")
    Call<ApiResponse> getExamSchedule(@Query("student_id") int studentID);
}
