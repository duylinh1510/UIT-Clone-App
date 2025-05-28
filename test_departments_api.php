<?php
echo "<h2>Test Departments API (Updated with department_code)</h2>";

echo "<h3>1. Test lấy tất cả departments</h3>";
echo "<a href='admin/departments.php' target='_blank'>GET admin/departments.php</a><br><br>";

echo "<h3>2. Test search departments</h3>";
echo "<a href='admin/departments.php?search=CNTT' target='_blank'>Search 'CNTT'</a><br>";
echo "<a href='admin/departments.php?search=thông tin' target='_blank'>Search 'thông tin'</a><br><br>";

echo "<hr>";
echo "<h3>Kiểm tra dữ liệu hiện tại</h3>";

require_once 'db_connect.php';

if ($conn === null) {
    echo "❌ Database connection failed<br>";
} else {
    echo "✅ Database connected successfully<br><br>";
    
    try {
        // Check if department_code column exists
        $check_col = $conn->query("SHOW COLUMNS FROM department LIKE 'department_code'");
        if ($check_col->rowCount() > 0) {
            echo "✅ Cột department_code đã có trong bảng department<br>";
        } else {
            echo "❌ Cột department_code chưa có, cần chạy script fix_department_code.php<br>";
        }
        
        // Test departments with codes
        echo "<h4>All Departments with Codes:</h4>";
        $departments_stmt = $conn->query("SELECT d.department_id, d.department_code, d.department_name,
                                                 (SELECT COUNT(*) FROM program_class pc WHERE pc.department_id = d.department_id) as class_count,
                                                 (SELECT COUNT(*) FROM subject sub WHERE sub.department_id = d.department_id) as subject_count
                                          FROM department d 
                                          ORDER BY d.department_id");
        $departments = $departments_stmt->fetchAll(PDO::FETCH_ASSOC);
        
        echo "<table border='1'>";
        echo "<tr><th>ID</th><th>Mã khoa</th><th>Tên khoa</th><th>Số lớp</th><th>Số môn học</th></tr>";
        foreach ($departments as $dept) {
            echo "<tr>";
            echo "<td>{$dept['department_id']}</td>";
            echo "<td>" . ($dept['department_code'] ?? 'NULL') . "</td>";
            echo "<td>{$dept['department_name']}</td>";
            echo "<td>{$dept['class_count']}</td>";
            echo "<td>{$dept['subject_count']}</td>";
            echo "</tr>";
        }
        echo "</table>";
        
        echo "<h4>Test JSON Response:</h4>";
        echo "<p>Kiểm tra response format của API:</p>";
        echo "<pre>";
        
        // Simulate API call
        $api_data = [];
        foreach ($departments as $dept) {
            $api_data[] = [
                'department_id' => (int)$dept['department_id'],
                'department_code' => $dept['department_code'],
                'department_name' => $dept['department_name'],
                'class_count' => (int)$dept['class_count'],
                'subject_count' => (int)$dept['subject_count']
            ];
        }
        
        echo json_encode([
            'success' => true,
            'data' => $api_data
        ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
        
        echo "</pre>";
        
    } catch (Exception $e) {
        echo "❌ Error: " . $e->getMessage() . "<br>";
    }
}
?> 