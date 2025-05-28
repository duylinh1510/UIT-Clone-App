<?php
echo "<h2>Test Grade Management Logic</h2>";

echo "<h3>Kiểm tra logic quản lý điểm</h3>";
echo "<p><strong>Vấn đề cũ:</strong> Khi edit điểm, hệ thống vẫn cho phép thay đổi sinh viên, môn học, học kỳ</p>";
echo "<p><strong>Logic mới:</strong></p>";
echo "<ul>";
echo "<li><strong>ADD mode:</strong> Cho phép chọn sinh viên, môn học, học kỳ</li>";
echo "<li><strong>EDIT mode:</strong> Disable spinner, chỉ cho phép chỉnh sửa điểm số</li>";
echo "</ul>";

echo "<h3>1. Test API grades</h3>";
echo "<a href='admin/grades.php' target='_blank'>GET admin/grades.php</a><br><br>";

echo "<h3>2. Kiểm tra dữ liệu grades hiện tại</h3>";

require_once 'db_connect.php';

if ($conn === null) {
    echo "❌ Database connection failed<br>";
} else {
    echo "✅ Database connected successfully<br><br>";
    
    try {
        // Kiểm tra grades hiện có
        echo "<h4>Grades hiện tại trong database:</h4>";
        $grades_stmt = $conn->query("SELECT g.grade_id, s.student_code, s.student_full_name, 
                                            sub.name as subject_name, sc.semester,
                                            g.process_grade, g.practice_grade, g.midterm_grade, g.final_grade
                                     FROM grade g
                                     JOIN student s ON g.student_id = s.student_id
                                     JOIN subject_class sc ON g.subject_class_id = sc.subject_class_id
                                     JOIN subject sub ON sc.subject_id = sub.subject_id
                                     ORDER BY g.grade_id
                                     LIMIT 10");
        $grades = $grades_stmt->fetchAll(PDO::FETCH_ASSOC);
        
        if (count($grades) > 0) {
            echo "<table border='1'>";
            echo "<tr><th>ID</th><th>Sinh viên</th><th>Môn học</th><th>Học kỳ</th><th>QT</th><th>TH</th><th>GK</th><th>CK</th></tr>";
            foreach ($grades as $grade) {
                echo "<tr>";
                echo "<td>{$grade['grade_id']}</td>";
                echo "<td>{$grade['student_code']} - {$grade['student_full_name']}</td>";
                echo "<td>{$grade['subject_name']}</td>";
                echo "<td>{$grade['semester']}</td>";
                echo "<td>{$grade['process_grade']}</td>";
                echo "<td>{$grade['practice_grade']}</td>";
                echo "<td>{$grade['midterm_grade']}</td>";
                echo "<td>{$grade['final_grade']}</td>";
                echo "</tr>";
            }
            echo "</table>";
            
            echo "<h4>Logic mới trong Android app:</h4>";
            echo "<div style='background: #f0f8ff; padding: 15px; border: 1px solid #ccc;'>";
            echo "<h5>🔹 Chế độ ADD điểm mới:</h5>";
            echo "<ul>";
            echo "<li>✅ Hiển thị dropdown sinh viên</li>";
            echo "<li>✅ Hiển thị dropdown môn học</li>";
            echo "<li>✅ Hiển thị dropdown học kỳ</li>";
            echo "<li>✅ Validate tất cả field</li>";
            echo "</ul>";
            
            echo "<h5>🔹 Chế độ EDIT điểm:</h5>";
            echo "<ul>";
            echo "<li>🔒 Disable dropdown sinh viên (chỉ hiển thị thông tin hiện tại)</li>";
            echo "<li>🔒 Disable dropdown môn học (chỉ hiển thị thông tin hiện tại)</li>";
            echo "<li>🔒 Disable dropdown học kỳ (chỉ hiển thị thông tin hiện tại)</li>";
            echo "<li>✏️ Chỉ cho phép chỉnh sửa điểm số (QT, TH, GK, CK)</li>";
            echo "<li>🧮 Tự động tính điểm trung bình</li>";
            echo "<li>✅ Chỉ validate điểm số</li>";
            echo "</ul>";
            echo "</div>";
            
        } else {
            echo "<p>Chưa có dữ liệu grades. Hãy tạo một số điểm mẫu.</p>";
            
            echo "<h4>Tạo dữ liệu điểm mẫu:</h4>";
            echo "<p>Để test logic, bạn có thể:</p>";
            echo "<ol>";
            echo "<li>Vào app Android</li>";
            echo "<li>Đăng nhập admin</li>";
            echo "<li>Vào Admin → Grades</li>";
            echo "<li>Thêm một vài điểm mẫu</li>";
            echo "<li>Sau đó test chức năng Edit</li>";
            echo "</ol>";
        }
        
        echo "<h4>Cách test logic mới:</h4>";
        echo "<ol>";
        echo "<li><strong>Test ADD:</strong> Vào app → Admin → Grades → Thêm điểm mới</li>";
        echo "<li>Kiểm tra có thể chọn sinh viên, môn học, học kỳ</li>";
        echo "<li><strong>Test EDIT:</strong> Bấm nút edit một điểm có sẵn</li>";
        echo "<li>Kiểm tra các dropdown bị disable</li>";
        echo "<li>Kiểm tra chỉ có thể chỉnh sửa điểm số</li>";
        echo "<li>Kiểm tra điểm trung bình tự động tính</li>";
        echo "</ol>";
        
    } catch (Exception $e) {
        echo "❌ Error: " . $e->getMessage() . "<br>";
    }
}
?> 