# 📚 Hệ thống Quản lý Sinh viên (Student Management System)

## 📖 Tổng quan dự án

Đây là một hệ thống quản lý sinh viên hoàn chỉnh được xây dựng với kiến trúc **Android App + PHP API Backend + MySQL Database**. Hệ thống hỗ trợ 3 loại người dùng: **Admin**, **Giáo viên** và **Sinh viên**, mỗi loại có các chức năng riêng biệt phù hợp với vai trò của họ.

## 🏗️ Kiến trúc hệ thống

```
┌─────────────────┐    HTTP/JSON    ┌──────────────────┐    PDO/MySQL    ┌─────────────────┐
│   Android App   │ ◄───────────── │   PHP Backend    │ ◄─────────────► │  MySQL Database │
│   (Java/XML)    │                 │   (REST APIs)    │                 │  (quanlysinhvien)│
└─────────────────┘                 └──────────────────┘                 └─────────────────┘
```

## 🚀 Công nghệ sử dụng

### Frontend (Android App)
- **Ngôn ngữ**: Java
- **IDE**: Android Studio
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 14)
- **UI Framework**: Android XML Layouts
- **HTTP Client**: Retrofit 2.9.0 + OkHttp
- **JSON Parser**: Gson
- **UI Components**: 
  - Material Design Components 1.12.0
  - CardView 1.0.0
  - RecyclerView 1.3.2
  - SwipeRefreshLayout

### Backend (PHP APIs)
- **Ngôn ngữ**: PHP 8.2.12
- **Web Server**: Apache/Nginx
- **Database Driver**: PDO (PHP Data Objects)
- **API Style**: RESTful APIs
- **Authentication**: Session-based với role-based access control

### Database
- **RDBMS**: MySQL/MariaDB 10.4.32
- **Database Name**: `quanlysinhvien`
- **Charset**: utf8mb4

## 📊 Cấu trúc Database

### Các bảng chính:

#### 👤 Quản lý người dùng
- **`user`**: Thông tin đăng nhập (username, password, role)
- **`student`**: Thông tin sinh viên
- **`teacher`**: Thông tin giáo viên

#### 🏫 Quản lý tổ chức
- **`department`**: Khoa/Ngành
- **`program_class`**: Lớp học phần
- **`subject`**: Môn học
- **`subject_class`**: Lớp môn học (instance của môn học)

#### 📅 Quản lý lịch học
- **`timetable`**: Thời khóa biểu
- **`student_enrollment`**: Đăng ký học của sinh viên
- **`exam_schedule`**: Lịch thi

#### 📈 Quản lý điểm số
- **`grade`**: Điểm số (điểm quá trình, thực hành, giữa kỳ, cuối kỳ)

## 🎯 Chức năng hệ thống

### 👨‍💼 Chức năng Admin
#### Quản lý tổ chức
- **Quản lý khoa**: Thêm, sửa, xóa, tìm kiếm khoa
- **Quản lý lớp học**: Quản lý lớp thuộc từng khoa, phân công giáo viên chủ nhiệm
- **Quản lý môn học**: Thêm, sửa, xóa môn học theo khoa

#### Quản lý người dùng  
- **Quản lý sinh viên**: CRUD sinh viên, phân lớp, quản lý thông tin cá nhân
- **Quản lý giáo viên**: CRUD giáo viên, phân khoa

#### Quản lý học tập
- **Quản lý thời khóa biểu**: Tạo lịch học, phân công giảng dạy
- **Quản lý sinh viên trong lớp**: Thêm/xóa sinh viên khỏi lớp học cụ thể
- **Quản lý điểm số**: Nhập, chỉnh sửa điểm của sinh viên

### 👨‍🎓 Chức năng Sinh viên
- **Xem thông tin cá nhân**: Profile, thông tin lớp, khoa
- **Xem thời khóa biểu**: Lịch học hàng tuần theo ngày
- **Xem điểm số**: Điểm các môn học theo học kỳ, tính GPA
- **Xem lịch thi**: Lịch thi cuối kỳ

### 👩‍🏫 Chức năng Giáo viên
- **Xem thông tin cá nhân**: Profile giáo viên
- **Xem thời khóa biểu**: Lịch giảng dạy
- *(Có thể mở rộng thêm chức năng quản lý lớp do mình giảng dạy)*

## 📱 Cấu trúc Android App

### 📁 Cấu trúc thư mục chính

```
app/src/main/
├── java/com/example/doan/           # Source code Java
├── res/
│   ├── layout/                      # XML layouts
│   ├── values/                      # Colors, strings, themes
│   └── drawable/                    # Icons, images
└── AndroidManifest.xml              # App configuration
```

### 🏠 Activities chính

#### Authentication & Core
- **`LoginActivity`**: Màn hình đăng nhập
- **`MainActivity`**: Màn hình chính cho sinh viên/giáo viên  
- **`ProfileActivity`**: Màn hình profile người dùng
- **`BaseActivity`**: Base class với navigation drawer

