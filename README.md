# 📚 HỆ THỐNG QUẢN LÝ SINH VIÊN - ANDROID APP

## 🏗️ TỔNG QUAN KIẾN TRÚC

Đây là ứng dụng Android quản lý sinh viên sử dụng kiến trúc **MVC (Model-View-Controller)** với:
- **View**: Activities + XML layouts  
- **Model**: Java POJOs + API Response classes
- **Controller**: Activities + Adapters + API Service

## 📱 LUỒNG HOẠT ĐỘNG CHÍNH

```
    Admin: AdminActivity → [Quản lý Students/Teachers/Subjects/Grades/Departments/Timetables]
                    ↑
LoginActivity → MainActivity → [Student View | Admin View]
                    ↓
    Student: ScheduleActivity, GradeActivity, , ProfileActivity, LichThiActivity               

```

---

## 🔐 1. HỆ THỐNG ĐĂNG NHẬP & AUTHENTICATION

### `LoginActivity.java`
- **Chức năng**: Màn hình đăng nhập đầu tiên
- **Layout**: `activity_login.xml` với TextInputLayout cho username/password
- **Luồng**: 
  1. User nhập username/password
  2. Gửi `LoginRequest` tới server qua `ApiService.login()`
  3. Nhận `LoginResponse` chứa user info và token
  4. `SessionManager` lưu session
  5. Điều hướng tới `MainActivity` hoặc `AdminActivity`

### `SessionManager.java`
- **Chức năng**: Quản lý session user, lưu/đọc thông tin đăng nhập
- **Methods chính**:
  - `saveUserSession()`: Lưu thông tin user vào SharedPreferences
  - `getUserSession()`: Đọc thông tin user
  - `clearSession()`: Đăng xuất

### Models liên quan:
- `LoginRequest.java`: Dữ liệu gửi đi (username, password)
- `LoginResponse.java`: Dữ liệu nhận về (user info, role, token)

---

## 🏠 2. MAIN NAVIGATION

### `MainActivity.java`
- **Chức năng**: Màn hình chính cho sinh viên
- **Layout**: `activity_main.xml` với BottomNavigationView
- **Kết nối**: Navigation tới 4 màn hình chính:
  - `GradeActivity` - Xem điểm
  - `ScheduleActivity` - Thời khóa biểu  
  - `ProfileActivity` - Thông tin cá nhân
  - `LichThiActivity` - Lịch thi

### `BaseActivity.java`
- **Chức năng**: Activity cha cung cấp common functionality
- **Features**: Toolbar setup, common methods

---

## 👨‍🎓 3. CHỨC NĂNG SINH VIÊN

### `GradeActivity.java`
- **Chức năng**: Hiển thị bảng điểm theo học kỳ
- **Layout**: `activity_grade.xml` với RecyclerView
- **Luồng**: 
  1. Load `StudentProfile` từ API
  2. Gọi `ApiService.getStudentGrades()` 
  3. Hiển thị qua custom adapter với `item_semester_grade.xml`
- **Models**: `Grade.java`, `SemesterGrade.java`, `GradeResponse.java`

### `ScheduleActivity.java`  
- **Chức năng**: Hiển thị thời khóa biểu tuần
- **Layout**: `activity_thoikhoabieu.xml` với GridLayout 7x6
- **Luồng**:
  1. `WeekUtils.java` tính toán tuần hiện tại
  2. `ScheduleApiService.getSchedule()` load thời khóa biểu
  3. Hiển thị grid 7x6 (thứ x tiết)
- **Models**: `Schedule.java`, `ScheduleResponse.java`

### `ProfileActivity.java`
- **Chức năng**: Hiển thị & chỉnh sửa thông tin cá nhân
- **API**: `getStudentProfile()` 
- **Models**: `StudentProfile.java`, `StudentProfileResponse.java`

### `LichThiActivity.java`
- **Chức năng**: Xem lịch thi
- **Models**: `ExamSchedule.java`

---

## 👩‍💼 4. HỆ THỐNG ADMIN

