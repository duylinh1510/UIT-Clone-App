<?php
require_once 'db_connect.php';

echo "=== Sửa dữ liệu thực tế - Tạo lớp HTTT2022.1 ===\n";

try {
    $conn->beginTransaction();
    
    // 1. Lấy department_id của khoa "Hệ thống thông tin"
    echo "\n1. Lấy thông tin khoa Hệ thống thông tin...\n";
    $httt_dept_sql = "SELECT department_id FROM department WHERE department_name = 'Hệ thống thông tin'";
    $httt_dept_stmt = $conn->prepare($httt_dept_sql);
    $httt_dept_stmt->execute();
    $httt_dept = $httt_dept_stmt->fetch();
    
    if ($httt_dept) {
        $httt_dept_id = $httt_dept['department_id'];
        echo "   ✅ Tìm thấy khoa Hệ thống thông tin (ID: $httt_dept_id)\n";
    } else {
        echo "   ❌ Không tìm thấy khoa Hệ thống thông tin\n";
        exit;
    }
    
    // 2. Lấy teacher_id đầu tiên để gán cho lớp
    echo "\n2. Lấy teacher_id...\n";
    $teacher_sql = "SELECT teacher_id FROM teacher ORDER BY teacher_id LIMIT 1";
    $teacher_stmt = $conn->prepare($teacher_sql);
    $teacher_stmt->execute();
    $teacher = $teacher_stmt->fetch();
    
    if ($teacher) {
        $teacher_id = $teacher['teacher_id'];
        echo "   ✅ Sử dụng teacher_id: $teacher_id\n";
    } else {
        echo "   ⚠️ Không tìm thấy teacher, sử dụng teacher_id = 1\n";
        $teacher_id = 1;
    }
    
    // 3. Kiểm tra xem lớp HTTT2022.1 đã tồn tại chưa
    echo "\n3. Kiểm tra lớp HTTT2022.1...\n";
    $check_class_sql = "SELECT program_class_id FROM program_class WHERE program_class_code = 'HTTT2022.1'";
    $check_class_stmt = $conn->prepare($check_class_sql);
    $check_class_stmt->execute();
    $existing_class = $check_class_stmt->fetch();
    
    if ($existing_class) {
        $httt_class_id = $existing_class['program_class_id'];
        echo "   ✅ Lớp HTTT2022.1 đã tồn tại (ID: $httt_class_id)\n";
    } else {
        // Tạo lớp HTTT2022.1 mới
        $create_class_sql = "INSERT INTO program_class (program_class_code, teacher_id, year, department_id) VALUES (?, ?, ?, ?)";
        $create_class_stmt = $conn->prepare($create_class_sql);
        $create_class_stmt->execute(['HTTT2022.1', $teacher_id, 2022, $httt_dept_id]);
        $httt_class_id = $conn->lastInsertId();
        echo "   ✅ Tạo lớp HTTT2022.1 mới (ID: $httt_class_id)\n";
    }
    
    // 4. Tạo thêm lớp Hệ thống thông tin 2022.1 
    echo "\n4. Tạo lớp 'Hệ thống thông tin 2022.1'...\n";
    $check_httt_class_sql = "SELECT program_class_id FROM program_class WHERE program_class_code = 'Hệ thống thông tin 2022.1'";
    $check_httt_class_stmt = $conn->prepare($check_httt_class_sql);
    $check_httt_class_stmt->execute();
    $existing_httt_class = $check_httt_class_stmt->fetch();
    
    if (!$existing_httt_class) {
        $create_httt_class_sql = "INSERT INTO program_class (program_class_code, teacher_id, year, department_id) VALUES (?, ?, ?, ?)";
        $create_httt_class_stmt = $conn->prepare($create_httt_class_sql);
        $create_httt_class_stmt->execute(['Hệ thống thông tin 2022.1', $teacher_id, 2022, $httt_dept_id]);
        $httt_full_class_id = $conn->lastInsertId();
        echo "   ✅ Tạo lớp 'Hệ thống thông tin 2022.1' (ID: $httt_full_class_id)\n";
    } else {
        $httt_full_class_id = $existing_httt_class['program_class_id'];
        echo "   ✅ Lớp 'Hệ thống thông tin 2022.1' đã tồn tại (ID: $httt_full_class_id)\n";
    }
    
    // 5. Hiển thị danh sách lớp để admin chọn
    echo "\n=== DANH SÁCH LỚP HIỆN CÓ ===\n";
    $list_classes_sql = "SELECT pc.program_class_id, pc.program_class_code, d.department_name 
                         FROM program_class pc 
                         JOIN department d ON pc.department_id = d.department_id 
                         ORDER BY pc.program_class_id";
    $list_classes_stmt = $conn->prepare($list_classes_sql);
    $list_classes_stmt->execute();
    $classes = $list_classes_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    foreach ($classes as $class) {
        echo "ID: {$class['program_class_id']} - Lớp: {$class['program_class_code']} - Khoa: {$class['department_name']}\n";
    }
    
    echo "\n=== CÁC SINH VIÊN HIỆN TẠI ===\n";
    $list_students_sql = "SELECT s.student_id, s.student_code, s.student_full_name, s.program_class_id,
                                 pc.program_class_code, d.department_name 
                          FROM student s 
                          JOIN program_class pc ON s.program_class_id = pc.program_class_id 
                          JOIN department d ON pc.department_id = d.department_id 
                          ORDER BY s.student_id";
    $list_students_stmt = $conn->prepare($list_students_sql);
    $list_students_stmt->execute();
    $students = $list_students_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    foreach ($students as $student) {
        echo "SV: {$student['student_code']} - {$student['student_full_name']} - Lớp: {$student['program_class_code']} - Khoa: {$student['department_name']}\n";
    }
    
    $conn->commit();
    echo "\n🎉 Hoàn tất tạo lớp!\n";
    echo "\nBây giờ bạn có thể:\n";
    echo "1. Vào giao diện Android admin\n";
    echo "2. Chỉnh sửa sinh viên Nguyen Van Hoa\n";
    echo "3. Chọn lớp 'HTTT2022.1' hoặc 'Hệ thống thông tin 2022.1'\n";
    echo "4. Lưu lại\n";
    echo "5. Sinh viên sẽ hiển thị khoa 'Hệ thống thông tin'\n";
    
} catch (Exception $e) {
    $conn->rollBack();
    echo "❌ Lỗi: " . $e->getMessage() . "\n";
}
?> 