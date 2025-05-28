<?php
require "../db_connect.php";
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

$method = $_SERVER['REQUEST_METHOD'];

try {
    switch ($method) {
        case 'GET':
            getTimetableStudents($conn);
            break;
        case 'POST':
            addStudentToTimetable($conn);
            break;
        case 'DELETE':
            removeStudentFromTimetable($conn);
            break;
        default:
            http_response_code(405);
            echo json_encode(['success' => false, 'message' => 'Method not allowed']);
            break;
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Server error: ' . $e->getMessage()]);
}

function getTimetableStudents($conn) {
    if (!isset($_GET['timetable_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Missing timetable_id']);
        return;
    }
    
    $timetable_id = (int)$_GET['timetable_id'];
    
    // Get timetable info first
    $timetable_sql = "SELECT tt.timetable_id, tt.subject_class_id, 
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
                             END as day_name,
                             tt.period, tt.start_time, tt.end_time
                      FROM timetable tt
                      JOIN subject_class sc ON tt.subject_class_id = sc.subject_class_id
                      JOIN subject s ON sc.subject_id = s.subject_id
                      LEFT JOIN teacher t ON sc.teacher_id = t.teacher_id
                      WHERE tt.timetable_id = ?";
    
    $timetable_stmt = $conn->prepare($timetable_sql);
    $timetable_stmt->execute([$timetable_id]);
    $timetable_info = $timetable_stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$timetable_info) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Timetable not found']);
        return;
    }
    
    // Get students enrolled in this subject class
    $students_sql = "SELECT s.student_id, s.student_code, s.student_full_name, s.student_email,
                            pc.program_class_code, d.department_name,
                            se.enrollment_id, se.enrollment_date, se.status
                     FROM student_enrollment se
                     JOIN student s ON se.student_id = s.student_id
                     JOIN program_class pc ON s.program_class_id = pc.program_class_id
                     JOIN department d ON pc.department_id = d.department_id
                     WHERE se.subject_class_id = ? AND se.status = 'active'
                     ORDER BY s.student_code";
    
    $students_stmt = $conn->prepare($students_sql);
    $students_stmt->execute([$timetable_info['subject_class_id']]);
    $students = $students_stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Convert numeric fields
    foreach ($students as &$student) {
        $student['student_id'] = (int)$student['student_id'];
        $student['enrollment_id'] = (int)$student['enrollment_id'];
    }
    
    echo json_encode([
        'success' => true,
        'timetable_info' => $timetable_info,
        'students' => $students,
        'total_students' => count($students)
    ]);
}

function addStudentToTimetable($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input || !isset($input['timetable_id']) || !isset($input['student_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Missing timetable_id or student_id']);
        return;
    }
    
    $timetable_id = (int)$input['timetable_id'];
    $student_id = (int)$input['student_id'];
    
    // Get subject_class_id from timetable
    $timetable_sql = "SELECT subject_class_id FROM timetable WHERE timetable_id = ?";
    $timetable_stmt = $conn->prepare($timetable_sql);
    $timetable_stmt->execute([$timetable_id]);
    $timetable = $timetable_stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$timetable) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Timetable not found']);
        return;
    }
    
    $subject_class_id = $timetable['subject_class_id'];
    
    // Check if student exists
    $student_sql = "SELECT student_id, student_full_name FROM student WHERE student_id = ?";
    $student_stmt = $conn->prepare($student_sql);
    $student_stmt->execute([$student_id]);
    $student = $student_stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$student) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Student not found']);
        return;
    }
    
    // Check if student is already enrolled
    $check_sql = "SELECT enrollment_id FROM student_enrollment 
                  WHERE student_id = ? AND subject_class_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$student_id, $subject_class_id]);
    
    if ($check_stmt->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Sinh viên đã được đăng ký môn học này']);
        return;
    }
    
    try {
        // Add student to enrollment
        $enroll_sql = "INSERT INTO student_enrollment (student_id, subject_class_id, status) 
                       VALUES (?, ?, 'active')";
        $enroll_stmt = $conn->prepare($enroll_sql);
        $enroll_stmt->execute([$student_id, $subject_class_id]);
        
        echo json_encode([
            'success' => true, 
            'message' => 'Đã thêm sinh viên ' . $student['student_full_name'] . ' vào lớp học'
        ]);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error adding student: ' . $e->getMessage()]);
    }
}

function removeStudentFromTimetable($conn) {
    if (!isset($_GET['enrollment_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Missing enrollment_id']);
        return;
    }
    
    $enrollment_id = (int)$_GET['enrollment_id'];
    
    // Get student info before deletion
    $student_sql = "SELECT s.student_full_name 
                    FROM student_enrollment se
                    JOIN student s ON se.student_id = s.student_id
                    WHERE se.enrollment_id = ?";
    $student_stmt = $conn->prepare($student_sql);
    $student_stmt->execute([$enrollment_id]);
    $student = $student_stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$student) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Enrollment not found']);
        return;
    }
    
    try {
        // Remove student from enrollment
        $delete_sql = "DELETE FROM student_enrollment WHERE enrollment_id = ?";
        $delete_stmt = $conn->prepare($delete_sql);
        $delete_stmt->execute([$enrollment_id]);
        
        echo json_encode([
            'success' => true, 
            'message' => 'Đã xóa sinh viên ' . $student['student_full_name'] . ' khỏi lớp học'
        ]);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error removing student: ' . $e->getMessage()]);
    }
}
?> 