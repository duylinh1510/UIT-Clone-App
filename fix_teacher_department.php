<?php
require_once 'db_connect.php';

echo "<h2>Sửa lỗi Teacher Department</h2>";

try {
    echo "<h3>1. Thêm cột department_id vào bảng teacher</h3>";
    
    // Kiểm tra xem cột department_id đã tồn tại chưa
    $check_column = $conn->query("SHOW COLUMNS FROM teacher LIKE 'department_id'");
    if ($check_column->rowCount() == 0) {
        $sql = "ALTER TABLE teacher ADD COLUMN department_id INT(11) NULL AFTER teacher_email";
        $conn->exec($sql);
        echo "✅ Đã thêm cột department_id vào bảng teacher<br>";
        
        // Thêm foreign key constraint
        $fk_sql = "ALTER TABLE teacher ADD CONSTRAINT fk_teacher_department 
                   FOREIGN KEY (department_id) REFERENCES department(department_id) 
                   ON DELETE SET NULL ON UPDATE CASCADE";
        $conn->exec($fk_sql);
        echo "✅ Đã thêm foreign key constraint<br>";
    } else {
        echo "⚠️ Cột department_id đã tồn tại<br>";
    }
    
    echo "<h3>2. Cập nhật dữ liệu teacher với department_id mặc định</h3>";
    
    // Gán department_id cho các teacher hiện tại
    // Teacher 1: Dương Phi Long -> Khoa CNTT (id=1)
    // Teacher 2: Nguyễn Minh Nhựt -> Khoa CNTT (id=1) 
    // Teacher 3: Thai Bao Tran -> Hệ thống thông tin (id=2)
    
    $updates = [
        [1, 1, "Dương Phi Long -> Khoa CNTT"],
        [2, 1, "Nguyễn Minh Nhựt -> Khoa CNTT"],
        [3, 2, "Thai Bao Tran -> Hệ thống thông tin"]
    ];
    
    foreach ($updates as [$teacher_id, $dept_id, $desc]) {
        $stmt = $conn->prepare("UPDATE teacher SET department_id = ? WHERE teacher_id = ?");
        $stmt->execute([$dept_id, $teacher_id]);
        echo "✅ $desc<br>";
    }
    
    echo "<h3>3. Kiểm tra kết quả</h3>";
    $stmt = $conn->query("SELECT t.teacher_id, t.teacher_full_name, t.department_id, d.department_name 
                          FROM teacher t 
                          LEFT JOIN department d ON t.department_id = d.department_id 
                          ORDER BY t.teacher_id");
    
    echo "<table border='1'>";
    echo "<tr><th>ID</th><th>Tên</th><th>Dept ID</th><th>Tên Khoa</th></tr>";
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "<tr>";
        echo "<td>{$row['teacher_id']}</td>";
        echo "<td>{$row['teacher_full_name']}</td>";
        echo "<td>{$row['department_id']}</td>";
        echo "<td>{$row['department_name']}</td>";
        echo "</tr>";
    }
    echo "</table>";
    
} catch (Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "<br>";
    echo "Details: " . $e->getTraceAsString() . "<br>";
}
?> 