### `AdminActivity.java`
- **Chức năng**: Dashboard admin với 6 menu chính
- **Layout**: `activity_admin.xml` với GridLayout 2x3
- **Navigation tới**:
  - `AdminStudentsActivity` - Quản lý sinh viên
  - `AdminTeachersActivity` - Quản lý giáo viên  
  - `AdminSubjectsActivity` - Quản lý môn học
  - `AdminGradesActivity` - Quản lý điểm
  - `AdminDepartmentsActivity` - Quản lý khoa
  - `AdminTimetablesActivity` - Quản lý thời khóa biểu

---

## 📊 5. QUẢN LÝ SINH VIÊN

### `AdminStudentsActivity.java`
- **Chức năng**: Danh sách và tìm kiếm sinh viên
- **Layout**: `activity_admin_students.xml` với SearchView + Spinner + RecyclerView + FAB
- **Features**: 
  - Pagination (page, limit)
  - Search theo tên
  - Filter theo khoa
  - CRUD operations

### `AddEditStudentActivity.java`
- **Chức năng**: Thêm mới/chỉnh sửa sinh viên
- **Layout**: `activity_add_edit_student.xml` với TextInputLayout forms
- **Validation**: Kiểm tra các trường bắt buộc
- **API**: `createStudent()` / `updateStudent()`

### Models & Requests:
- `AdminStudent.java`: Model sinh viên với đầy đủ thông tin
- `CreateStudentRequest.java` / `UpdateStudentRequest.java`: Dữ liệu gửi API
- `AdminStudentsResponse.java`: Response từ server

### `StudentsAdapter.java`
- **Chức năng**: Adapter hiển thị list sinh viên trong RecyclerView
- **Layout**: `item_admin_student.xml` với CardView + action buttons
- **Actions**: Edit, Delete buttons

---

## 👨‍🏫 6. QUẢN LÝ GIÁO VIÊN

### `AdminTeachersActivity.java`
- **Chức năng**: Tương tự AdminStudentsActivity nhưng cho giáo viên
- **Features**: Search, filter theo khoa, pagination

### `AddEditTeacherActivity.java`
- **Chức năng**: Form thêm/sửa giáo viên
- **Validation**: Email format, required fields

### Models liên quan:
- `Teacher.java`, `CreateTeacherRequest.java`, `UpdateTeacherRequest.java`
- `TeachersResponse.java`, `TeachersAdapter.java`

---

## 📚 7. QUẢN LÝ MÔN HỌC & LỚP HỌC

### `AdminSubjectsActivity.java`
- **Chức năng**: Danh sách môn học với search và filter theo khoa
- **Layout**: `activity_admin_subjects.xml` 
- **Navigation**: Click "Xem lớp" → `SubjectClassesActivity`

### `SubjectClassesActivity.java` ⭐ **CORE FEATURE**
- **Chức năng**: Quản lý các lớp của 1 môn học cụ thể
- **Layout**: `activity_subject_classes.xml` với CoordinatorLayout + RecyclerView + FAB
- **Features**:
  - Hiển thị danh sách lớp (mã lớp, học kỳ, giáo viên)
  - Edit lớp học (mã lớp, học kỳ, giáo viên)
  - Delete lớp học
  - Add lớp học mới
- **Adapter**: `SubjectClassesAdapter.java` với callbacks: `onEditClass()`, `onDeleteClass()`

### `AddEditSubjectClassActivity.java` ⭐ **NEW FUNCTIONALITY**
- **Chức năng**: Form thêm/sửa thông tin lớp học
- **Layout**: `activity_add_edit_subject_class.xml`
- **Components**:
  - TextInputLayout: Mã lớp, Học kỳ
  - Spinner: Chọn giáo viên (load từ API)
  - Button: Save/Update
- **Validation**: Kiểm tra mã lớp unique, giáo viên được chọn

### Models liên quan:
- `Subject.java`, `SubjectClass.java`
- `CreateSubjectClassRequest.java`, `UpdateSubjectClassRequest.java`
- `SubjectClassesResponse.java`

---

## 🏛️ 8. QUẢN LÝ KHOA & LỚP CHUYÊN NGÀNH

### `AdminDepartmentsActivity.java`
- **Chức năng**: CRUD khoa
- **Navigation**: Click khoa → `DepartmentClassesActivity`