#### Student Features
- **`ScheduleActivity`**: Xem thời khóa biểu
- **`GradeActivity`**: Xem điểm số
- **`LichThiActivity`**: Xem lịch thi

#### Admin Features
##### Quản lý tổ chức
- **`AdminActivity`**: Dashboard admin
- **`AdminDepartmentsActivity`**: Quản lý khoa
- **`AddEditDepartmentActivity`**: Thêm/sửa khoa
- **`DepartmentClassesActivity`**: Quản lý lớp trong khoa
- **`AddEditProgramClassActivity`**: Thêm/sửa lớp học

##### Quản lý người dùng
- **`AdminStudentsActivity`**: Quản lý sinh viên
- **`AddEditStudentActivity`**: Thêm/sửa sinh viên
- **`AdminTeachersActivity`**: Quản lý giáo viên  
- **`AddEditTeacherActivity`**: Thêm/sửa giáo viên

##### Quản lý học tập
- **`AdminSubjectsActivity`**: Quản lý môn học
- **`AddEditSubjectActivity`**: Thêm/sửa môn học
- **`AdminTimetablesActivity`**: Quản lý thời khóa biểu
- **`AddEditTimetableActivity`**: Thêm/sửa thời khóa biểu
- **`TimetableStudentsActivity`**: Quản lý sinh viên trong lớp
- **`AddStudentToTimetableActivity`**: Thêm sinh viên vào lớp
- **`AdminGradesActivity`**: Quản lý điểm số
- **`AddEditGradeActivity`**: Thêm/sửa điểm

### 🔧 Models & APIs

#### Network Layer
- **`ApiClient`**: Cấu hình Retrofit client
- **`ApiService`**: Interface định nghĩa các API endpoints
- **`SessionManager`**: Quản lý session đăng nhập

#### Data Models
##### Core Models
- **`Department`**: Model khoa
- **`ProgramClass`**: Model lớp học
- **`Subject`**: Model môn học
- **`AdminStudent`**, **`Teacher`**: Models người dùng

##### Timetable & Enrollment
- **`AdminTimetable`**: Model thời khóa biểu (admin view)
- **`Schedule`**: Model thời khóa biểu (student view)
- **`TimetableStudent`**: Model sinh viên trong lớp
- **`SubjectClass`**: Model lớp môn học

##### Grades & Exams
- **`AdminGrade`**, **`Grade`**: Models điểm số
- **`ExamSchedule`**: Model lịch thi

#### Request/Response Models
- **`LoginRequest`**, **`LoginResponse`**: Authentication
- **`Create*Request`**, **`Update*Request`**: CRUD operations
- **`*Response`**: API responses với pagination

### 🎨 UI Components

#### Adapters
- **`DepartmentsAdapter`**: Hiển thị danh sách khoa
- **`DepartmentClassesAdapter`**: Danh sách lớp trong khoa
- **`StudentsAdapter`**, **`TeachersAdapter`**: Danh sách người dùng
- **`TimetablesAdapter`**: Danh sách thời khóa biểu
- **`TimetableStudentsAdapter`**: Sinh viên trong lớp
- **`GradesAdapter`**: Danh sách điểm số

## 🔐 API Backend Structure

### 📁 Cấu trúc thư mục API

```
/
├── admin/                          # Admin APIs
│   ├── departments.php             # CRUD khoa
│   ├── program_classes.php         # CRUD lớp học  
│   ├── students.php                # CRUD sinh viên
│   ├── teachers.php                # CRUD giáo viên
│   ├── subjects.php                # CRUD môn học
│   ├── timetables.php              # CRUD thời khóa biểu
│   ├── timetable_students.php      # Quản lý sinh viên trong lớp
│   ├── available_students.php      # Danh sách sinh viên có thể thêm
│   ├── grades.php                  # CRUD điểm số
│   └── subject_classes.php         # Lấy danh sách lớp môn học
├── login.php                       # Authentication API
├── student_profile.php             # Profile sinh viên
├── get_schedule.php                # Thời khóa biểu sinh viên
├── student.php                     # Điểm số sinh viên
├── get_exam_schedule.php           # Lịch thi
└── db_connect.php                  # Database connection
```

### 🔄 REST API Endpoints

#### Authentication
- `POST /login.php` - Đăng nhập

#### Student APIs  
- `GET /student_profile.php?student_id={id}` - Profile sinh viên
- `GET /get_schedule.php?student_id={id}&day_of_week={day}` - Thời khóa biểu
- `GET /student.php?student_id={id}` - Điểm số
- `GET /get_exam_schedule.php?student_id={id}` - Lịch thi

#### Admin APIs (CRUD pattern)
- `GET|POST|PUT|DELETE /admin/departments.php` - Quản lý khoa
- `GET|POST|PUT|DELETE /admin/students.php` - Quản lý sinh viên
- `GET|POST|PUT|DELETE /admin/teachers.php` - Quản lý giáo viên
- `GET|POST|PUT|DELETE /admin/subjects.php` - Quản lý môn học
- `GET|POST|PUT|DELETE /admin/timetables.php` - Quản lý thời khóa biểu
- `GET|POST|DELETE /admin/timetable_students.php` - Quản lý sinh viên trong lớp

