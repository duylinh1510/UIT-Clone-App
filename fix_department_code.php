<?php
require_once 'db_connect.php';

echo "<h2>Sửa lỗi Department Code</h2>";

try {
    echo "<h3>1. Thêm cột department_code vào bảng department</h3>";
    
    // Kiểm tra xem cột department_code đã tồn tại chưa
    $check_column = $conn->query("SHOW COLUMNS FROM department LIKE 'department_code'");
    if ($check_column->rowCount() == 0) {
        $sql = "ALTER TABLE department ADD COLUMN department_code VARCHAR(10) NULL AFTER department_id";
        $conn->exec($sql);
        echo "✅ Đã thêm cột department_code vào bảng department<br>";
        
        // Thêm unique constraint
        $unique_sql = "ALTER TABLE department ADD CONSTRAINT uk_department_code UNIQUE (department_code)";
        $conn->exec($unique_sql);
        echo "✅ Đã thêm unique constraint cho department_code<br>";
    } else {
        echo "⚠️ Cột department_code đã tồn tại<br>";
    }
    
    echo "<h3>2. Cập nhật dữ liệu department với mã khoa phù hợp</h3>";
    
    // Tạo mã khoa dựa trên tên khoa hiện có
    $departments = $conn->query("SELECT department_id, department_name FROM department")->fetchAll(PDO::FETCH_ASSOC);
    
    foreach ($departments as $dept) {
        $dept_code = '';
        $dept_name = strtolower($dept['department_name']);
        
        // Tạo mã khoa dựa trên tên
        if (strpos($dept_name, 'cntt') !== false || strpos($dept_name, 'công nghệ thông tin') !== false) {
            $dept_code = 'CNTT';
        } elseif (strpos($dept_name, 'hệ thống thông tin') !== false || strpos($dept_name, 'httt') !== false) {
            $dept_code = 'HTTT';
        } elseif (strpos($dept_name, 'điện') !== false || strpos($dept_name, 'ktdt') !== false) {
            $dept_code = 'KTDT';
        } elseif (strpos($dept_name, 'cơ khí') !== false || strpos($dept_name, 'ktck') !== false) {
            $dept_code = 'KTCK';
        } elseif (strpos($dept_name, 'quản trị') !== false || strpos($dept_name, 'qtkd') !== false) {
            $dept_code = 'QTKD';
        } elseif (strpos($dept_name, 'dân dụng') !== false || strpos($dept_name, 'xây dựng') !== false) {
            $dept_code = 'KTXD';
        } else {
            // Tạo mã từ chữ cái đầu các từ
            $words = explode(' ', $dept['department_name']);
            $dept_code = '';
            foreach ($words as $word) {
                if (strlen(trim($word)) > 0) {
                    $dept_code .= strtoupper(substr(trim($word), 0, 1));
                }
            }
            // Giới hạn tối đa 6 ký tự
            $dept_code = substr($dept_code, 0, 6);
        }
        
        // Đảm bảo mã khoa unique
        $check_unique = $conn->prepare("SELECT COUNT(*) FROM department WHERE department_code = ? AND department_id != ?");
        $check_unique->execute([$dept_code, $dept['department_id']]);
        $count = $check_unique->fetchColumn();
        
        if ($count > 0) {
            $dept_code .= $dept['department_id']; // Thêm ID để unique
        }
        
        // Cập nhật mã khoa
        $update_stmt = $conn->prepare("UPDATE department SET department_code = ? WHERE department_id = ?");
        $update_stmt->execute([$dept_code, $dept['department_id']]);
        
        echo "✅ {$dept['department_name']} -> Mã khoa: $dept_code<br>";
    }
    
    echo "<h3>3. Kiểm tra kết quả</h3>";
    $result_stmt = $conn->query("SELECT department_id, department_code, department_name FROM department ORDER BY department_id");
    
    echo "<table border='1'>";
    echo "<tr><th>ID</th><th>Mã khoa</th><th>Tên khoa</th></tr>";
    while ($row = $result_stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "<tr>";
        echo "<td>{$row['department_id']}</td>";
        echo "<td>{$row['department_code']}</td>";
        echo "<td>{$row['department_name']}</td>";
        echo "</tr>";
    }
    echo "</table>";
    
} catch (Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "<br>";
    echo "Details: " . $e->getTraceAsString() . "<br>";
}
?> 