<?php
require_once 'db_connect.php';

echo "=== Sửa tất cả vấn đề về department mapping ===\n";

try {
    $conn->beginTransaction();
    
    // 1. Tạo/kiểm tra khoa "Hệ thống thông tin"
    echo "\n1. Kiểm tra khoa Hệ thống thông tin...\n";
    $httt_dept_sql = "SELECT department_id FROM department WHERE department_name = 'Hệ thống thông tin'";
    $httt_dept_stmt = $conn->prepare($httt_dept_sql);
    $httt_dept_stmt->execute();
    $httt_dept = $httt_dept_stmt->fetch();
    
    if (!$httt_dept) {
        $create_dept_sql = "INSERT INTO department (department_name) VALUES ('Hệ thống thông tin')";
        $create_dept_stmt = $conn->prepare($create_dept_sql);
        $create_dept_stmt->execute();
        $httt_dept_id = $conn->lastInsertId();
        echo "   ✅ Tạo khoa: Hệ thống thông tin (ID: $httt_dept_id)\n";
    } else {
        $httt_dept_id = $httt_dept['department_id'];
        echo "   ✅ Khoa Hệ thống thông tin đã tồn tại (ID: $httt_dept_id)\n";
    }
    
    // 2. Cập nhật lớp HTTT thuộc khoa Hệ thống thông tin
    echo "\n2. Cập nhật lớp HTTT...\n";
    $update_httt_sql = "UPDATE program_class SET department_id = ? WHERE program_class_code LIKE 'HTTT%'";
    $update_httt_stmt = $conn->prepare($update_httt_sql);
    $update_httt_stmt->execute([$httt_dept_id]);
    echo "   ✅ Cập nhật " . $update_httt_stmt->rowCount() . " lớp HTTT\n";
    
    // 3. Kiểm tra/tạo khoa Kỹ thuật điện tử
    echo "\n3. Kiểm tra khoa Kỹ thuật điện tử...\n";
    $ktdt_dept_sql = "SELECT department_id FROM department WHERE department_name LIKE '%điện%'";
    $ktdt_dept_stmt = $conn->prepare($ktdt_dept_sql);
    $ktdt_dept_stmt->execute();
    $ktdt_dept = $ktdt_dept_stmt->fetch();
    
    if (!$ktdt_dept) {
        $create_ktdt_sql = "INSERT INTO department (department_name) VALUES ('Kỹ thuật điện tử')";
        $create_ktdt_stmt = $conn->prepare($create_ktdt_sql);
        $create_ktdt_stmt->execute();
        $ktdt_dept_id = $conn->lastInsertId();
        echo "   ✅ Tạo khoa: Kỹ thuật điện tử (ID: $ktdt_dept_id)\n";
    } else {
        $ktdt_dept_id = $ktdt_dept['department_id'];
        echo "   ✅ Khoa Kỹ thuật điện tử đã tồn tại (ID: $ktdt_dept_id)\n";
    }
    
    // 4. Cập nhật lớp KTDT thuộc khoa Kỹ thuật điện tử
    echo "\n4. Cập nhật lớp KTDT...\n";
    $update_ktdt_sql = "UPDATE program_class SET department_id = ? WHERE program_class_code LIKE 'KTDT%'";
    $update_ktdt_stmt = $conn->prepare($update_ktdt_sql);
    $update_ktdt_stmt->execute([$ktdt_dept_id]);
    echo "   ✅ Cập nhật " . $update_ktdt_stmt->rowCount() . " lớp KTDT\n";
    
    // 5. Tương tự cho các khoa khác
    $dept_mappings = [
        'KTCK' => 'Kỹ thuật cơ khí',
        'QTKD' => 'Quản trị kinh doanh'
    ];
    
    foreach ($dept_mappings as $class_prefix => $dept_name) {
        echo "\n5. Xử lý khoa $dept_name...\n";
        
        // Kiểm tra/tạo khoa
        $check_dept_sql = "SELECT department_id FROM department WHERE department_name = ?";
        $check_dept_stmt = $conn->prepare($check_dept_sql);
        $check_dept_stmt->execute([$dept_name]);
        $existing_dept = $check_dept_stmt->fetch();
        
        if (!$existing_dept) {
            $create_new_dept_sql = "INSERT INTO department (department_name) VALUES (?)";
            $create_new_dept_stmt = $conn->prepare($create_new_dept_sql);
            $create_new_dept_stmt->execute([$dept_name]);
            $new_dept_id = $conn->lastInsertId();
            echo "   ✅ Tạo khoa: $dept_name (ID: $new_dept_id)\n";
        } else {
            $new_dept_id = $existing_dept['department_id'];
            echo "   ✅ Khoa $dept_name đã tồn tại (ID: $new_dept_id)\n";
        }
        
        // Cập nhật lớp
        $update_class_sql = "UPDATE program_class SET department_id = ? WHERE program_class_code LIKE ?";
        $update_class_stmt = $conn->prepare($update_class_sql);
        $update_class_stmt->execute([$new_dept_id, $class_prefix . '%']);
        echo "   ✅ Cập nhật " . $update_class_stmt->rowCount() . " lớp $class_prefix\n";
    }
    
    // 6. Hiển thị kết quả cuối cùng
    echo "\n=== KẾT QUẢ CUỐI CÙNG ===\n";
    $result_sql = "SELECT pc.program_class_code, d.department_name 
                   FROM program_class pc 
                   JOIN department d ON pc.department_id = d.department_id 
                   ORDER BY pc.program_class_code";
    $result_stmt = $conn->prepare($result_sql);
    $result_stmt->execute();
    $results = $result_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    foreach ($results as $result) {
        echo "Lớp {$result['program_class_code']}: {$result['department_name']}\n";
    }
    
    $conn->commit();
    echo "\n🎉 Hoàn tất sửa tất cả vấn đề!\n";
    echo "\nBây giờ:\n";
    echo "- Lớp HTTT2022.1, HTTT2022.2 thuộc khoa 'Hệ thống thông tin'\n";
    echo "- Lớp KTDT2022.1 thuộc khoa 'Kỹ thuật điện tử'\n";
    echo "- Lớp KTCK2022.1 thuộc khoa 'Kỹ thuật cơ khí'\n";
    echo "- Lớp QTKD2022.1 thuộc khoa 'Quản trị kinh doanh'\n";
    
} catch (Exception $e) {
    $conn->rollBack();
    echo "❌ Lỗi: " . $e->getMessage() . "\n";
}
?> 