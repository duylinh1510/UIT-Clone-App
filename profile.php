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
    $user_id = isset($_GET['user_id']) ? (int)$_GET['user_id'] : null;
    $student_id = isset($_GET['student_id']) ? (int)$_GET['student_id'] : null;
    
    if (!$user_id && !$student_id) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'user_id or student_id parameter is required']);
        return;
    }
    
    // Build query based on parameter
    if ($user_id) {
        $sql = "SELECT s.student_id, s.student_code, s.full_name, s.birth_date, 
                       s.student_email, s.student_address, s.class_id, s.department_id,
                       c.class_name, d.department_name, u.username
                FROM student s 
                LEFT JOIN class c ON s.class_id = c.class_id 
                LEFT JOIN department d ON s.department_id = d.department_id 
                LEFT JOIN user u ON s.user_id = u.user_id
                WHERE s.user_id = ?";
        $param = $user_id;
    } else {
        $sql = "SELECT s.student_id, s.student_code, s.full_name, s.birth_date, 
                       s.student_email, s.student_address, s.class_id, s.department_id,
                       c.class_name, d.department_name, u.username
                FROM student s 
                LEFT JOIN class c ON s.class_id = c.class_id 
                LEFT JOIN department d ON s.department_id = d.department_id 
                LEFT JOIN user u ON s.user_id = u.user_id
                WHERE s.student_id = ?";
        $param = $student_id;
    }
    
    $stmt = $conn->prepare($sql);
    $stmt->execute([$param]);
    $profile = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$profile) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Student profile not found']);
        return;
    }
    
    // Convert numeric types
    $profile['student_id'] = (int)$profile['student_id'];
    $profile['class_id'] = $profile['class_id'] ? (int)$profile['class_id'] : null;
    $profile['department_id'] = $profile['department_id'] ? (int)$profile['department_id'] : null;
    
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
    
    $user_id = isset($input['user_id']) ? (int)$input['user_id'] : null;
    $student_id = isset($input['student_id']) ? (int)$input['student_id'] : null;
    
    if (!$user_id && !$student_id) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'user_id or student_id is required']);
        return;
    }
    
    // Get student record
    if ($user_id) {
        $check_sql = "SELECT student_id, user_id FROM student WHERE user_id = ?";
        $check_param = $user_id;
    } else {
        $check_sql = "SELECT student_id, user_id FROM student WHERE student_id = ?";
        $check_param = $student_id;
    }
    
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$check_param]);
    $student = $check_stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$student) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Student not found']);
        return;
    }
    
    $target_student_id = $student['student_id'];
    $target_user_id = $student['user_id'];
    
    // Check if email already exists (excluding current student)
    if (!empty($input['student_email'])) {
        $check_email_sql = "SELECT student_id FROM student WHERE student_email = ? AND student_id != ?";
        $check_email_stmt = $conn->prepare($check_email_sql);
        $check_email_stmt->execute([$input['student_email'], $target_student_id]);
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
        
        $allowed_fields = ['full_name', 'birth_date', 'student_email', 'student_address'];
        foreach ($allowed_fields as $field) {
            if (isset($input[$field])) {
                $update_fields[] = "$field = ?";
                $update_params[] = $input[$field];
            }
        }
        
        if (!empty($update_fields)) {
            $update_params[] = $target_student_id;
            $update_sql = "UPDATE student SET " . implode(', ', $update_fields) . " WHERE student_id = ?";
            $update_stmt = $conn->prepare($update_sql);
            $update_stmt->execute($update_params);
        }
        
        // Update password if provided
        if (!empty($input['new_password'])) {
            $hashed_password = password_hash($input['new_password'], PASSWORD_DEFAULT);
            $password_sql = "UPDATE user SET password = ? WHERE user_id = ?";
            $password_stmt = $conn->prepare($password_sql);
            $password_stmt->execute([$hashed_password, $target_user_id]);
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