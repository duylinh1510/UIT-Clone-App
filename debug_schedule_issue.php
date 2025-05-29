<?php
require_once 'db_connect.php';

echo "<h2>🔍 Debug Thời Khóa Biểu - Nguyễn Tiến Đạt</h2>";

try {
    // 1. Kiểm tra thông tin sinh viên Đạt
    echo "<h3>👨‍🎓 Thông tin sinh viên Nguyễn Tiến Đạt:</h3>";
    $student_sql = "SELECT s.*, u.username, u.role 
                    FROM student s 
                    JOIN user u ON s.user_id = u.user_id 
                    WHERE s.student_code = '22520226' OR s.student_full_name LIKE '%Tiến Đạt%'";
    $student_stmt = $conn->prepare($student_sql);
    $student_stmt->execute();
    $student = $student_stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($student) {
        echo "<table border='1' style='border-collapse: collapse; margin: 10px 0;'>";
        echo "<tr><th>Field</th><th>Value</th></tr>";
        foreach ($student as $key => $value) {
            echo "<tr><td>$key</td><td>$value</td></tr>";
        }
        echo "</table>";
        
        $student_id = $student['student_id'];
        echo "<p><strong>Student ID sử dụng: $student_id</strong></p>";
    } else {
        echo "<p style='color: red;'>❌ Không tìm thấy sinh viên Nguyễn Tiến Đạt</p>";
        exit;
    }
    
    // 2. Kiểm tra student_enrollment
    echo "<h3>📚 Student Enrollment Records:</h3>";
    $enrollment_sql = "SELECT se.*, sc.subject_class_code, s.subject_code, s.name as subject_name
                       FROM student_enrollment se
                       JOIN subject_class sc ON se.subject_class_id = sc.subject_class_id
                       JOIN subject s ON sc.subject_id = s.subject_id
                       WHERE se.student_id = ?";
    $enrollment_stmt = $conn->prepare($enrollment_sql);
    $enrollment_stmt->execute([$student_id]);
    $enrollments = $enrollment_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($enrollments) {
        echo "<table border='1' style='border-collapse: collapse; margin: 10px 0;'>";
        echo "<tr><th>Enrollment ID</th><th>Subject Class ID</th><th>Subject Class Code</th><th>Subject</th><th>Status</th><th>Date</th></tr>";
        foreach ($enrollments as $enroll) {
            echo "<tr>";
            echo "<td>" . $enroll['enrollment_id'] . "</td>";
            echo "<td>" . $enroll['subject_class_id'] . "</td>";
            echo "<td>" . $enroll['subject_class_code'] . "</td>";
            echo "<td>" . $enroll['subject_code'] . " - " . $enroll['subject_name'] . "</td>";
            echo "<td>" . $enroll['status'] . "</td>";
            echo "<td>" . $enroll['enrollment_date'] . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    } else {
        echo "<p style='color: red;'>❌ Không có enrollment records cho sinh viên này</p>";
    }
    
    // 3. Kiểm tra timetable
    echo "<h3>⏰ Timetable Records:</h3>";
    $timetable_sql = "SELECT tt.*, sc.subject_class_code, s.subject_code, s.name as subject_name,
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
                      ORDER BY tt.day_of_week, tt.period";
    $timetable_stmt = $conn->prepare($timetable_sql);
    $timetable_stmt->execute();
    $timetables = $timetable_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($timetables) {
        echo "<table border='1' style='border-collapse: collapse; margin: 10px 0;'>";
        echo "<tr><th>Timetable ID</th><th>Subject Class ID</th><th>Day</th><th>Period</th><th>Time</th><th>Subject</th></tr>";
        foreach ($timetables as $tt) {
            echo "<tr>";
            echo "<td>" . $tt['timetable_id'] . "</td>";
            echo "<td>" . $tt['subject_class_id'] . "</td>";
            echo "<td>" . $tt['day_name'] . " (" . $tt['day_of_week'] . ")</td>";
            echo "<td>" . $tt['period'] . "</td>";
            echo "<td>" . $tt['start_time'] . " - " . $tt['end_time'] . "</td>";
            echo "<td>" . $tt['subject_class_code'] . " - " . $tt['subject_code'] . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    } else {
        echo "<p style='color: red;'>❌ Không có timetable records</p>";
    }
    
    // 4. Test query thời khóa biểu cho sinh viên này
    echo "<h3>🧪 Test Schedule Query cho Nguyễn Tiến Đạt (Thứ 2):</h3>";
    $test_sql = "SELECT tt.timetable_id as schedule_id, tt.subject_class_id, tt.day_of_week, tt.period,
                        tt.start_time, tt.end_time, '' as classroom,
                        s.subject_code, s.name as subject_name, s.credits,
                        subcl.subject_class_code as subject_class,
                        t.teacher_full_name,
                        d.department_name
                 FROM timetable tt
                 JOIN subject_class subcl ON tt.subject_class_id = subcl.subject_class_id
                 JOIN subject s ON subcl.subject_id = s.subject_id
                 LEFT JOIN teacher t ON subcl.teacher_id = t.teacher_id
                 LEFT JOIN department d ON s.department_id = d.department_id
                 JOIN student_enrollment se ON se.subject_class_id = subcl.subject_class_id
                 WHERE tt.day_of_week = 2 AND se.student_id = ? AND se.status = 'active'
                 ORDER BY tt.period";
    
    $test_stmt = $conn->prepare($test_sql);
    $test_stmt->execute([$student_id]);
    $schedules = $test_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<p><strong>Query Parameters:</strong></p>";
    echo "<ul>";
    echo "<li>day_of_week = 2 (Thứ hai)</li>";
    echo "<li>student_id = $student_id</li>";
    echo "</ul>";
    
    if ($schedules) {
        echo "<p style='color: green;'>✅ Tìm thấy " . count($schedules) . " lịch học cho Thứ 2:</p>";
        echo "<table border='1' style='border-collapse: collapse; margin: 10px 0;'>";
        echo "<tr><th>Period</th><th>Time</th><th>Subject</th><th>Class</th><th>Teacher</th></tr>";
        foreach ($schedules as $schedule) {
            echo "<tr>";
            echo "<td>" . $schedule['period'] . "</td>";
            echo "<td>" . $schedule['start_time'] . " - " . $schedule['end_time'] . "</td>";
            echo "<td>" . $schedule['subject_name'] . " (" . $schedule['subject_code'] . ")</td>";
            echo "<td>" . $schedule['subject_class'] . "</td>";
            echo "<td>" . $schedule['teacher_full_name'] . "</td>";
            echo "</tr>";
        }
        echo "</table>";
    } else {
        echo "<p style='color: red;'>❌ Không tìm thấy lịch học nào cho Thứ 2</p>";
    }
    
    // 5. Kiểm tra có conflict trong JOIN không
    echo "<h3>🔍 Debug JOIN Steps:</h3>";
    
    // Step 1: Timetable for Monday
    echo "<h4>Step 1: Timetable records for Monday (day_of_week = 2):</h4>";
    $step1_sql = "SELECT tt.timetable_id, tt.subject_class_id, tt.period, tt.start_time, tt.end_time
                  FROM timetable tt
                  WHERE tt.day_of_week = 2";
    $step1_stmt = $conn->prepare($step1_sql);
    $step1_stmt->execute();
    $step1_results = $step1_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($step1_results) {
        echo "<table border='1' style='border-collapse: collapse; margin: 10px 0;'>";
        echo "<tr><th>Timetable ID</th><th>Subject Class ID</th><th>Period</th><th>Time</th></tr>";
        foreach ($step1_results as $row) {
            echo "<tr><td>" . $row['timetable_id'] . "</td><td>" . $row['subject_class_id'] . "</td><td>" . $row['period'] . "</td><td>" . $row['start_time'] . " - " . $row['end_time'] . "</td></tr>";
        }
        echo "</table>";
    } else {
        echo "<p>No Monday timetables found</p>";
    }
    
    // Step 2: Student enrollments for our student
    echo "<h4>Step 2: Enrollments for student_id = $student_id:</h4>";
    $step2_sql = "SELECT se.enrollment_id, se.subject_class_id, se.status
                  FROM student_enrollment se
                  WHERE se.student_id = ? AND se.status = 'active'";
    $step2_stmt = $conn->prepare($step2_sql);
    $step2_stmt->execute([$student_id]);
    $step2_results = $step2_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($step2_results) {
        echo "<table border='1' style='border-collapse: collapse; margin: 10px 0;'>";
        echo "<tr><th>Enrollment ID</th><th>Subject Class ID</th><th>Status</th></tr>";
        foreach ($step2_results as $row) {
            echo "<tr><td>" . $row['enrollment_id'] . "</td><td>" . $row['subject_class_id'] . "</td><td>" . $row['status'] . "</td></tr>";
        }
        echo "</table>";
    } else {
        echo "<p>No active enrollments found</p>";
    }
    
    // Step 3: Intersection
    echo "<h4>Step 3: Monday timetables for enrolled subject_classes:</h4>";
    $step3_sql = "SELECT tt.timetable_id, tt.subject_class_id, tt.period, se.enrollment_id
                  FROM timetable tt
                  JOIN student_enrollment se ON se.subject_class_id = tt.subject_class_id
                  WHERE tt.day_of_week = 2 AND se.student_id = ? AND se.status = 'active'";
    $step3_stmt = $conn->prepare($step3_sql);
    $step3_stmt->execute([$student_id]);
    $step3_results = $step3_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if ($step3_results) {
        echo "<table border='1' style='border-collapse: collapse; margin: 10px 0;'>";
        echo "<tr><th>Timetable ID</th><th>Subject Class ID</th><th>Period</th><th>Enrollment ID</th></tr>";
        foreach ($step3_results as $row) {
            echo "<tr><td>" . $row['timetable_id'] . "</td><td>" . $row['subject_class_id'] . "</td><td>" . $row['period'] . "</td><td>" . $row['enrollment_id'] . "</td></tr>";
        }
        echo "</table>";
        echo "<p style='color: green;'>✅ Found " . count($step3_results) . " matching records!</p>";
    } else {
        echo "<p style='color: red;'>❌ No intersection found - this is the problem!</p>";
    }

} catch (Exception $e) {
    echo "<p style='color: red;'>Error: " . $e->getMessage() . "</p>";
}
?> 