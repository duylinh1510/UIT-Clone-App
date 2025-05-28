<?php
echo "<h2>Test Program Classes API</h2>";

echo "<h3>1. Test lấy tất cả program classes</h3>";
echo "<a href='admin/program_classes.php' target='_blank'>GET admin/program_classes.php</a><br><br>";

echo "<h3>2. Test lấy program classes theo khoa CNTT (ID=1)</h3>";
echo "<a href='admin/program_classes.php?department_id=1' target='_blank'>GET admin/program_classes.php?department_id=1</a><br><br>";

echo "<h3>3. Test lấy program classes theo khoa Hệ thống thông tin (ID=2)</h3>";
echo "<a href='admin/program_classes.php?department_id=2' target='_blank'>GET admin/program_classes.php?department_id=2</a><br><br>";

echo "<hr>";
echo "<h3>Kiểm tra dữ liệu hiện tại</h3>";

require_once 'db_connect.php';

if ($conn === null) {
    echo "❌ Database connection failed<br>";
} else {
    echo "✅ Database connected successfully<br><br>";
    
    try {
        // Test program classes
        echo "<h4>All Program Classes:</h4>";
        $pc_stmt = $conn->query("SELECT pc.program_class_id, pc.program_class_code, pc.year, 
                                        d.department_name, t.teacher_full_name
                                 FROM program_class pc 
                                 LEFT JOIN department d ON pc.department_id = d.department_id 
                                 LEFT JOIN teacher t ON pc.teacher_id = t.teacher_id 
                                 ORDER BY pc.program_class_code");
        $program_classes = $pc_stmt->fetchAll(PDO::FETCH_ASSOC);
        
        echo "<table border='1'>";
        echo "<tr><th>ID</th><th>Code</th><th>Year</th><th>Department</th><th>Teacher</th></tr>";
        foreach ($program_classes as $pc) {
            echo "<tr>";
            echo "<td>{$pc['program_class_id']}</td>";
            echo "<td>{$pc['program_class_code']}</td>";
            echo "<td>{$pc['year']}</td>";
            echo "<td>{$pc['department_name']}</td>";
            echo "<td>{$pc['teacher_full_name']}</td>";
            echo "</tr>";
        }
        echo "</table>";
        
    } catch (Exception $e) {
        echo "❌ Error: " . $e->getMessage() . "<br>";
    }
}
?> 