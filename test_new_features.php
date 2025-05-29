<?php
echo "<h2>Test New Features</h2>";

require_once 'db_connect.php';

if ($conn === null) {
    echo "❌ Database connection failed<br>";
    exit;
}

echo "✅ Database connected successfully<br><br>";

try {
    // 1. Test Subject Classes API 
    echo "<h3>1. Test Subject Classes API (for viewing classes of a subject)</h3>";
    
    // Get first subject for testing
    $subject_stmt = $conn->query("SELECT subject_id, subject_code, name FROM subject LIMIT 1");
    $subject = $subject_stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($subject) {
        echo "<p>Testing with subject: <strong>{$subject['subject_code']} - {$subject['name']}</strong></p>";
        
        $subject_id = $subject['subject_id'];
        $api_url = "admin/subject_classes.php?subject_id=$subject_id";
        echo "<p>API Call: <a href='$api_url' target='_blank'>$api_url</a></p>";
        
        // Manually execute the query to show results
        $sc_stmt = $conn->prepare("SELECT sc.subject_class_id, sc.subject_class_code, sc.semester,
                                         sc.subject_id, s.subject_code, s.name as subject_name, s.credits,
                                         sc.teacher_id, t.teacher_full_name,
                                         d.department_name
                                  FROM subject_class sc
                                  LEFT JOIN subject s ON sc.subject_id = s.subject_id
                                  LEFT JOIN teacher t ON sc.teacher_id = t.teacher_id
                                  LEFT JOIN department d ON s.department_id = d.department_id
                                  WHERE sc.subject_id = ?
                                  ORDER BY sc.semester DESC, s.subject_code");
        $sc_stmt->execute([$subject_id]);
        $subject_classes = $sc_stmt->fetchAll(PDO::FETCH_ASSOC);
        
        if (count($subject_classes) > 0) {
            echo "<table border='1' cellpadding='5'>";
            echo "<tr><th>ID</th><th>Class Code</th><th>Semester</th><th>Teacher</th><th>Department</th></tr>";
            foreach ($subject_classes as $sc) {
                echo "<tr>";
                echo "<td>{$sc['subject_class_id']}</td>";
                echo "<td><strong>{$sc['subject_class_code']}</strong></td>";
                echo "<td>{$sc['semester']}</td>";
                echo "<td>" . ($sc['teacher_full_name'] ?: 'Chưa có GV') . "</td>";
                echo "<td>" . ($sc['department_name'] ?: 'N/A') . "</td>";
                echo "</tr>";
            }
            echo "</table><br>";
        } else {
            echo "<p>❌ No subject classes found for this subject. Creating some...</p>";
            
            // Create sample subject classes
            $teachers_stmt = $conn->query("SELECT teacher_id, teacher_full_name FROM teacher LIMIT 2");
            $teachers = $teachers_stmt->fetchAll(PDO::FETCH_ASSOC);
            
            if (count($teachers) > 0) {
                $semesters = ['HK1 2024-2025', 'HK2 2024-2025'];
                $class_codes = ['L01', 'L02', 'T01'];
                
                $insert_sql = "INSERT INTO subject_class (subject_class_code, subject_id, teacher_id, semester) VALUES (?, ?, ?, ?)";
                $insert_stmt = $conn->prepare($insert_sql);
                
                foreach ($semesters as $semester) {
                    foreach ($class_codes as $i => $class_code) {
                        $full_code = $subject['subject_code'] . '.' . $class_code;
                        $teacher = $teachers[$i % count($teachers)];
                        
                        $insert_stmt->execute([
                            $full_code,
                            $subject_id,
                            $teacher['teacher_id'],
                            $semester
                        ]);
                        
                        echo "✅ Created: $full_code - {$teacher['teacher_full_name']} - $semester<br>";
                    }
                }
                echo "<p>🔄 <a href='test_new_features.php'>Reload page</a> to see the new data</p>";
            }
        }
    } else {
        echo "<p>❌ No subjects found in database</p>";
    }
    
    echo "<hr>";
    
    // 2. Test Grades with Subject Class Code
    echo "<h3>2. Test Grades with Subject Class Code</h3>";
    
    $grades_stmt = $conn->prepare("SELECT g.id as grade_id, g.student_id, g.subject_class_id, g.semester,
                                         g.process_grade, g.practice_grade, g.midterm_grade, g.final_grade,
                                         st.student_code, st.student_full_name as student_name,
                                         s.subject_code, s.name as subject_name, s.credits,
                                         sc.subject_class_code, sc.semester as class_semester
                                  FROM grade g
                                  JOIN student st ON g.student_id = st.student_id
                                  JOIN subject_class sc ON g.subject_class_id = sc.subject_class_id
                                  JOIN subject s ON sc.subject_id = s.subject_id
                                  ORDER BY g.semester DESC, st.student_code, s.subject_code
                                  LIMIT 10");
    $grades_stmt->execute();
    $grades = $grades_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (count($grades) > 0) {
        echo "<p>✅ Found " . count($grades) . " grade records with subject class codes:</p>";
        echo "<table border='1' cellpadding='5'>";
        echo "<tr><th>Student</th><th>Subject</th><th>Class Code</th><th>Semester</th><th>QT</th><th>TH</th><th>GK</th><th>CK</th></tr>";
        
        foreach ($grades as $grade) {
            echo "<tr>";
            echo "<td>{$grade['student_code']} - {$grade['student_name']}</td>";
            echo "<td>{$grade['subject_code']} - {$grade['subject_name']}</td>";
            echo "<td><strong>{$grade['subject_class_code']}</strong></td>";
            echo "<td>{$grade['semester']}</td>";
            echo "<td>" . ($grade['process_grade'] ?: '--') . "</td>";
            echo "<td>" . ($grade['practice_grade'] ?: '--') . "</td>";
            echo "<td>" . ($grade['midterm_grade'] ?: '--') . "</td>";
            echo "<td>" . ($grade['final_grade'] ?: '--') . "</td>";
            echo "</tr>";
        }
        echo "</table><br>";
        
        echo "<p><strong>Ý nghĩa:</strong></p>";
        echo "<ul>";
        echo "<li>Mã môn (ví dụ: CS101): Mã của môn học</li>";
        echo "<li>Mã lớp (ví dụ: CS101.L01): Mã của lớp cụ thể của môn học đó</li>";
        echo "<li>Trong Android app, sẽ hiển thị: 'Nhập môn lập trình (CS101.L01)'</li>";
        echo "</ul>";
        
    } else {
        echo "<p>❌ No grade records found. Need to create some sample data first.</p>";
        echo "<p><a href='create_sample_data_fixed.php'>Create sample data</a> first</p>";
    }
    
    echo "<hr>";
    
    // 3. Test Update Subject Class Teacher API
    echo "<h3>3. Test Update Subject Class Teacher API</h3>";
    
    $sc_test_stmt = $conn->query("SELECT sc.subject_class_id, sc.subject_class_code, sc.teacher_id, t.teacher_full_name
                                  FROM subject_class sc 
                                  LEFT JOIN teacher t ON sc.teacher_id = t.teacher_id 
                                  LIMIT 1");
    $sc_test = $sc_test_stmt->fetch(PDO::FETCH_ASSOC);
    
    $other_teachers_stmt = $conn->query("SELECT teacher_id, teacher_full_name FROM teacher LIMIT 3");
    $other_teachers = $other_teachers_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($sc_test && count($other_teachers) >= 2) {
        echo "<p>Test subject class: <strong>{$sc_test['subject_class_code']}</strong></p>";
        echo "<p>Current teacher: {$sc_test['teacher_full_name']}</p>";
        echo "<p>Available teachers for change:</p>";
        echo "<ul>";
        foreach ($other_teachers as $teacher) {
            if ($teacher['teacher_id'] != $sc_test['teacher_id']) {
                echo "<li>ID {$teacher['teacher_id']}: {$teacher['teacher_full_name']}</li>";
            }
        }
        echo "</ul>";
        
        echo "<p><strong>Example PUT request to update teacher:</strong></p>";
        echo "<pre>";
        echo "URL: admin/subject_classes.php\n";
        echo "Method: PUT\n";
        echo "Body: {\n";
        echo "  \"subject_class_id\": {$sc_test['subject_class_id']},\n";
        echo "  \"teacher_id\": " . $other_teachers[1]['teacher_id'] . "\n";
        echo "}\n";
        echo "</pre>";
        
    } else {
        echo "<p>❌ Need at least 1 subject class and 2+ teachers to test this feature</p>";
    }
    
} catch (Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "<br>";
}
?> 