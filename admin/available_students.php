<?php
require "../db_connect.php";
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

try {
    getAvailableStudents($conn);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Server error: ' . $e->getMessage()]);
}

function getAvailableStudents($conn) {
    if (!isset($_GET['subject_class_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Missing subject_class_id']);
        return;
    }
    
    $subject_class_id = (int)$_GET['subject_class_id'];
    $search = isset($_GET['search']) ? $_GET['search'] : '';
    
    // Build WHERE clause for search
    $search_condition = '';
    $params = [$subject_class_id];
    
    if (!empty($search)) {
        $search_condition = "AND (s.student_code LIKE ? OR s.student_full_name LIKE ? OR pc.program_class_code LIKE ?)";
        $params[] = "%$search%";
        $params[] = "%$search%";
        $params[] = "%$search%";
    }
    
    // Get students who are NOT enrolled in this subject class
    $sql = "SELECT s.student_id, s.student_code, s.student_full_name, s.student_email,
                   pc.program_class_code, d.department_name
            FROM student s
            JOIN program_class pc ON s.program_class_id = pc.program_class_id
            JOIN department d ON pc.department_id = d.department_id
            WHERE s.student_id NOT IN (
                SELECT se.student_id 
                FROM student_enrollment se 
                WHERE se.subject_class_id = ? AND se.status = 'active'
            )
            $search_condition
            ORDER BY s.student_code
            LIMIT 50";
    
    $stmt = $conn->prepare($sql);
    $stmt->execute($params);
    $students = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Convert numeric fields
    foreach ($students as &$student) {
        $student['student_id'] = (int)$student['student_id'];
    }
    
    echo json_encode([
        'success' => true,
        'data' => $students,
        'total' => count($students)
    ]);
}
?> 