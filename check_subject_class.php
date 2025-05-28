<?php
echo "<h2>Check Subject Class Structure</h2>";

require_once 'db_connect.php';

if ($conn === null) {
    echo "❌ Database connection failed<br>";
} else {
    echo "✅ Database connected successfully<br><br>";
    
    try {
        // 1. Kiểm tra cấu trúc bảng subject_class
        echo "<h3>1. Subject Class Table Structure:</h3>";
        $structure_stmt = $conn->query("DESCRIBE subject_class");
        $columns = $structure_stmt->fetchAll(PDO::FETCH_ASSOC);
        
        echo "<table border='1'>";
        echo "<tr><th>Field</th><th>Type</th><th>Null</th><th>Key</th><th>Default</th><th>Extra</th></tr>";
        foreach ($columns as $col) {
            echo "<tr>";
            echo "<td>{$col['Field']}</td>";
            echo "<td>{$col['Type']}</td>";
            echo "<td>{$col['Null']}</td>";
            echo "<td>{$col['Key']}</td>";
            echo "<td>{$col['Default']}</td>";
            echo "<td>{$col['Extra']}</td>";
            echo "</tr>";
        }
        echo "</table>";
        
        // 2. Kiểm tra dữ liệu subject_class hiện có
        echo "<h3>2. Current Subject Class Data:</h3>";
        $data_stmt = $conn->query("SELECT sc.subject_class_id, sc.subject_class_code, sc.semester,
                                          s.subject_id, s.subject_code, s.name as subject_name,
                                          t.teacher_id, t.teacher_full_name
                                   FROM subject_class sc
                                   LEFT JOIN subject s ON sc.subject_id = s.subject_id
                                   LEFT JOIN teacher t ON sc.teacher_id = t.teacher_id
                                   ORDER BY sc.subject_class_id
                                   LIMIT 10");
        $subject_classes = $data_stmt->fetchAll(PDO::FETCH_ASSOC);
        
        if (count($subject_classes) > 0) {
            echo "<table border='1'>";
            echo "<tr><th>SC ID</th><th>SC Code</th><th>Semester</th><th>Subject ID</th><th>Subject Code</th><th>Subject Name</th><th>Teacher</th></tr>";
            foreach ($subject_classes as $sc) {
                echo "<tr>";
                echo "<td>{$sc['subject_class_id']}</td>";
                echo "<td>{$sc['subject_class_code']}</td>";
                echo "<td>{$sc['semester']}</td>";
                echo "<td>{$sc['subject_id']}</td>";
                echo "<td>{$sc['subject_code']}</td>";
                echo "<td>{$sc['subject_name']}</td>";
                echo "<td>{$sc['teacher_full_name']}</td>";
                echo "</tr>";
            }
            echo "</table>";
        } else {
            echo "<p>❌ Không có dữ liệu subject_class nào!</p>";
            
            echo "<h3>3. Tạo dữ liệu subject_class mẫu:</h3>";
            
            // Lấy subjects và teachers có sẵn
            $subjects_stmt = $conn->query("SELECT subject_id, subject_code, name FROM subject LIMIT 5");
            $subjects = $subjects_stmt->fetchAll(PDO::FETCH_ASSOC);
            
            $teachers_stmt = $conn->query("SELECT teacher_id, teacher_full_name FROM teacher LIMIT 3");
            $teachers = $teachers_stmt->fetchAll(PDO::FETCH_ASSOC);
            
            if (count($subjects) > 0 && count($teachers) > 0) {
                echo "<p>Tạo subject_class từ subjects và teachers hiện có...</p>";
                
                $semesters = ['HK1 2024-2025', 'HK2 2024-2025'];
                $insert_sql = "INSERT INTO subject_class (subject_class_code, subject_id, teacher_id, semester) VALUES (?, ?, ?, ?)";
                $insert_stmt = $conn->prepare($insert_sql);
                
                $count = 0;
                foreach ($subjects as $subject) {
                    foreach ($semesters as $semester) {
                        $teacher = $teachers[$count % count($teachers)]; // Rotate teachers
                        $subject_class_code = $subject['subject_code'] . '-' . ($count + 1);
                        
                        $insert_stmt->execute([
                            $subject_class_code,
                            $subject['subject_id'],
                            $teacher['teacher_id'],
                            $semester
                        ]);
                        
                        echo "✅ Created: {$subject_class_code} - {$subject['name']} - {$teacher['teacher_full_name']} - {$semester}<br>";
                        $count++;
                    }
                }
                
                echo "<br><p>🔄 Reload trang để xem dữ liệu mới!</p>";
            } else {
                echo "<p>❌ Cần có dữ liệu subjects và teachers trước!</p>";
            }
        }
        
        // 3. Kiểm tra grades hiện có
        echo "<h3>4. Current Grades using Subject Classes:</h3>";
        $grades_stmt = $conn->query("SELECT g.grade_id, s.student_code, sub.subject_code, sub.name as subject_name,
                                           sc.subject_class_code, sc.semester,
                                           g.process_grade, g.practice_grade, g.midterm_grade, g.final_grade
                                    FROM grade g
                                    JOIN student s ON g.student_id = s.student_id
                                    JOIN subject_class sc ON g.subject_class_id = sc.subject_class_id
                                    JOIN subject sub ON sc.subject_id = sub.subject_id
                                    ORDER BY g.grade_id
                                    LIMIT 5");
        $grades = $grades_stmt->fetchAll(PDO::FETCH_ASSOC);
        
        if (count($grades) > 0) {
            echo "<table border='1'>";
            echo "<tr><th>Grade ID</th><th>Student</th><th>Subject</th><th>Class Code</th><th>Semester</th><th>QT</th><th>TH</th><th>GK</th><th>CK</th></tr>";
            foreach ($grades as $grade) {
                echo "<tr>";
                echo "<td>{$grade['grade_id']}</td>";
                echo "<td>{$grade['student_code']}</td>";
                echo "<td>{$grade['subject_code']} - {$grade['subject_name']}</td>";
                echo "<td>{$grade['subject_class_code']}</td>";
                echo "<td>{$grade['semester']}</td>";
                echo "<td>{$grade['process_grade']}</td>";
                echo "<td>{$grade['practice_grade']}</td>";
                echo "<td>{$grade['midterm_grade']}</td>";
                echo "<td>{$grade['final_grade']}</td>";
                echo "</tr>";
            }
            echo "</table>";
        } else {
            echo "<p>Chưa có dữ liệu grades nào sử dụng subject_class.</p>";
        }
        
    } catch (Exception $e) {
        echo "❌ Error: " . $e->getMessage() . "<br>";
    }
}
?> 