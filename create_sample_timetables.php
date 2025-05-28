<?php
require_once 'db_connect.php';

try {
    echo "<h2>Tạo dữ liệu mẫu cho Timetable</h2>";
    
    // Check if timetable table exists and has data
    $count_sql = "SELECT COUNT(*) as count FROM timetable";
    $count_stmt = $conn->prepare($count_sql);
    $count_stmt->execute();
    $count = $count_stmt->fetch()['count'];
    
    echo "<p>Timetable hiện tại có: <strong>$count</strong> bản ghi</p>";
    
    if ($count == 0) {
        echo "<h3>📝 Tạo dữ liệu mẫu timetable...</h3>";
        
        // Sample timetable data
        // day_of_week: 1=Chủ nhật, 2=Thứ hai, ..., 7=Thứ bảy
        // period: 1-12 (tiết học)
        $sample_timetables = [
            // Thứ hai (day_of_week = 2)
            [1, 2, 1, '07:00:00', '08:30:00'], // subject_class_id=1 (MB101A), tiết 1
            [2, 2, 2, '08:30:00', '10:00:00'], // subject_class_id=2 (DA101A), tiết 2
            
            // Thứ ba (day_of_week = 3)
            [1, 3, 3, '10:15:00', '11:45:00'], // MB101A, tiết 3
            [2, 3, 4, '13:00:00', '14:30:00'], // DA101A, tiết 4
            
            // Thứ tư (day_of_week = 4)
            [1, 4, 1, '07:00:00', '08:30:00'], // MB101A, tiết 1
            [2, 4, 5, '14:30:00', '16:00:00'], // DA101A, tiết 5
            
            // Thứ năm (day_of_week = 5)
            [1, 5, 2, '08:30:00', '10:00:00'], // MB101A, tiết 2
            [2, 5, 3, '10:15:00', '11:45:00'], // DA101A, tiết 3
            
            // Thứ sáu (day_of_week = 6)
            [1, 6, 6, '16:00:00', '17:30:00'], // MB101A, tiết 6
            [2, 6, 1, '07:00:00', '08:30:00'], // DA101A, tiết 1
        ];
        
        $timetable_sql = "INSERT INTO timetable (subject_class_id, day_of_week, period, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        $timetable_stmt = $conn->prepare($timetable_sql);
        
        foreach ($sample_timetables as $index => $tt) {
            $timetable_stmt->execute($tt);
            
            $day_names = ['', 'Chủ nhật', 'Thứ hai', 'Thứ ba', 'Thứ tư', 'Thứ năm', 'Thứ sáu', 'Thứ bảy'];
            $day_name = $day_names[$tt[1]];
            
            echo "<p>✅ Thêm timetable " . ($index + 1) . ": Subject Class {$tt[0]} - {$day_name} - Tiết {$tt[2]} ({$tt[3]} - {$tt[4]})</p>";
        }
        
        echo "<h3>✅ Hoàn tất tạo dữ liệu mẫu!</h3>";
        
    } else {
        echo "<p>⚠️ Đã có dữ liệu, không tạo mới.</p>";
    }
    
    // Display current timetable data
    echo "<h3>📋 Dữ liệu Timetable hiện tại:</h3>";
    
    $display_sql = "SELECT tt.timetable_id, tt.subject_class_id, tt.day_of_week, tt.period,
                           tt.start_time, tt.end_time,
                           sc.subject_class_code, sc.semester,
                           s.subject_code, s.name as subject_name,
                           t.teacher_full_name,
                           CASE tt.day_of_week
                               WHEN 1 THEN 'Chủ nhật'
                               WHEN 2 THEN 'Thứ hai'
                               WHEN 3 THEN 'Thứ ba'
                               WHEN 4 THEN 'Thứ tư'
                               WHEN 5 THEN 'Thứ năm'
                               WHEN 6 THEN 'Thứ sáu'
                               WHEN 7 THEN 'Thứ bảy'
                           END as day_name
                    FROM timetable tt
                    JOIN subject_class sc ON tt.subject_class_id = sc.subject_class_id
                    JOIN subject s ON sc.subject_id = s.subject_id
                    LEFT JOIN teacher t ON sc.teacher_id = t.teacher_id
                    ORDER BY tt.day_of_week, tt.period";
    
    $display_stmt = $conn->prepare($display_sql);
    $display_stmt->execute();
    $timetables = $display_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<table border='1' style='border-collapse: collapse; width: 100%;'>";
    echo "<tr style='background-color: #f0f0f0;'>";
    echo "<th>ID</th><th>Thứ</th><th>Tiết</th><th>Thời gian</th><th>Môn học</th><th>Lớp</th><th>Giáo viên</th><th>Học kỳ</th>";
    echo "</tr>";
    
    foreach ($timetables as $tt) {
        echo "<tr>";
        echo "<td>{$tt['timetable_id']}</td>";
        echo "<td>{$tt['day_name']}</td>";
        echo "<td>Tiết {$tt['period']}</td>";
        echo "<td>{$tt['start_time']} - {$tt['end_time']}</td>";
        echo "<td>{$tt['subject_code']} - {$tt['subject_name']}</td>";
        echo "<td>{$tt['subject_class_code']}</td>";
        echo "<td>{$tt['teacher_full_name']}</td>";
        echo "<td>{$tt['semester']}</td>";
        echo "</tr>";
    }
    
    echo "</table>";
    
    echo "<h3>🧪 Test API endpoints:</h3>";
    echo "<ul>";
    echo "<li><a href='admin/timetables.php' target='_blank'>GET admin/timetables.php</a> - Lấy tất cả timetable</li>";
    echo "<li><a href='admin/timetables.php?day_of_week=2' target='_blank'>GET admin/timetables.php?day_of_week=2</a> - Thứ hai</li>";
    echo "<li><a href='admin/timetables.php?search=mobile' target='_blank'>GET admin/timetables.php?search=mobile</a> - Tìm kiếm</li>";
    echo "</ul>";

} catch (Exception $e) {
    echo "❌ Lỗi: " . $e->getMessage();
    echo "<br>Stack trace: <pre>" . $e->getTraceAsString() . "</pre>";
}
?> 