<?php
require_once 'db_connect.php';

echo "<h2>Debug Student Data</h2>";

try {
    // Kiểm tra program_class và department
    echo "<h3>Program Classes:</h3>";
    $pc_sql = "SELECT pc.program_class_id, pc.program_class_code, pc.department_id, d.department_name 
               FROM program_class pc 
               LEFT JOIN department d ON pc.department_id = d.department_id 
               ORDER BY pc.program_class_id";
    $pc_stmt = $conn->prepare($pc_sql);
    $pc_stmt->execute();
    $program_classes = $pc_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<table border='1'>";
    echo "<tr><th>ID</th><th>Mã lớp</th><th>Department ID</th><th>Tên khoa</th></tr>";
    foreach ($program_classes as $pc) {
        echo "<tr>";
        echo "<td>{$pc['program_class_id']}</td>";
        echo "<td>{$pc['program_class_code']}</td>";
        echo "<td>{$pc['department_id']}</td>";
        echo "<td>{$pc['department_name']}</td>";
        echo "</tr>";
    }
    echo "</table>";
    
    echo "<h3>Departments:</h3>";
    $dept_sql = "SELECT * FROM department ORDER BY department_id";
    $dept_stmt = $conn->prepare($dept_sql);
    $dept_stmt->execute();
    $departments = $dept_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<table border='1'>";
    echo "<tr><th>ID</th><th>Tên khoa</th></tr>";
    foreach ($departments as $dept) {
        echo "<tr>";
        echo "<td>{$dept['department_id']}</td>";
        echo "<td>{$dept['department_name']}</td>";
        echo "</tr>";
    }
    echo "</table>";
    
    echo "<h3>Students với Program Class:</h3>";
    $student_sql = "SELECT s.student_id, s.student_code, s.student_full_name, s.program_class_id,
                           pc.program_class_code, d.department_name 
                    FROM student s 
                    LEFT JOIN program_class pc ON s.program_class_id = pc.program_class_id 
                    LEFT JOIN department d ON pc.department_id = d.department_id 
                    ORDER BY s.student_id";
    $student_stmt = $conn->prepare($student_sql);
    $student_stmt->execute();
    $students = $student_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<table border='1'>";
    echo "<tr><th>ID</th><th>Mã SV</th><th>Tên</th><th>Program Class ID</th><th>Mã lớp</th><th>Khoa</th></tr>";
    foreach ($students as $student) {
        echo "<tr>";
        echo "<td>{$student['student_id']}</td>";
        echo "<td>{$student['student_code']}</td>";
        echo "<td>{$student['student_full_name']}</td>";
        echo "<td>{$student['program_class_id']}</td>";
        echo "<td>{$student['program_class_code']}</td>";
        echo "<td>{$student['department_name']}</td>";
        echo "</tr>";
    }
    echo "</table>";
    
    // Kiểm tra lớp HTTT2022.1 cụ thể
    echo "<h3>Chi tiết lớp HTTT2022.1:</h3>";
    $httt_sql = "SELECT pc.*, d.department_name 
                 FROM program_class pc 
                 LEFT JOIN department d ON pc.department_id = d.department_id 
                 WHERE pc.program_class_code = 'HTTT2022.1'";
    $httt_stmt = $conn->prepare($httt_sql);
    $httt_stmt->execute();
    $httt_class = $httt_stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($httt_class) {
        echo "<p><strong>Lớp HTTT2022.1:</strong></p>";
        echo "<ul>";
        echo "<li>ID: {$httt_class['program_class_id']}</li>";
        echo "<li>Mã lớp: {$httt_class['program_class_code']}</li>";
        echo "<li>Department ID: {$httt_class['department_id']}</li>";
        echo "<li>Tên khoa: {$httt_class['department_name']}</li>";
        echo "<li>Năm: {$httt_class['year']}</li>";
        echo "</ul>";
    } else {
        echo "<p>Không tìm thấy lớp HTTT2022.1</p>";
    }
    
} catch (Exception $e) {
    echo "Lỗi: " . $e->getMessage();
}
?> 