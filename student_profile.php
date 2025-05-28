<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, PUT, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

require_once 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];

try {
    switch ($method) {
        case 'GET':
            getProfile($conn);
            break;
        case 'PUT':
            updateProfile($conn);
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

function getProfile($conn) {
    $student_id = isset($_GET['student_id']) ? (int)$_GET['student_id'] : null;
    
    if (!$student_id) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'student_id parameter is required']);
        return;
    }
    
    // Query với cấu trúc database thực tế
    $sql = "SELECT 
                s.student_id,
                s.student_code,
                s.student_full_name AS full_name,
                s.date_of_birth AS birth_date,
                s.student_email,
                s.student_address,
                pc.program_class_code AS class_name,
                d.department_name AS department
            FROM student s
            LEFT JOIN program_class pc ON s.program_class_id = pc.program_class_id
            LEFT JOIN department d ON pc.department_id = d.department_id
            WHERE s.student_id = ?";
    
    $stmt = $conn->prepare($sql);
    $stmt->execute([$student_id]);
    $profile = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$profile) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Student profile not found']);
        return;
    }
    
    // Convert numeric types và format date
    $profile['student_id'] = (int)$profile['student_id'];
    
    // Format birth date nếu có
    if ($profile['birth_date']) {
        $date = new DateTime($profile['birth_date']);
        $profile['birth_date'] = $date->format('d/m/Y');
    }
    
    echo json_encode([
        'success' => true,
        'data' => $profile
    ]);
}

function updateProfile($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid JSON input']);
        return;
    }
    
    $student_id = isset($input['student_id']) ? (int)$input['student_id'] : null;
    
    if (!$student_id) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'student_id is required']);
        return;
    }
    
    // Check if student exists
    $check_sql = "SELECT student_id FROM student WHERE student_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$student_id]);
    $student = $check_stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$student) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Student not found']);
        return;
    }
    
    // Check if email already exists (excluding current student)
    if (!empty($input['student_email'])) {
        $check_email_sql = "SELECT student_id FROM student WHERE student_email = ? AND student_id != ?";
        $check_email_stmt = $conn->prepare($check_email_sql);
        $check_email_stmt->execute([$input['student_email'], $student_id]);
        if ($check_email_stmt->fetch()) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => 'Email already exists']);
            return;
        }
    }
    
    $conn->beginTransaction();
    
    try {
        // Update student profile
        $update_fields = [];
        $update_params = [];
        
        $allowed_fields = ['student_full_name', 'date_of_birth', 'student_email', 'student_address'];
        foreach ($allowed_fields as $field) {
            if (isset($input[$field])) {
                $update_fields[] = "$field = ?";
                $update_params[] = $input[$field];
            }
        }
        
        if (!empty($update_fields)) {
            $update_params[] = $student_id;
            $update_sql = "UPDATE student SET " . implode(', ', $update_fields) . " WHERE student_id = ?";
            $update_stmt = $conn->prepare($update_sql);
            $update_stmt->execute($update_params);
        }
        
        $conn->commit();
        echo json_encode(['success' => true, 'message' => 'Profile updated successfully']);
        
    } catch (Exception $e) {
        $conn->rollBack();
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error updating profile: ' . $e->getMessage()]);
    }
}
?> 