<?php
require_once 'db_connect.php';

echo "=== Sửa mapping department cho lớp HTTT2022.1 ===\n";

try {
    $conn->beginTransaction();
    
    // 1. Kiểm tra xem có khoa "Hệ thống thông tin" không
    $httt_dept_sql = "SELECT department_id FROM department WHERE department_name LIKE '%thông tin%'";
    $httt_dept_stmt = $conn->prepare($httt_dept_sql);
    $httt_dept_stmt->execute();
    $httt_dept = $httt_dept_stmt->fetch();
    
    if (!$httt_dept) {
        // Tạo khoa "Hệ thống thông tin" nếu chưa có
        $create_dept_sql = "INSERT INTO department (department_name) VALUES ('Hệ thống thông tin')";
        $create_dept_stmt = $conn->prepare($create_dept_sql);
        $create_dept_stmt->execute();
        $httt_dept_id = $conn->lastInsertId();
        echo "✅ Tạo khoa: Hệ thống thông tin (ID: $httt_dept_id)\n";
    } else {
        $httt_dept_id = $httt_dept['department_id'];
        echo "✅ Tìm thấy khoa Hệ thống thông tin (ID: $httt_dept_id)\n";
    }
    
    // 2. Cập nhật lớp HTTT2022.1 để thuộc khoa "Hệ thống thông tin"
    $update_class_sql = "UPDATE program_class SET department_id = ? WHERE program_class_code = 'HTTT2022.1'";
    $update_class_stmt = $conn->prepare($update_class_sql);
    $update_class_stmt->execute([$httt_dept_id]);
    
    if ($update_class_stmt->rowCount() > 0) {
        echo "✅ Cập nhật lớp HTTT2022.1 thuộc khoa Hệ thống thông tin\n";
    } else {
        echo "⚠️ Không tìm thấy lớp HTTT2022.1 để cập nhật\n";
    }
    
    // 3. Tương tự với lớp HTTT2022.2
    $update_class2_sql = "UPDATE program_class SET department_id = ? WHERE program_class_code = 'HTTT2022.2'";
    $update_class2_stmt = $conn->prepare($update_class2_sql);
    $update_class2_stmt->execute([$httt_dept_id]);
    
    if ($update_class2_stmt->rowCount() > 0) {
        echo "✅ Cập nhật lớp HTTT2022.2 thuộc khoa Hệ thống thông tin\n";
    }
    
    // 4. Kiểm tra kết quả
    echo "\n=== Kiểm tra kết quả ===\n";
    $check_sql = "SELECT pc.program_class_code, pc.department_id, d.department_name 
                  FROM program_class pc 
                  JOIN department d ON pc.department_id = d.department_id 
                  WHERE pc.program_class_code LIKE 'HTTT%'";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute();
    $results = $check_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    foreach ($results as $result) {
        echo "Lớp {$result['program_class_code']}: {$result['department_name']}\n";
    }
    
    $conn->commit();
    echo "\n🎉 Hoàn tất sửa mapping!\n";
    
} catch (Exception $e) {
    $conn->rollBack();
    echo "❌ Lỗi: " . $e->getMessage() . "\n";
}
?> 