### `DepartmentClassesActivity.java`
- **Chức năng**: Quản lý các lớp chuyên ngành của khoa
- **Models**: `ProgramClass.java`, `DepartmentClassesAdapter.java`

### `AddEditProgramClassActivity.java`
- **Chức năng**: Thêm/sửa lớp chuyên ngành
- **Models**: `CreateProgramClassRequest.java`, `UpdateProgramClassRequest.java`

---

## 📊 9. QUẢN LÝ ĐIỂM

### `AdminGradesActivity.java`
- **Chức năng**: Xem và tìm kiếm điểm theo nhiều tiêu chí
- **Filters**: Theo sinh viên, lớp học, học kỳ

### `AddEditGradeActivity.java`
- **Chức năng**: Nhập/sửa điểm cho sinh viên
- **Components**: Spinner chọn sinh viên, lớp học, nhập điểm các loại

### Models:
- `AdminGrade.java`, `GradesAdapter.java`
- `CreateGradeRequest.java`, `UpdateGradeRequest.java`

---

## 📅 10. QUẢN LÝ THỜI KHÓA BIỂU

### `AdminTimetablesActivity.java`
- **Chức năng**: Quản lý lịch học theo lớp
- **Features**: Search, filter theo thứ, lớp học

### `AddEditTimetableActivity.java`
- **Chức năng**: Tạo/sửa lịch học
- **Components**: Chọn lớp học, thứ, tiết, phòng

### `TimetableStudentsActivity.java`
- **Chức năng**: Quản lý sinh viên trong lớp học
- **Features**: Add/remove sinh viên khỏi lớp

### `AddStudentToTimetableActivity.java`
- **Chức năng**: Thêm sinh viên vào lớp học

### Models:
- `AdminTimetable.java`, `TimetableStudent.java`
- `TimetablesAdapter.java`, `TimetableStudentsAdapter.java`
- `AddStudentToTimetableRequest.java`, `AvailableStudentsResponse.java`

---

## 🌐 11. HỆ THỐNG API & NETWORKING

### `ApiClient.java`
- **Chức năng**: Singleton cấu hình Retrofit client
- **Base URL**: Kết nối tới server PHP
- **Interceptors**: Logging requests/responses

### `ApiService.java` ⭐ **CENTRAL API INTERFACE**
- **Chức năng**: Interface định nghĩa tất cả API endpoints
- **Methods**: GET, POST, PUT, DELETE cho từng module
- **Groups**:
  - **Authentication**: `login()`
  - **Students**: `getStudents()`, `createStudent()`, `updateStudent()`, `deleteStudent()`
  - **Teachers**: `getTeachers()`, `createTeacher()`, `updateTeacher()`, `deleteTeacher()`
  - **Subjects**: `getSubjects()`, `createSubject()`, `updateSubject()`, `deleteSubject()`
  - **Subject Classes**: `getSubjectClasses()`, `createSubjectClass()`, `updateSubjectClass()`, `deleteSubjectClass()`
  - **Grades**: `getGrades()`, `createGrade()`, `updateGrade()`, `deleteGrade()`
  - **Departments**: `getDepartments()`, `createDepartment()`, `updateDepartment()`, `deleteDepartment()`
  - **Program Classes**: `getProgramClasses()`, `createProgramClass()`, `updateProgramClass()`, `deleteProgramClass()`
  - **Timetables**: `getTimetables()`, `createTimetable()`, `updateTimetable()`, `deleteTimetable()`
  - **Student Enrollments**: `getTimetableStudents()`, `addStudentToTimetable()`, `removeStudentFromTimetable()`

### Response Models:
- `AdminResponse.java`: Generic response (success, message)
- `XXXResponse.java`: Specific responses với data array

---

## 🛠️ 12. UTILITIES & HELPERS

### `WeekUtils.java`
- **Chức năng**: Tính toán tuần học, ngày trong tuần
- **Methods**: `getCurrentWeek()`, `getWeekDates()`, etc.

### `ApiResponse.java`
- **Chức năng**: Generic API response wrapper

---

## 📱 13. LAYOUTS & XML FILES ANALYSIS

### Main Activity Layouts:
- **`activity_login.xml`**: 
  - ScrollView + LinearLayout
  - TextInputLayout với Material Design style
  - Button với ripple effect
  - Background gradient

