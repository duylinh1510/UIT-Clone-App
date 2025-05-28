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
            getSubjects($conn);
            break;
        case 'POST':
            createSubject($conn);
            break;
        case 'PUT':
            updateSubject($conn);
            break;
        case 'DELETE':
            deleteSubject($conn);
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

function getSubjects($conn) {
    $search = isset($_GET['search']) ? $_GET['search'] : '';
    $department_id = isset($_GET['department_id']) ? (int)$_GET['department_id'] : null;
    
    // Build WHERE clause
    $where_conditions = [];
    $params = [];
    
    if (!empty($search)) {
        $where_conditions[] = "(s.subject_code LIKE ? OR s.name LIKE ?)";
        $params[] = "%$search%";
        $params[] = "%$search%";
    }
    
    if ($department_id) {
        $where_conditions[] = "s.department_id = ?";
        $params[] = $department_id;
    }
    
    $where_clause = !empty($where_conditions) ? 'WHERE ' . implode(' AND ', $where_conditions) : '';
    
    // Get subjects
    $sql = "SELECT s.subject_id, s.subject_code, s.name, s.credits, 
                   s.department_id, d.department_name
            FROM subject s 
            LEFT JOIN department d ON s.department_id = d.department_id 
            $where_clause
            ORDER BY s.subject_code";
    
    $stmt = $conn->prepare($sql);
    if (!empty($params)) {
        $stmt->execute($params);
    } else {
        $stmt->execute();
    }
    
    $subjects = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'data' => $subjects
    ]);
}

function createSubject($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid JSON input']);
        return;
    }
    
    // Validate required fields
    $required = ['subject_code', 'name', 'credits', 'department_id'];
    foreach ($required as $field) {
        if (!isset($input[$field]) || $input[$field] === '') {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => "Missing required field: $field"]);
            return;
        }
    }
    
    // Validate credits range
    if ($input['credits'] < 1 || $input['credits'] > 10) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Credits must be between 1 and 10']);
        return;
    }
    
    // Check if subject code already exists
    $check_sql = "SELECT subject_id FROM subject WHERE subject_code = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$input['subject_code']]);
    if ($check_stmt->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Subject code already exists']);
        return;
    }
    
    // Check if department exists
    $dept_sql = "SELECT department_id FROM department WHERE department_id = ?";
    $dept_stmt = $conn->prepare($dept_sql);
    $dept_stmt->execute([$input['department_id']]);
    if (!$dept_stmt->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Department not found']);
        return;
    }
    
    try {
        // Create subject record
        $subject_sql = "INSERT INTO subject (subject_code, name, credits, department_id) 
                        VALUES (?, ?, ?, ?)";
        $subject_stmt = $conn->prepare($subject_sql);
        $subject_stmt->execute([
            $input['subject_code'],
            $input['name'],
            $input['credits'],
            $input['department_id']
        ]);
        
        echo json_encode(['success' => true, 'message' => 'Subject created successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error creating subject: ' . $e->getMessage()]);
    }
}

function updateSubject($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input || !isset($input['subject_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid input or missing subject_id']);
        return;
    }
    
    // Check if subject exists
    $check_sql = "SELECT subject_id FROM subject WHERE subject_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$input['subject_id']]);
    
    if (!$check_stmt->fetch()) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Subject not found']);
        return;
    }
    
    // Validate credits if provided
    if (isset($input['credits']) && ($input['credits'] < 1 || $input['credits'] > 10)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Credits must be between 1 and 10']);
        return;
    }
    
    // Check if subject code already exists (excluding current subject)
    if (!empty($input['subject_code'])) {
        $check_code_sql = "SELECT subject_id FROM subject WHERE subject_code = ? AND subject_id != ?";
        $check_code_stmt = $conn->prepare($check_code_sql);
        $check_code_stmt->execute([$input['subject_code'], $input['subject_id']]);
        if ($check_code_stmt->fetch()) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => 'Subject code already exists']);
            return;
        }
    }
    
    // Check if department exists (if provided)
    if (isset($input['department_id'])) {
        $dept_sql = "SELECT department_id FROM department WHERE department_id = ?";
        $dept_stmt = $conn->prepare($dept_sql);
        $dept_stmt->execute([$input['department_id']]);
        if (!$dept_stmt->fetch()) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => 'Department not found']);
            return;
        }
    }
    
    try {
        // Update subject record
        $subject_sql = "UPDATE subject SET 
                        subject_code = COALESCE(?, subject_code),
                        name = COALESCE(?, name),
                        credits = COALESCE(?, credits),
                        department_id = COALESCE(?, department_id)
                        WHERE subject_id = ?";
        
        $subject_stmt = $conn->prepare($subject_sql);
        $subject_stmt->execute([
            $input['subject_code'] ?? null,
            $input['name'] ?? null,
            $input['credits'] ?? null,
            $input['department_id'] ?? null,
            $input['subject_id']
        ]);
        
        echo json_encode(['success' => true, 'message' => 'Subject updated successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error updating subject: ' . $e->getMessage()]);
    }
}

function deleteSubject($conn) {
    if (!isset($_GET['subject_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Missing subject_id']);
        return;
    }
    
    $subject_id = (int)$_GET['subject_id'];
    
    // Check if subject exists
    $check_sql = "SELECT subject_id FROM subject WHERE subject_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$subject_id]);
    
    if (!$check_stmt->fetch()) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Subject not found']);
        return;
    }
    
    // Check if subject is being used in subject_class
    $usage_sql = "SELECT COUNT(*) as count FROM subject_class WHERE subject_id = ?";
    $usage_stmt = $conn->prepare($usage_sql);
    $usage_stmt->execute([$subject_id]);
    $usage_count = $usage_stmt->fetch(PDO::FETCH_ASSOC)['count'];
    
    if ($usage_count > 0) {
        http_response_code(400);
        echo json_encode([
            'success' => false, 
            'message' => "Cannot delete subject. It is being used in $usage_count subject class(es)"
        ]);
        return;
    }
    
    try {
        // Delete subject
        $delete_sql = "DELETE FROM subject WHERE subject_id = ?";
        $delete_stmt = $conn->prepare($delete_sql);
        $delete_stmt->execute([$subject_id]);
        
        echo json_encode(['success' => true, 'message' => 'Subject deleted successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error deleting subject: ' . $e->getMessage()]);
    }
}
?> 