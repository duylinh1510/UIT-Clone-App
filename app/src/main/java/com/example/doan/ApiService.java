package com.example.doan;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

//Đây là một interface ApiService sử dụng thư viện Retrofit để định nghĩa các API endpoints
//cho một ứng dụng quản lý sinh viên (có thể là hệ thống quản lý trường học).
public interface ApiService {
    // Login API
    @POST("login.php")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // Student Profile API
    @GET("student_profile.php")
    Call<StudentProfileResponse> getStudentProfile(@Query("student_id") int studentId);

    // Exam Schedule API
    @GET("get_exam_schedule.php")
    Call<ApiResponse> getExamSchedule(@Query("student_id") int studentId);

    @GET("student.php")
    Call<GradeResponse> getStudentGrades(@Query("student_id") int studentId);

    @GET("semester.php")
    Call<GradeResponse> getStudentGradesBySemester(
            @Query("student_id") int studentId,
            @Query("semester") String semester
    );

    // ==== ADMIN APIs ====
    
    // Students Management
    @GET("admin/students.php")
    Call<AdminStudentsResponse> getStudents(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("search") String search,
            @Query("department_id") Integer departmentId
    );

    @POST("admin/students.php")
    Call<AdminResponse> createStudent(@Body CreateStudentRequest request);

    @PUT("admin/students.php")
    Call<AdminResponse> updateStudent(@Body UpdateStudentRequest request);

    @DELETE("admin/students.php")
    Call<AdminResponse> deleteStudent(@Query("student_id") int studentId);

    // Departments Management
    @GET("admin/departments.php")
    Call<DepartmentsResponse> getDepartments();

    @POST("admin/departments.php")
    Call<AdminResponse> createDepartment(@Body CreateDepartmentRequest request);

    @PUT("admin/departments.php")
    Call<AdminResponse> updateDepartment(@Body UpdateDepartmentRequest request);

    @DELETE("admin/departments.php")
    Call<AdminResponse> deleteDepartment(@Query("department_id") int departmentId);

    // Grades Management
    @GET("admin/grades.php")
    Call<AdminGradesResponse> getGrades(
            @Query("student_id") Integer studentId,
            @Query("subject_class_id") Integer subjectClassId,
            @Query("semester") String semester
    );

    @POST("admin/grades.php")
    Call<AdminResponse> createGrade(@Body CreateGradeRequest request);

    @PUT("admin/grades.php")
    Call<AdminResponse> updateGrade(@Body UpdateGradeRequest request);

    @DELETE("admin/grades.php")
    Call<AdminResponse> deleteGrade(@Query("grade_id") int gradeId);

    // Teachers Management
    @GET("admin/teachers.php")
    Call<TeachersResponse> getTeachers(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("search") String search,
            @Query("department_id") Integer departmentId
    );

    @POST("admin/teachers.php")
    Call<AdminResponse> createTeacher(@Body CreateTeacherRequest request);

    @PUT("admin/teachers.php")
    Call<AdminResponse> updateTeacher(@Body UpdateTeacherRequest request);

    @DELETE("admin/teachers.php")
    Call<AdminResponse> deleteTeacher(@Query("teacher_id") int teacherId);

    // Subjects Management
    @GET("admin/subjects.php")
    Call<SubjectsResponse> getSubjects(
            @Query("search") String search,
            @Query("department_id") Integer departmentId
    );

    @POST("admin/subjects.php")
    Call<AdminResponse> createSubject(@Body CreateSubjectRequest request);

    @PUT("admin/subjects.php")
    Call<AdminResponse> updateSubject(@Body UpdateSubjectRequest request);

    @DELETE("admin/subjects.php")
    Call<AdminResponse> deleteSubject(@Query("subject_id") int subjectId);
    
    // Program Classes Management
    @GET("admin/program_classes.php")
    Call<ProgramClassesResponse> getProgramClassesByDepartment(@Query("department_id") int departmentId);
    
    @POST("admin/program_classes.php")
    Call<AdminResponse> createProgramClass(@Body CreateProgramClassRequest request);
    
    @PUT("admin/program_classes.php")
    Call<AdminResponse> updateProgramClass(@Body UpdateProgramClassRequest request);
    
    @DELETE("admin/program_classes.php")
    Call<AdminResponse> deleteProgramClass(@Query("program_class_id") int programClassId);

    // Subject Classes Management
    @GET("admin/subject_classes.php")
    Call<SubjectClassesResponse> getSubjectClasses(
            @Query("semester") String semester,
            @Query("subject_id") Integer subjectId
    );
    
    @POST("admin/subject_classes.php")
    Call<AdminResponse> createSubjectClass(@Body CreateSubjectClassRequest request);
    
    @PUT("admin/subject_classes.php")
    Call<AdminResponse> updateSubjectClass(@Body UpdateSubjectClassRequest request);
    
    @PUT("admin/subject_classes.php")
    Call<AdminResponse> updateSubjectClassTeacher(@Body UpdateSubjectClassTeacherRequest request);
    
    @DELETE("admin/subject_classes.php")
    Call<AdminResponse> deleteSubjectClass(@Query("subject_class_id") int subjectClassId);

    // Timetables Management
    @GET("admin/timetables.php")
    Call<AdminTimetablesResponse> getTimetables(
            @Query("search") String search,
            @Query("day_of_week") Integer dayOfWeek,
            @Query("subject_class_id") Integer subjectClassId
    );

    @POST("admin/timetables.php")
    Call<AdminResponse> createTimetable(@Body CreateTimetableRequest request);

    @PUT("admin/timetables.php")
    Call<AdminResponse> updateTimetable(@Body UpdateTimetableRequest request);

    @DELETE("admin/timetables.php")
    Call<AdminResponse> deleteTimetable(@Query("timetable_id") int timetableId);
    
    // Timetable Students Management
    @GET("admin/timetable_students.php")
    Call<TimetableStudentsResponse> getTimetableStudents(@Query("timetable_id") int timetableId);
    
    @POST("admin/timetable_students.php")
    Call<AdminResponse> addStudentToTimetable(@Body AddStudentToTimetableRequest request);
    
    @DELETE("admin/timetable_students.php")
    Call<AdminResponse> removeStudentFromTimetable(@Query("enrollment_id") int enrollmentId);
    
    // Available Students for Timetable
    @GET("admin/available_students.php")
    Call<AvailableStudentsResponse> getAvailableStudents(
            @Query("subject_class_id") int subjectClassId,
            @Query("search") String search
    );
}