<?php
require_once 'db_connect.php';

echo "Creating sample data for DoAn System...\n\n";

try {
    $conn->beginTransaction();
    
    // 1. Create departments
    echo "1. Creating departments...\n";
    $departments = [
        ['IT', 'Công nghệ thông tin'],
        ['EE', 'Kỹ thuật điện'],
        ['ME', 'Kỹ thuật cơ khí'],
        ['CE', 'Kỹ thuật dân dụng'],
        ['BBA', 'Quản trị kinh doanh']
    ];
    
    $dept_sql = "INSERT INTO department (department_code, department_name) VALUES (?, ?)";
    $dept_stmt = $conn->prepare($dept_sql);
    
    foreach ($departments as $dept) {
        $dept_stmt->execute($dept);
        echo "  - Added department: {$dept[1]}\n";
    }
    
    // 2. Create semesters
    echo "\n2. Creating semesters...\n";
    $semesters = [
        ['Học kỳ 1', 2023],
        ['Học kỳ 2', 2023],
        ['Học kỳ hè', 2023],
        ['Học kỳ 1', 2024],
        ['Học kỳ 2', 2024]
    ];
    
    $sem_sql = "INSERT INTO semester (semester_name, year) VALUES (?, ?)";
    $sem_stmt = $conn->prepare($sem_sql);
    
    foreach ($semesters as $sem) {
        $sem_stmt->execute($sem);
        echo "  - Added semester: {$sem[0]} {$sem[1]}\n";
    }
    
    // 3. Create classes
    echo "\n3. Creating classes...\n";
    $classes = [
        ['HTTT2022.1', 1], // IT department
        ['HTTT2022.2', 1],
        ['KTDT2022.1', 2], // EE department
        ['KTCK2022.1', 3], // ME department
        ['KTXD2022.1', 4], // CE department
        ['QTKD2022.1', 5]  // BBA department
    ];
    
    $class_sql = "INSERT INTO class (class_name, department_id) VALUES (?, ?)";
    $class_stmt = $conn->prepare($class_sql);
    
    foreach ($classes as $class) {
        $class_stmt->execute($class);
        echo "  - Added class: {$class[0]}\n";
    }
    
    // 4. Create subjects
    echo "\n4. Creating subjects...\n";
    $subjects = [
        // IT subjects
        ['CS101', 'Nhập môn lập trình', 3, 1],
        ['CS102', 'Lập trình hướng đối tượng', 4, 1],
        ['CS201', 'Cấu trúc dữ liệu và giải thuật', 4, 1],
        ['CS301', 'Cơ sở dữ liệu', 3, 1],
        ['CS401', 'Phát triển ứng dụng web', 4, 1],
        
        // EE subjects
        ['EE101', 'Mạch điện cơ bản', 3, 2],
        ['EE201', 'Điện tử số', 4, 2],
        
        // ME subjects
        ['ME101', 'Cơ học kỹ thuật', 3, 3],
        ['ME201', 'Vẽ kỹ thuật', 2, 3],
        
        // General subjects
        ['GE101', 'Toán cao cấp A1', 4, 1],
        ['GE102', 'Vật lý đại cương', 3, 1],
        ['GE201', 'Tiếng Anh chuyên ngành', 2, 1]
    ];
    
    $subj_sql = "INSERT INTO subject (subject_code, name, credits, department_id) VALUES (?, ?, ?, ?)";
    $subj_stmt = $conn->prepare($subj_sql);
    
    foreach ($subjects as $subj) {
        $subj_stmt->execute($subj);
        echo "  - Added subject: {$subj[1]} ({$subj[0]})\n";
    }
    
    // 5. Create admin user
    echo "\n5. Creating admin user...\n";
    $admin_password = password_hash('admin123', PASSWORD_DEFAULT);
    $admin_sql = "INSERT INTO user (username, password, role) VALUES ('admin', ?, 'admin')";
    $admin_stmt = $conn->prepare($admin_sql);
    $admin_stmt->execute([$admin_password]);
    echo "  - Added admin user (username: admin, password: admin123)\n";
    
    // 6. Create sample teachers
    echo "\n6. Creating sample teachers...\n";
    $teachers = [
        ['Nguyễn Văn Giáo', '1980-05-15', 'giao.nguyen@university.edu.vn', 1, 'teacher1', 'teacher123'],
        ['Trần Thị Minh', '1985-08-22', 'minh.tran@university.edu.vn', 1, 'teacher2', 'teacher123'],
        ['Lê Hoàng Nam', '1978-12-10', 'nam.le@university.edu.vn', 2, 'teacher3', 'teacher123'],
        ['Phạm Thị Lan', '1982-03-18', 'lan.pham@university.edu.vn', 1, 'teacher4', 'teacher123']
    ];
    
    foreach ($teachers as $teacher) {
        // Create user account
        $user_password = password_hash($teacher[5], PASSWORD_DEFAULT);
        $user_sql = "INSERT INTO user (username, password, role) VALUES (?, ?, 'teacher')";
        $user_stmt = $conn->prepare($user_sql);
        $user_stmt->execute([$teacher[4], $user_password]);
        $user_id = $conn->lastInsertId();
        
        // Create teacher record
        $teacher_sql = "INSERT INTO teacher (user_id, teacher_full_name, date_of_birth, teacher_email, department_id) VALUES (?, ?, ?, ?, ?)";
        $teacher_stmt = $conn->prepare($teacher_sql);
        $teacher_stmt->execute([$user_id, $teacher[0], $teacher[1], $teacher[2], $teacher[3]]);
        
        echo "  - Added teacher: {$teacher[0]} (username: {$teacher[4]})\n";
    }
    
    // 7. Create sample students
    echo "\n7. Creating sample students...\n";
    $students = [
        ['22520001', 'Vũ Nguyễn Duy Linh', '2004-10-15', 'linh.vu@student.edu.vn', '123 Nguyễn Văn Cừ, Q5, TP.HCM', 1, 1, 'student1', 'student123'],
        ['22520002', 'Nguyễn Thị Hương', '2004-08-22', 'huong.nguyen@student.edu.vn', '456 Lê Văn Sỹ, Q3, TP.HCM', 1, 1, 'student2', 'student123'],
        ['22520003', 'Trần Văn Minh', '2004-12-05', 'minh.tran@student.edu.vn', '789 Điện Biên Phủ, Q1, TP.HCM', 2, 1, 'student3', 'student123'],
        ['22520004', 'Lê Thị Mai', '2004-06-18', 'mai.le@student.edu.vn', '321 Cách Mạng Tháng 8, Q10, TP.HCM', 1, 1, 'student4', 'student123'],
        ['22520005', 'Phạm Văn Đức', '2004-09-30', 'duc.pham@student.edu.vn', '654 Võ Văn Tần, Q3, TP.HCM', 2, 1, 'student5', 'student123']
    ];
    
    foreach ($students as $student) {
        // Create user account
        $user_password = password_hash($student[8], PASSWORD_DEFAULT);
        $user_sql = "INSERT INTO user (username, password, role) VALUES (?, ?, 'student')";
        $user_stmt = $conn->prepare($user_sql);
        $user_stmt->execute([$student[7], $user_password]);
        $user_id = $conn->lastInsertId();
        
        // Create student record
        $student_sql = "INSERT INTO student (user_id, student_code, full_name, birth_date, student_email, student_address, class_id, department_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        $student_stmt = $conn->prepare($student_sql);
        $student_stmt->execute([$user_id, $student[0], $student[1], $student[2], $student[3], $student[4], $student[5], $student[6]]);
        
        echo "  - Added student: {$student[1]} ({$student[0]}, username: {$student[7]})\n";
    }
    
    // 8. Create sample grades
    echo "\n8. Creating sample grades...\n";
    $grade_sql = "INSERT INTO grade (student_id, subject_id, semester_id, process_grade, practice_grade, midterm_grade, final_grade) VALUES (?, ?, ?, ?, ?, ?, ?)";
    $grade_stmt = $conn->prepare($grade_sql);
    
    // Sample grades for student 1 (Vũ Nguyễn Duy Linh)
    $sample_grades = [
        [1, 1, 1, 8.5, 9.0, 8.0, 8.5], // CS101 - Semester 1/2023
        [1, 2, 1, 7.5, 8.0, 7.0, 7.8], // CS102 - Semester 1/2023
        [1, 10, 1, 9.0, null, 8.5, 8.8], // GE101 - Semester 1/2023
        [1, 3, 2, 8.0, 8.5, 7.5, 8.2], // CS201 - Semester 2/2023
        [1, 11, 2, 7.0, 7.5, 8.0, 7.5], // GE102 - Semester 2/2023
    ];
    
    foreach ($sample_grades as $grade) {
        $grade_stmt->execute($grade);
        echo "  - Added grade for student {$grade[0]}, subject {$grade[1]}\n";
    }
    
    // 9. Create sample exam schedules
    echo "\n9. Creating sample exam schedules...\n";
    $exam_sql = "INSERT INTO exam_schedule (subject_id, semester_id, exam_date, start_time, end_time, exam_room, exam_type, teacher_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    $exam_stmt = $conn->prepare($exam_sql);
    
    $sample_exams = [
        [1, 1, '2023-12-15', '08:00:00', '10:00:00', 'P101', 'Cuối kỳ', 1],
        [2, 1, '2023-12-18', '13:30:00', '15:30:00', 'P102', 'Cuối kỳ', 2],
        [10, 1, '2023-12-20', '08:00:00', '10:00:00', 'P201', 'Cuối kỳ', 1],
        [3, 2, '2024-05-10', '08:00:00', '10:00:00', 'P103', 'Cuối kỳ', 1],
        [11, 2, '2024-05-12', '13:30:00', '15:30:00', 'P104', 'Cuối kỳ', 3]
    ];
    
    foreach ($sample_exams as $exam) {
        $exam_stmt->execute($exam);
        echo "  - Added exam schedule for subject {$exam[0]} on {$exam[2]}\n";
    }
    
    $conn->commit();
    
    echo "\n✅ Sample data created successfully!\n\n";
    echo "=== LOGIN CREDENTIALS ===\n";
    echo "Admin:\n";
    echo "  Username: admin\n";
    echo "  Password: admin123\n\n";
    echo "Teacher (example):\n";
    echo "  Username: teacher1\n";
    echo "  Password: teacher123\n\n";
    echo "Student (example):\n";
    echo "  Username: student1 (Vũ Nguyễn Duy Linh)\n";
    echo "  Password: student123\n\n";
    
} catch (Exception $e) {
    $conn->rollBack();
    echo "\n❌ Error creating sample data: " . $e->getMessage() . "\n";
}
?> 