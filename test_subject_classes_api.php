<?php
echo "<h2>Test Subject Classes API</h2>";

echo "<h3>1. Test Subject Classes API</h3>";
echo "<a href='admin/subject_classes.php' target='_blank'>GET admin/subject_classes.php</a><br>";
echo "<a href='admin/subject_classes.php?semester=2025HK1' target='_blank'>GET với semester=2025HK1</a><br><br>";

echo "<h3>2. Test JSON Response</h3>";

require_once 'db_connect.php';

if ($conn === null) {
    echo "❌ Database connection failed<br>";
} else {
    echo "✅ Database connected successfully<br><br>";
    
    try {
        // Test API response
        echo "<h4>Subject Classes Data:</h4>";
        $stmt = $conn->query("SELECT sc.subject_class_id, sc.subject_class_code, sc.semester,
                                     sc.subject_id, s.subject_code, s.name as subject_name, s.credits,
                                     sc.teacher_id, t.teacher_full_name,
                                     d.department_name
                              FROM subject_class sc 
                              LEFT JOIN subject s ON sc.subject_id = s.subject_id
                              LEFT JOIN teacher t ON sc.teacher_id = t.teacher_id
                              LEFT JOIN department d ON s.department_id = d.department_id
                              ORDER BY sc.semester DESC, s.subject_code");
        $subject_classes = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Convert numeric fields
        foreach ($subject_classes as &$sc) {
            $sc['subject_class_id'] = (int)$sc['subject_class_id'];
            $sc['subject_id'] = (int)$sc['subject_id'];
            $sc['teacher_id'] = (int)$sc['teacher_id'];
            $sc['credits'] = (int)$sc['credits'];
        }
        
        echo "<pre>";
        echo json_encode([
            'success' => true,
            'data' => $subject_classes
        ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
        echo "</pre>";
        
        echo "<h4>Subject Classes hiển thị trong Android spinner:</h4>";
        echo "<ul>";
        foreach ($subject_classes as $sc) {
            $display_text = $sc['subject_code'] . " - " . $sc['subject_name'] . " (" . $sc['subject_class_code'] . ")";
            echo "<li><strong>ID {$sc['subject_class_id']}:</strong> {$display_text}</li>";
        }
        echo "</ul>";
        
        echo "<h4>Logic mapping trong Android:</h4>";
        echo "<div style='background: #f0f8ff; padding: 15px; border: 1px solid #ccc;'>";
        echo "<p><strong>Khi user chọn:</strong> 'MB101 - Lập trình mobile (MB101A)'</p>";
        echo "<p><strong>Android sẽ lấy:</strong> subject_class_id = 1</p>";
        echo "<p><strong>Gửi API create grade với:</strong> subject_class_id = 1</p>";
        echo "<p><strong>Database sẽ lưu:</strong> grade record với subject_class_id = 1</p>";
        echo "<p><strong>Khi hiển thị lại:</strong> JOIN với subject_class để lấy tên môn học đúng</p>";
        echo "</div>";
        
    } catch (Exception $e) {
        echo "❌ Error: " . $e->getMessage() . "<br>";
    }
}
?> 