- **`activity_main.xml`**: 
  - CoordinatorLayout chứa BottomNavigationView
  - 4 menu items: Điểm, Lịch học, Profile, Lịch thi

- **`activity_admin.xml`**: 
  - GridLayout 2x3 với 6 CardViews
  - Mỗi card có icon + text
  - Material Design elevation

### Admin Management Layouts:
- **`activity_admin_students.xml`**:
  ```xml
  CoordinatorLayout
    ├── AppBarLayout (Toolbar + SearchView)
    ├── LinearLayout (Filter Spinner + Status)
    ├── RecyclerView (Student list)
    └── FloatingActionButton (Add new)
  ```

- **`activity_add_edit_student.xml`**:
  ```xml
  ScrollView
    └── LinearLayout
        ├── TextInputLayout (Họ tên)
        ├── TextInputLayout (MSSV)
        ├── TextInputLayout (Email)
        ├── Spinner (Khoa)
        ├── Spinner (Lớp chuyên ngành)
        └── Button (Save/Update)
  ```

- **`activity_subject_classes.xml`**:
  ```xml
  CoordinatorLayout
    ├── AppBarLayout (Toolbar + Subject info)
    ├── RecyclerView (Subject classes list)
    └── FloatingActionButton (Add new class)
  ```

- **`activity_add_edit_subject_class.xml`**:
  ```xml
  LinearLayout
    ├── TextInputLayout (Mã lớp học)
    ├── TextInputLayout (Học kỳ) 
    ├── Spinner (Chọn giáo viên)
    └── Button (Save/Update)
  ```

### Item Layouts:
- **`item_admin_student.xml`**:
  ```xml
  CardView
    └── LinearLayout
        ├── TextView (Student info display)
        └── LinearLayout (Edit + Delete buttons)
  ```

- **`item_subject_class.xml`**:
  ```xml
  CardView
    └── LinearLayout
        ├── TextView (Class code, semester, teacher)
        └── LinearLayout
            ├── Button (Edit - Blue)
            └── Button (Delete - Red)
  ```

- **`item_semester_grade.xml`**: Card hiển thị điểm theo học kỳ
- **`item_timetable.xml`**: Card hiển thị thông tin lịch học

### Common UI Components:
- **Material Design**: TextInputLayout, CardView, FAB
- **Color Scheme**: Primary blue, accent colors
- **Typography**: Roboto font family
- **Spacing**: 16dp margins, 8dp padding
- **Elevation**: 4dp cho cards, 8dp cho FAB

---

## 🔄 14. LUỒNG DỮ LIỆU CHI TIẾT

### Luồng tổng quát:
```
1. USER INPUT (Activities) 
   ↓
2. VALIDATION & REQUEST CREATION (Request classes)
   ↓  
3. API CALL (ApiService + Retrofit)
   ↓
4. SERVER PROCESSING (PHP backend)
   ↓
5. RESPONSE PARSING (Response classes)
   ↓
6. DATA BINDING (Models + Adapters)
   ↓
7. UI UPDATE (RecyclerViews + Layouts)
```

### Ví dụ cụ thể - Cập nhật lớp học:
```
AddEditSubjectClassActivity (user input)
    ↓
UpdateSubjectClassRequest (data preparation)  
    ↓
ApiService.updateSubjectClass() (API call)
    ↓
admin/subject_classes.php (server processing)
    ↓
AdminResponse (response parsing)
    ↓
SubjectClassesActivity.loadSubjectClasses() (refresh data)
    ↓
SubjectClassesAdapter.notifyDataSetChanged() (UI update)
```

### Luồng xử lý error:
```
API Call fails → Retrofit onFailure() → Show error Toast/SnackBar → Log error
Server returns success:false → Parse error message → Show error to user
Network timeout → Show "Kiểm tra kết nối mạng" → Retry option
```

---

## 🏆 15. DESIGN PATTERNS SỬ DỤNG

