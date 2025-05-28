<?php
require_once 'db_connect.php';

echo "=== Tạo dữ liệu mẫu cho hệ thống ===\n";

try {
    // Kiểm tra kết nối database
    if ($conn === null) {
        throw new Exception('Không thể kết nối database');
    }
    
    echo "✅ Kết nối database thành công\n";
    
    $conn->beginTransaction();
    
    // 1. Tạo departments nếu chưa có
    echo "\n1. Tạo khoa...\n";
    $dept_count_sql = "SELECT COUNT(*) as count FROM department";
    $dept_count_stmt = $conn->prepare($dept_count_sql);
    $dept_count_stmt->execute();
    $dept_count = $dept_count_stmt->fetch()['count'];
    
    if ($dept_count == 0) {
        $departments = [
            'Công nghệ thông tin',
            'Kỹ thuật điện',
            'Kỹ thuật cơ khí',
            'Quản trị kinh doanh'
        ];
        
        $dept_sql = "INSERT INTO department (department_name) VALUES (?)";
        $dept_stmt = $conn->prepare($dept_sql);
        
        foreach ($departments as $dept_name) {
            $dept_stmt->execute([$dept_name]);
            echo "   ✅ Tạo khoa: $dept_name\n";
        }
    } else {
        echo "   ⚠️ Đã có $dept_count khoa trong database\n";
    }
    
    // 2. Tạo admin user nếu chưa có
    echo "\n2. Tạo tài khoản admin...\n";
    $admin_check_sql = "SELECT * FROM user WHERE role = 'admin' LIMIT 1";
    $admin_check_stmt = $conn->prepare($admin_check_sql);
    $admin_check_stmt->execute();
    $existing_admin = $admin_check_stmt->fetch();
    
    if (!$existing_admin) {
        $admin_sql = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
        $admin_stmt = $conn->prepare($admin_sql);
        $admin_stmt->execute(['admin', 'admin123', 'admin']);
        echo "   ✅ Tạo admin: username=admin, password=admin123\n";
    } else {
        echo "   ⚠️ Admin đã tồn tại: " . $existing_admin['username'] . "\n";
    }
    
    // 3. Tạo teachers nếu chưa có
    echo "\n3. Tạo giáo viên...\n";
    $teacher_count_sql = "SELECT COUNT(*) as count FROM teacher";
    $teacher_count_stmt = $conn->prepare($teacher_count_sql);
    $teacher_count_stmt->execute();
    $teacher_count = $teacher_count_stmt->fetch()['count'];
    
    if ($teacher_count == 0) {
        $teachers = [
            ['teacher1', 'teacher123', 'Nguyễn Văn Giáo', '1980-05-15', 'giao.nguyen@university.edu.vn'],
            ['teacher2', 'teacher123', 'Trần Thị Minh', '1985-08-22', 'minh.tran@university.edu.vn'],
            ['teacher3', 'teacher123', 'Lê Hoàng Nam', '1978-12-10', 'nam.le@university.edu.vn']
        ];
        
        foreach ($teachers as $teacher_data) {
            // Tạo user account
            $user_sql = "INSERT INTO user (username, password, role) VALUES (?, ?, 'teacher')";
            $user_stmt = $conn->prepare($user_sql);
            $user_stmt->execute([$teacher_data[0], $teacher_data[1]]);
            $user_id = $conn->lastInsertId();
            
            // Tạo teacher record
            $teacher_sql = "INSERT INTO teacher (user_id, teacher_full_name, date_of_birth, teacher_email) VALUES (?, ?, ?, ?)";
            $teacher_stmt = $conn->prepare($teacher_sql);
            $teacher_stmt->execute([$user_id, $teacher_data[2], $teacher_data[3], $teacher_data[4]]);
            
            echo "   ✅ Tạo giáo viên: {$teacher_data[2]} (username: {$teacher_data[0]})\n";
        }
    } else {
        echo "   ⚠️ Đã có $teacher_count giáo viên trong database\n";
    }
    
    // 4. Tạo program_class nếu chưa có
    echo "\n4. Tạo lớp chương trình...\n";
    $class_count_sql = "SELECT COUNT(*) as count FROM program_class";
    $class_count_stmt = $conn->prepare($class_count_sql);
    $class_count_stmt->execute();
    $class_count = $class_count_stmt->fetch()['count'];
    
    if ($class_count == 0) {
        // Lấy danh sách department_id và teacher_id
        $dept_stmt = $conn->query("SELECT department_id FROM department ORDER BY department_id LIMIT 4");
        $departments = $dept_stmt->fetchAll(PDO::FETCH_COLUMN);
        
        $teacher_stmt = $conn->query("SELECT teacher_id FROM teacher ORDER BY teacher_id LIMIT 3");
        $teachers = $teacher_stmt->fetchAll(PDO::FETCH_COLUMN);
        
        if (count($departments) > 0 && count($teachers) > 0) {
            $program_classes = [
                ['HTTT2022.1', $teachers[0] ?? 1, 2022, $departments[0] ?? 1],
                ['HTTT2022.2', $teachers[1] ?? 1, 2022, $departments[0] ?? 1],
                ['KTDT2022.1', $teachers[2] ?? 1, 2022, $departments[1] ?? 2],
                ['KTCK2022.1', $teachers[0] ?? 1, 2022, $departments[2] ?? 3],
                ['QTKD2022.1', $teachers[1] ?? 1, 2022, $departments[3] ?? 4]
            ];
            
            $class_sql = "INSERT INTO program_class (program_class_code, teacher_id, year, department_id) VALUES (?, ?, ?, ?)";
            $class_stmt = $conn->prepare($class_sql);
            
            foreach ($program_classes as $class_data) {
                $class_stmt->execute($class_data);
                echo "   ✅ Tạo lớp: {$class_data[0]}\n";
            }
        }
    } else {
        echo "   ⚠️ Đã có $class_count lớp chương trình trong database\n";
    }
    
    // 5. Tạo subjects nếu chưa có
    echo "\n5. Tạo môn học...\n";
    $subject_count_sql = "SELECT COUNT(*) as count FROM subject";
    $subject_count_stmt = $conn->prepare($subject_count_sql);
    $subject_count_stmt->execute();
    $subject_count = $subject_count_stmt->fetch()['count'];
    
    if ($subject_count == 0) {
        $subjects = [
            ['CS101', 'Nhập môn lập trình', 3, $departments[0] ?? 1],
            ['CS102', 'Lập trình hướng đối tượng', 4, $departments[0] ?? 1],
            ['CS201', 'Cấu trúc dữ liệu và giải thuật', 4, $departments[0] ?? 1],
            ['CS301', 'Cơ sở dữ liệu', 3, $departments[0] ?? 1],
            ['EE101', 'Mạch điện cơ bản', 3, $departments[1] ?? 2],
            ['ME101', 'Cơ học kỹ thuật', 3, $departments[2] ?? 3],
            ['BBA101', 'Nguyên lý quản trị', 3, $departments[3] ?? 4]
        ];
        
        $subject_sql = "INSERT INTO subject (subject_code, name, credits, department_id) VALUES (?, ?, ?, ?)";
        $subject_stmt = $conn->prepare($subject_sql);
        
        foreach ($subjects as $subject_data) {
            $subject_stmt->execute($subject_data);
            echo "   ✅ Tạo môn học: {$subject_data[1]} ({$subject_data[0]})\n";
        }
    } else {
        echo "   ⚠️ Đã có $subject_count môn học trong database\n";
    }
    
    // 6. Tạo subject_class nếu chưa có
    echo "\n6. Tạo lớp môn học...\n";
    $subject_class_count_sql = "SELECT COUNT(*) as count FROM subject_class";
    $subject_class_count_stmt = $conn->prepare($subject_class_count_sql);
    $subject_class_count_stmt->execute();
    $subject_class_count = $subject_class_count_stmt->fetch()['count'];
    
    if ($subject_class_count == 0) {
        // Lấy danh sách subject_id và teacher_id
        $subject_stmt = $conn->query("SELECT subject_id FROM subject ORDER BY subject_id LIMIT 5");
        $subjects = $subject_stmt->fetchAll(PDO::FETCH_COLUMN);
        
        $teacher_stmt = $conn->query("SELECT teacher_id FROM teacher ORDER BY teacher_id LIMIT 3");
        $teachers = $teacher_stmt->fetchAll(PDO::FETCH_COLUMN);
        
        if (count($subjects) > 0 && count($teachers) > 0) {
            $subject_classes = [
                [$subjects[0] ?? 1, $teachers[0] ?? 1, 'CS101_2023_1', 'Học kỳ 1 năm 2023'],
                [$subjects[1] ?? 1, $teachers[1] ?? 1, 'CS102_2023_1', 'Học kỳ 1 năm 2023'],
                [$subjects[2] ?? 1, $teachers[2] ?? 1, 'CS201_2023_2', 'Học kỳ 2 năm 2023'],
                [$subjects[3] ?? 1, $teachers[0] ?? 1, 'CS301_2023_2', 'Học kỳ 2 năm 2023'],
                [$subjects[4] ?? 1, $teachers[1] ?? 1, 'EE101_2023_1', 'Học kỳ 1 năm 2023']
            ];
            
            $subject_class_sql = "INSERT INTO subject_class (subject_id, teacher_id, subject_class_code, semester) VALUES (?, ?, ?, ?)";
            $subject_class_stmt = $conn->prepare($subject_class_sql);
            
            foreach ($subject_classes as $sc_data) {
                $subject_class_stmt->execute($sc_data);
                echo "   ✅ Tạo lớp môn học: {$sc_data[2]}\n";
            }
        }
    } else {
        echo "   ⚠️ Đã có $subject_class_count lớp môn học trong database\n";
    }
    
    // 7. Tạo student mẫu nếu chưa có
    echo "\n7. Tạo sinh viên mẫu...\n";
    $student_count_sql = "SELECT COUNT(*) as count FROM student";
    $student_count_stmt = $conn->prepare($student_count_sql);
    $student_count_stmt->execute();
    $student_count = $student_count_stmt->fetch()['count'];
    
    if ($student_count == 0) {
        // Lấy program_class_id đầu tiên
        $class_stmt = $conn->query("SELECT program_class_id FROM program_class ORDER BY program_class_id LIMIT 1");
        $first_class = $class_stmt->fetch();
        
        if ($first_class) {
            $students = [
                ['student1', 'student123', '22520001', 'Vũ Nguyễn Duy Linh', '2004-10-15', 'linh.vu@student.edu.vn', '123 Nguyễn Văn Cừ, Q5, TP.HCM'],
                ['student2', 'student123', '22520002', 'Nguyễn Thị Hương', '2004-08-22', 'huong.nguyen@student.edu.vn', '456 Lê Văn Sỹ, Q3, TP.HCM']
            ];
            
            foreach ($students as $student_data) {
                // Tạo user account
                $user_sql = "INSERT INTO user (username, password, role) VALUES (?, ?, 'student')";
                $user_stmt = $conn->prepare($user_sql);
                $user_stmt->execute([$student_data[0], $student_data[1]]);
                $user_id = $conn->lastInsertId();
                
                // Tạo student record
                $student_sql = "INSERT INTO student (user_id, student_code, student_full_name, date_of_birth, student_email, student_address, program_class_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
                $student_stmt = $conn->prepare($student_sql);
                $student_stmt->execute([
                    $user_id,
                    $student_data[2],
                    $student_data[3],
                    $student_data[4],
                    $student_data[5],
                    $student_data[6],
                    $first_class['program_class_id']
                ]);
                
                echo "   ✅ Tạo sinh viên: {$student_data[3]} ({$student_data[2]}, username: {$student_data[0]})\n";
            }
        }
    } else {
        echo "   ⚠️ Đã có $student_count sinh viên trong database\n";
    }
    
    // 8. Tạo grades mẫu nếu chưa có
    echo "\n8. Tạo điểm mẫu...\n";
    $grade_count_sql = "SELECT COUNT(*) as count FROM grade";
    $grade_count_stmt = $conn->prepare($grade_count_sql);
    $grade_count_stmt->execute();
    $grade_count = $grade_count_stmt->fetch()['count'];
    
    if ($grade_count == 0) {
        // Lấy student_id và subject_class_id đầu tiên
        $student_stmt = $conn->query("SELECT student_id FROM student ORDER BY student_id LIMIT 2");
        $students = $student_stmt->fetchAll(PDO::FETCH_COLUMN);
        
        $subject_class_stmt = $conn->query("SELECT subject_class_id FROM subject_class ORDER BY subject_class_id LIMIT 3");
        $subject_classes = $subject_class_stmt->fetchAll(PDO::FETCH_COLUMN);
        
        if (count($students) > 0 && count($subject_classes) > 0) {
            $grades = [
                [$students[0] ?? 1, $subject_classes[0] ?? 1, 'Học kỳ 1 năm 2023', 8.5, 9.0, 8.0, 8.5],
                [$students[0] ?? 1, $subject_classes[1] ?? 1, 'Học kỳ 1 năm 2023', 7.5, 8.0, 7.0, 7.8],
                [$students[1] ?? 1, $subject_classes[0] ?? 1, 'Học kỳ 1 năm 2023', 9.0, 8.5, 9.0, 8.8],
                [$students[1] ?? 1, $subject_classes[2] ?? 1, 'Học kỳ 2 năm 2023', 8.0, 8.5, 7.5, 8.2]
            ];
            
            $grade_sql = "INSERT INTO grade (student_id, subject_class_id, semester, process_grade, practice_grade, midterm_grade, final_grade) VALUES (?, ?, ?, ?, ?, ?, ?)";
            $grade_stmt = $conn->prepare($grade_sql);
            
            foreach ($grades as $grade_data) {
                $grade_stmt->execute($grade_data);
                echo "   ✅ Tạo điểm cho sinh viên {$grade_data[0]}, lớp môn học {$grade_data[1]}\n";
            }
        }
    } else {
        echo "   ⚠️ Đã có $grade_count điểm trong database\n";
    }
    
    $conn->commit();
    
    echo "\n🎉 Hoàn tất tạo dữ liệu mẫu!\n\n";
    echo "=== THÔNG TIN ĐĂNG NHẬP ===\n";
    echo "Admin:\n";
    echo "  Username: admin\n";
    echo "  Password: admin123\n\n";
    echo "Teacher (mẫu):\n";
    echo "  Username: teacher1\n";
    echo "  Password: teacher123\n\n";
    echo "Student (mẫu):\n";
    echo "  Username: student1\n";
    echo "  Password: student123\n\n";
    
} catch (Exception $e) {
    $conn->rollBack();
    echo "❌ Lỗi: " . $e->getMessage() . "\n";
    echo "Stack trace: " . $e->getTraceAsString() . "\n";
}
?> 