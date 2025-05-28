package com.example.doan;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ScheduleApiService {
    @GET("get_schedule.php")
    Call<ScheduleResponse> getSchedule(@Query("day_of_week") int dayOfWeek, @Query("student_id") int studentId);
}

