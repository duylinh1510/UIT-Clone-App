<?php
require_once 'db_connect.php';

echo "<h2>Quick Test Teacher API</h2>";

try {
    // Test lấy teachers
    echo "<h3>1. Direct API Call Test:</h3>";
    echo "<a href='admin/teachers.php?page=1&limit=10' target='_blank'>Test GET teachers</a><br>";
    echo "<a href='admin/teachers.php?page=1&limit=10&department_id=1' target='_blank'>Test GET teachers department=1</a><br>";
    echo "<a href='admin/teachers.php?page=1&limit=10&department_id=2' target='_blank'>Test GET teachers department=2</a><br><br>";
    
    // Kiểm tra database structure
    echo "<h3>2. Database Check:</h3>";
    
    // Check if department_id column exists
    $check_col = $conn->query("SHOW COLUMNS FROM teacher LIKE 'department_id'");
    if ($check_col->rowCount() > 0) {
        echo "✅ Cột department_id đã có trong bảng teacher<br>";
        
        // Show teachers with departments
        echo "<h4>Teachers with Departments:</h4>";
        $teachers = $conn->query("SELECT t.teacher_id, t.teacher_full_name, t.department_id, d.department_name 
                                  FROM teacher t 
                                  LEFT JOIN department d ON t.department_id = d.department_id");
        
        echo "<table border='1'>";
        echo "<tr><th>ID</th><th>Name</th><th>Dept ID</th><th>Department</th></tr>";
        while ($teacher = $teachers->fetch(PDO::FETCH_ASSOC)) {
            echo "<tr>";
            echo "<td>{$teacher['teacher_id']}</td>";
            echo "<td>{$teacher['teacher_full_name']}</td>";
            echo "<td>" . ($teacher['department_id'] ?? 'NULL') . "</td>";
            echo "<td>" . ($teacher['department_name'] ?? 'Chưa phân khoa') . "</td>";
            echo "</tr>";
        }
        echo "</table>";
        
    } else {
        echo "❌ Cột department_id chưa có, cần chạy SQL script<br>";
        echo "<p>Hãy chạy file <code>fix_teacher_department_sql.sql</code> trong database</p>";
    }
    
    echo "<h3>3. Test Update API:</h3>";
    echo "<p>Để test update API, bạn có thể:</p>";
    echo "<ol>";
    echo "<li>Mở app Android</li>";
    echo "<li>Vào Admin -> Teachers</li>";
    echo "<li>Chỉnh sửa giáo viên Thai Bao Tran</li>";
    echo "<li>Chuyển từ khoa hiện tại sang 'Hệ thống thông tin'</li>";
    echo "<li>Lưu và kiểm tra kết quả</li>";
    echo "</ol>";
    
} catch (Exception $e) {
    echo "❌ Error: " . $e->getMessage();
}
?> 