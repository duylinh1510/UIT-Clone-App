<?php
require "../db_connect.php";
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

$method = $_SERVER['REQUEST_METHOD'];

try {
    switch ($method) {
        case 'GET':
            getSubjectClasses($conn);
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

function getSubjectClasses($conn) {
    $semester = isset($_GET['semester']) ? $_GET['semester'] : '';
    $subject_id = isset($_GET['subject_id']) ? (int)$_GET['subject_id'] : null;
    
    // Build WHERE clause
    $where_conditions = [];
    $params = [];
    
    if (!empty($semester)) {
        $where_conditions[] = "sc.semester = ?";
        $params[] = $semester;
    }
    
    if ($subject_id) {
        $where_conditions[] = "sc.subject_id = ?";
        $params[] = $subject_id;
    }
    
    $where_clause = !empty($where_conditions) ? 'WHERE ' . implode(' AND ', $where_conditions) : '';
    
    // Get subject_classes with subject and teacher info
    $sql = "SELECT sc.subject_class_id, sc.subject_class_code, sc.semester,
                   sc.subject_id, s.subject_code, s.name as subject_name, s.credits,
                   sc.teacher_id, t.teacher_full_name,
                   d.department_name
            FROM subject_class sc 
            LEFT JOIN subject s ON sc.subject_id = s.subject_id
            LEFT JOIN teacher t ON sc.teacher_id = t.teacher_id
            LEFT JOIN department d ON s.department_id = d.department_id
            $where_clause
            ORDER BY sc.semester DESC, s.subject_code";
    
    $stmt = $conn->prepare($sql);
    if (!empty($params)) {
        $stmt->execute($params);
    } else {
        $stmt->execute();
    }
    
    $subject_classes = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Convert numeric fields
    foreach ($subject_classes as &$sc) {
        $sc['subject_class_id'] = (int)$sc['subject_class_id'];
        $sc['subject_id'] = (int)$sc['subject_id'];
        $sc['teacher_id'] = (int)$sc['teacher_id'];
        $sc['credits'] = (int)$sc['credits'];
    }
    
    echo json_encode([
        'success' => true,
        'data' => $subject_classes
    ]);
}
?> 