## 📋 Database Schema Details

### Quan hệ giữa các bảng

```
user (1) ──► (1) student ──► (n) program_class ──► (1) department
                │                    │
                └── (n) grade         └── (1) teacher
                        │
subject ──► (1) subject_class ──► (n) timetable
   │               │                    │
   └── (1) department     └── (n) student_enrollment
                                  │
                            └── (1) student
```

### Các tính năng đặc biệt

#### Enrollment System
- Bảng `student_enrollment` quản lý việc đăng ký học của sinh viên
- Mỗi sinh viên có thể đăng ký nhiều lớp môn học
- Trạng thái: `active`, `dropped`, `completed`

#### Grade Management  
- Hỗ trợ 4 loại điểm: Quá trình, Thực hành, Giữa kỳ, Cuối kỳ
- Tự động tính điểm trung bình môn học
- Phân chia theo học kỳ

#### Timetable System
- Thời khóa biểu linh hoạt theo ngày trong tuần
- Hỗ trợ nhiều tiết học trong ngày
- Quản lý thời gian bắt đầu/kết thúc

## ⚙️ Cài đặt và triển khai

### 🔧 Yêu cầu hệ thống

#### Backend Requirements
- PHP 8.0+ với extensions: PDO, MySQL
- Apache/Nginx web server  
- MySQL/MariaDB 5.7+

#### Android Development
- Android Studio Giraffe+
- JDK 11+
- Android SDK 24+

### 📦 Cách cài đặt

#### 1. Cài đặt Database
```sql
-- Import database schema
mysql -u root -p < quanlysinhvien.sql

-- Tạo bảng student_enrollment (nếu chưa có)
mysql -u root -p < create_student_enrollment_table.sql
```

#### 2. Cấu hình Backend
```php
// Cập nhật db_connect.php với thông tin database
$servername = "localhost";
$username = "your_username"; 
$password = "your_password";
$dbname = "quanlysinhvien";
```

#### 3. Build Android App
```bash
# Mở project trong Android Studio
# Sync Gradle
# Build & Run
```

### 🔑 Tài khoản mặc định

```
Admin: admin1 / admin123
Student: datnt / password_hash  
Student: linhvnd / password_hash 
Teacher: longdp / password_hash
```

## 🧪 Testing & Debugging

### 📝 Test Files

Dự án bao gồm nhiều file test để kiểm tra các chức năng:

```
test_admin_apis.php              # Test các API admin
test_departments_api.php         # Test API quản lý khoa  
test_teachers_api.php            # Test API giáo viên
test_program_classes_crud.php    # Test CRUD lớp học
test_grade_logic.php            # Test logic tính điểm
check_database_structure.php    # Kiểm tra cấu trúc DB
debug_student_data.php          # Debug dữ liệu sinh viên
```

### 🔍 Troubleshooting

#### Lỗi thường gặp:
1. **Database connection failed**: Kiểm tra thông tin kết nối trong `db_connect.php`
2. **API returns 500**: Kiểm tra PHP error logs
3. **App crashes**: Kiểm tra logcat trong Android Studio
4. **Authentication failed**: Kiểm tra username/password và table `user`

## 🚀 Tính năng nổi bật

### ✨ Highlights

1. **🔐 Role-based Access Control**: Phân quyền rõ ràng cho Admin/Teacher/Student
2. **📱 Modern Android UI**: Material Design với CardView, RecyclerView 
3. **🔄 Real-time Sync**: SwipeRefreshLayout cho cập nhật dữ liệu
4. **🎯 Smart Search**: Tìm kiếm thông minh trong tất cả modules
5. **📊 Grade Calculator**: Tự động tính toán điểm trung bình
6. **⏰ Schedule Management**: Quản lý thời khóa biểu linh hoạt
7. **👥 Class Enrollment**: Hệ thống đăng ký lớp học hoàn chỉnh
8. **📈 Data Validation**: Kiểm tra dữ liệu đầu vào chặt chẽ
9. **🔄 CRUD Operations**: Đầy đủ chức năng Create, Read, Update, Delete
10. **📱 Responsive Design**: Giao diện thân thiện trên mọi kích thước màn hình

## 📈 Future Enhancements

- [ ] Push notifications cho thông báo quan trọng
- [ ] Export/Import dữ liệu Excel
- [ ] Dashboard analytics cho admin
- [ ] Chat system giữa giáo viên và sinh viên  
- [ ] Mobile app cho giáo viên với nhiều chức năng hơn
- [ ] API versioning và documentation (Swagger)
- [ ] Unit testing coverage
- [ ] Performance optimization

## 👥 Đóng góp

Dự án được phát triển như một hệ thống quản lý sinh viên hoàn chỉnh, phù hợp cho các trường học, trung tâm đào tạo muốn số hóa quy trình quản lý.

## 📄 License

Dự án này được phát triển cho mục đích học tập và nghiên cứu.

---

*📞 Liên hệ: [Your Contact Information]*
*🌟 Nếu project hữu ích, hãy cho một star! ⭐* 