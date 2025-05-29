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
        case 'POST':
            createSubjectClass($conn);
            break;
        case 'PUT':
            updateSubjectClass($conn);
            break;
        case 'DELETE':
            deleteSubjectClass($conn);
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

function createSubjectClass($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!isset($input['subject_class_code']) || !isset($input['semester']) || !isset($input['subject_id'])) {
        echo json_encode(['success' => false, 'message' => 'Missing required fields']);
        return;
    }
    
    $sql = "INSERT INTO subject_class (subject_class_code, semester, subject_id, teacher_id) VALUES (?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    
    $result = $stmt->execute([
        $input['subject_class_code'],
        $input['semester'], 
        $input['subject_id'],
        $input['teacher_id'] ?? null
    ]);
    
    if ($result) {
        echo json_encode(['success' => true, 'message' => 'Subject class created successfully']);
    } else {
        echo json_encode(['success' => false, 'message' => 'Failed to create subject class']);
    }
}

function updateSubjectClass($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!isset($input['subject_class_id'])) {
        echo json_encode(['success' => false, 'message' => 'Missing subject_class_id']);
        return;
    }
    
    // Check if this is a full update (has subject_class_code and semester) or just teacher update
    if (isset($input['subject_class_code']) && isset($input['semester'])) {
        // Update toàn bộ thông tin lớp học
        $sql = "UPDATE subject_class SET 
                subject_class_code = ?, 
                semester = ?, 
                teacher_id = ? 
                WHERE subject_class_id = ?";
        
        $stmt = $conn->prepare($sql);
        $result = $stmt->execute([
            $input['subject_class_code'],
            $input['semester'],
            $input['teacher_id'] ?? null,
            $input['subject_class_id']
        ]);
        
        if ($result) {
            echo json_encode(['success' => true, 'message' => 'Subject class updated successfully']);
        } else {
            echo json_encode(['success' => false, 'message' => 'Failed to update subject class']);
        }
        
    } else if (isset($input['teacher_id'])) {
        // Chỉ update teacher
        $sql = "UPDATE subject_class SET teacher_id = ? WHERE subject_class_id = ?";
        
        $stmt = $conn->prepare($sql);
        $result = $stmt->execute([
            $input['teacher_id'],
            $input['subject_class_id']
        ]);
        
        if ($result) {
            echo json_encode(['success' => true, 'message' => 'Teacher updated successfully']);
        } else {
            echo json_encode(['success' => false, 'message' => 'Failed to update teacher']);
        }
    } else {
        echo json_encode(['success' => false, 'message' => 'No valid fields to update']);
    }
}

function deleteSubjectClass($conn) {
    $subject_class_id = isset($_GET['subject_class_id']) ? (int)$_GET['subject_class_id'] : null;
    
    if (!$subject_class_id) {
        echo json_encode(['success' => false, 'message' => 'Missing subject_class_id']);
        return;
    }
    
    $sql = "DELETE FROM subject_class WHERE subject_class_id = ?";
    $stmt = $conn->prepare($sql);
    $result = $stmt->execute([$subject_class_id]);
    
    if ($result) {
        echo json_encode(['success' => true, 'message' => 'Subject class deleted successfully']);
    } else {
        echo json_encode(['success' => false, 'message' => 'Failed to delete subject class']);
    }
}
?> 