1. **Singleton**: `ApiClient`, `SessionManager`
2. **Observer**: RecyclerView Adapters với OnClickListeners
3. **Builder**: Retrofit.Builder, AlertDialog.Builder
4. **Repository**: ApiService như data access layer
5. **MVC**: Clear separation of concerns
6. **Factory**: ApiClient factory cho Retrofit instances
7. **Adapter Pattern**: RecyclerView.Adapter implementations
8. **Strategy Pattern**: Different validation strategies
9. **Template Method**: BaseActivity với common functionality

---

## 🔗 16. MỐI QUAN HỆ GIỮA CÁC COMPONENTS

### Vertical Flow (theo chức năng):
```
AdminActivity 
  → AdminSubjectsActivity 
    → SubjectClassesActivity 
      → AddEditSubjectClassActivity
```

### Horizontal Flow (cùng cấp):
```
AdminStudentsActivity ← → AdminTeachersActivity ← → AdminSubjectsActivity
```

### Data Flow:
```
Models ← → Request/Response ← → ApiService ← → Server
```

### UI Flow:
```
Activities ← → Adapters ← → Layouts ← → User Interaction
```

### Dependency Graph:
```
Activities depend on:
  ├── Models (data representation)
  ├── ApiService (data access)
  ├── Adapters (UI binding)
  └── Layouts (UI structure)

Adapters depend on:
  ├── Models (data binding)
  ├── Layouts (item views)
  └── Click Listeners (user interaction)

ApiService depends on:
  ├── Request classes (input data)
  ├── Response classes (output data)
  └── Retrofit (networking)
```

---

## 🐛 17. DEBUGGING & ERROR HANDLING

### Common Issues và Solutions:

#### 1. API Connection Issues:
```java
// Trong ApiService calls
try {
    Response<XXXResponse> response = apiService.getXXX().execute();
    if (response.isSuccessful()) {
        // Handle success
    } else {
        Log.e("API_ERROR", "Code: " + response.code());
    }
} catch (IOException e) {
    Log.e("NETWORK_ERROR", "Network issue: " + e.getMessage());
}
```

#### 2. RecyclerView Update Issues:
```java
// Refresh data properly
private void loadData() {
    // Show loading
    progressBar.setVisibility(View.VISIBLE);
    
    // API call
    apiCall.enqueue(new Callback<Response>() {
        @Override
        public void onResponse(...) {
            adapter.updateData(newData);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    });
}
```

#### 3. Spinner Not Updating:
```java
// Load spinner data first, then set selection
private void loadTeachers() {
    ApiService.getTeachers().enqueue(new Callback<TeachersResponse>() {
        @Override
        public void onResponse(...) {
            teacherAdapter.updateData(teachers);
            if (editMode && currentTeacherId != null) {
                setTeacherSelection(currentTeacherId);
            }
        }
    });
}
```

### Logging Strategy:
```java
private static final String TAG = "ActivityName";

// Info logs for flow tracking
Log.i(TAG, "Starting data load for subject: " + subjectId);

// Debug logs for detailed tracking  
Log.d(TAG, "Received " + data.size() + " items from API");

// Error logs for problems
Log.e(TAG, "Failed to update: " + error.getMessage());

// Verbose logs for detailed debugging
Log.v(TAG, "Raw API response: " + response.toString());
```

---

## 📋 18. CONVENTIONS & BEST PRACTICES

### 1. Naming Conventions:
- **Activities**: `[Function]Activity.java` (e.g., `AddEditStudentActivity`)
- **Adapters**: `[Entity]Adapter.java` (e.g., `StudentsAdapter`) 
- **Models**: `[Entity].java` (e.g., `AdminStudent`)
- **Requests**: `[Action][Entity]Request.java` (e.g., `CreateStudentRequest`)
- **Responses**: `[Entity]Response.java` (e.g., `AdminStudentsResponse`)
- **Layouts**: `activity_[function].xml`, `item_[entity].xml`

### 2. Code Organization:
```
com.example.doan/
├── activities/          # All Activity classes
├── adapters/           # RecyclerView Adapters  
├── models/             # Data models
├── network/            # API related (ApiService, ApiClient)
├── requests/           # API request classes
├── responses/          # API response classes
├── utils/              # Utility classes
└── managers/           # SessionManager, etc.
```

### 3. Architecture Best Practices:
- **Single Responsibility**: Each class has one clear purpose
- **Separation of Concerns**: Activities handle UI, ApiService handles data
- **Consistent Error Handling**: All API calls have proper error handling
- **Resource Management**: Proper cleanup in onDestroy()
- **Memory Optimization**: Proper use of adapters, avoid memory leaks

### 4. UI/UX Best Practices:
- **Material Design**: Consistent use of Material components
- **Loading States**: ProgressBar for all async operations
- **User Feedback**: Toast/SnackBar for user actions
- **Validation**: Client-side validation before API calls
- **Accessibility**: Proper content descriptions

### 5. API Design:
- **RESTful URLs**: GET /subjects, POST /subjects, PUT /subjects/{id}
- **Consistent Response Format**: {success: boolean, message: string, data: array}
- **Proper HTTP Status Codes**: 200 OK, 400 Bad Request, 500 Server Error
- **Input Validation**: Both client and server side validation

---

## 🚀 19. PERFORMANCE OPTIMIZATIONS

### 1. RecyclerView Optimizations:
```java
// ViewHolder pattern implemented in all adapters
public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder {
        // Cache view references
    }
}

// Stable IDs for better performance
@Override
public long getItemId(int position) {
    return items.get(position).getId();
}
```

### 2. Image Loading:
```java
// Placeholder images for better UX
imageView.setImageResource(R.drawable.placeholder_user);
```

### 3. Network Optimizations:
```java
// Timeout configurations in ApiClient
OkHttpClient client = new OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build();
```

### 4. Memory Management:
```java
// Proper cleanup in activities
@Override
protected void onDestroy() {
    super.onDestroy();
    // Cancel ongoing API calls
    if (apiCall != null && !apiCall.isCanceled()) {
        apiCall.cancel();
    }
}
```

---

## 🔧 20. SETUP & DEPLOYMENT

### Development Setup:
1. **Clone Repository**: `git clone [repo-url]`
2. **Open in Android Studio**: Import as Android project
3. **Configure API Base URL**: Update `ApiClient.java` với server URL
4. **Build & Run**: Connect device/emulator and run

### Dependencies (build.gradle):
```gradle
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.9.0'
implementation 'com.google.android.material:material:1.4.0'
implementation 'androidx.recyclerview:recyclerview:1.2.1'
```

### Server Requirements:
- **PHP 7.4+** với PDO extension
- **MySQL 5.7+** database
- **Apache/Nginx** web server
- **CORS headers** configured for Android app

---

## 🚀 KẾT LUẬN

Đây là một hệ thống quản lý sinh viên hoàn chỉnh với:

### **Frontend (Android App)**:
- **42 Java Classes** với clear responsibilities
- **Modern UI** sử dụng Material Design
- **Robust Architecture** với MVC pattern
- **Complete CRUD** operations cho tất cả entities
- **Error Handling** và user feedback
- **Responsive Design** cho different screen sizes

### **Backend (PHP APIs)**:
- **RESTful Architecture** với proper HTTP methods
- **Secure Database** operations với prepared statements  
- **Consistent Response** format
- **Comprehensive Error** handling

### **Database (MySQL)**:
- **Normalized Schema** với proper relationships
- **Indexes** cho performance
- **Constraints** cho data integrity

### **Key Features**:
✅ **Authentication** với role-based access  
✅ **Student Management** với full CRUD  
✅ **Teacher Management** với department assignment  
✅ **Subject & Subject Classes** với teacher assignment  
✅ **Grade Management** với multiple grade types  
✅ **Department & Program Classes** management  
✅ **Timetable Management** với student enrollment  
✅ **Student Portal** với grades, schedule, profile  
✅ **Responsive UI** với modern Material Design  
✅ **Error Handling** và user feedback  

Mỗi component đều có trách nhiệm rõ ràng và kết nối chặt chẽ tạo thành một **ecosystem hoàn chỉnh** cho việc quản lý sinh viên trong môi trường giáo dục.

---

## 📞 SUPPORT & DOCUMENTATION

Để hiểu rõ hơn về implementation details, hãy xem:
- Source code comments trong từng file
- API documentation trong PHP files  
- Database schema trong `quanlysinhvien.sql`
- Test files để hiểu expected behavior 