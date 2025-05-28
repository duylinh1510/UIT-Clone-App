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
            getStudents($conn);
            break;
        case 'POST':
            createStudent($conn);
            break;
        case 'PUT':
            updateStudent($conn);
            break;
        case 'DELETE':
            deleteStudent($conn);
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

function getStudents($conn) {
    $page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
    $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 10;
    $search = isset($_GET['search']) ? $_GET['search'] : '';
    $department_id = isset($_GET['department_id']) ? (int)$_GET['department_id'] : null;
    $class_id = isset($_GET['class_id']) ? (int)$_GET['class_id'] : null;
    
    $offset = ($page - 1) * $limit;
    
    // Build WHERE clause
    $where_conditions = [];
    $params = [];
    
    if (!empty($search)) {
        $where_conditions[] = "(s.student_code LIKE ? OR s.student_full_name LIKE ? OR s.student_email LIKE ?)";
        $params[] = "%$search%";
        $params[] = "%$search%";
        $params[] = "%$search%";
    }
    
    if ($department_id) {
        $where_conditions[] = "pc.department_id = ?";
        $params[] = $department_id;
    }
    
    if ($class_id) {
        $where_conditions[] = "s.program_class_id = ?";
        $params[] = $class_id;
    }
    
    $where_clause = !empty($where_conditions) ? 'WHERE ' . implode(' AND ', $where_conditions) : '';
    
    // Count total records
    $count_sql = "SELECT COUNT(*) as total 
                  FROM student s 
                  LEFT JOIN program_class pc ON s.program_class_id = pc.program_class_id 
                  LEFT JOIN department d ON pc.department_id = d.department_id 
                  $where_clause";
    
    $count_stmt = $conn->prepare($count_sql);
    if (!empty($params)) {
        $count_stmt->execute($params);
    } else {
        $count_stmt->execute();
    }
    $total = $count_stmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    // Get students with pagination
    $sql = "SELECT s.student_id, s.student_code, s.student_full_name, s.date_of_birth, 
                   s.student_email, s.student_address, s.program_class_id,
                   pc.program_class_code, d.department_name, d.department_id
            FROM student s 
            LEFT JOIN program_class pc ON s.program_class_id = pc.program_class_id 
            LEFT JOIN department d ON pc.department_id = d.department_id 
            $where_clause
            ORDER BY s.student_code 
            LIMIT $limit OFFSET $offset";
    
    $stmt = $conn->prepare($sql);
    if (!empty($params)) {
        $stmt->execute($params);
    } else {
        $stmt->execute();
    }
    
    $students = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    $totalPages = ceil($total / $limit);
    
    echo json_encode([
        'success' => true,
        'data' => $students,
        'pagination' => [
            'total' => (int)$total,
            'page' => $page,
            'limit' => $limit,
            'totalPages' => $totalPages
        ]
    ]);
}

function createStudent($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid JSON input']);
        return;
    }
    
    // Validate required fields
    $required = ['student_code', 'student_full_name', 'student_email', 'program_class_id', 'username', 'password'];
    foreach ($required as $field) {
        if (empty($input[$field])) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => "Missing required field: $field"]);
            return;
        }
    }
    
    // Check if student code already exists
    $check_code_sql = "SELECT student_id FROM student WHERE student_code = ?";
    $check_code_stmt = $conn->prepare($check_code_sql);
    $check_code_stmt->execute([$input['student_code']]);
    if ($check_code_stmt->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Student code already exists']);
        return;
    }
    
    // Check if email already exists
    $check_email_sql = "SELECT student_id FROM student WHERE student_email = ?";
    $check_email_stmt = $conn->prepare($check_email_sql);
    $check_email_stmt->execute([$input['student_email']]);
    if ($check_email_stmt->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Email already exists']);
        return;
    }
    
    // Check if username already exists
    $check_username_sql = "SELECT user_id FROM user WHERE username = ?";
    $check_username_stmt = $conn->prepare($check_username_sql);
    $check_username_stmt->execute([$input['username']]);
    if ($check_username_stmt->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Username already exists']);
        return;
    }
    
    // Verify program class exists
    $verify_class = $conn->prepare("SELECT program_class_id FROM program_class WHERE program_class_id = ?");
    $verify_class->execute([$input['program_class_id']]);
    if (!$verify_class->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Program class not found']);
        return;
    }
    
    $conn->beginTransaction();
    
    try {
        // Create user account
        $user_sql = "INSERT INTO user (username, password, role) VALUES (?, ?, 'student')";
        $user_stmt = $conn->prepare($user_sql);
        $hashed_password = password_hash($input['password'], PASSWORD_DEFAULT);
        $user_stmt->execute([$input['username'], $hashed_password]);
        $user_id = $conn->lastInsertId();
        
        // Create student record
        $student_sql = "INSERT INTO student (user_id, student_code, student_full_name, date_of_birth, student_email, student_address, program_class_id) 
                        VALUES (?, ?, ?, ?, ?, ?, ?)";
        $student_stmt = $conn->prepare($student_sql);
        $student_stmt->execute([
            $user_id,
            $input['student_code'],
            $input['student_full_name'],
            $input['date_of_birth'] ?? null,
            $input['student_email'],
            $input['student_address'] ?? null,
            $input['program_class_id']
        ]);
        
        $conn->commit();
        echo json_encode(['success' => true, 'message' => 'Student created successfully']);
        
    } catch (Exception $e) {
        $conn->rollBack();
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error creating student: ' . $e->getMessage()]);
    }
}

function updateStudent($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input || !isset($input['student_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid input or missing student_id']);
        return;
    }
    
    // Check if student exists
    $check_sql = "SELECT user_id FROM student WHERE student_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$input['student_id']]);
    $student = $check_stmt->fetch();
    
    if (!$student) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Student not found']);
        return;
    }
    
    // Check if student code already exists (excluding current student)
    if (!empty($input['student_code'])) {
        $check_code_sql = "SELECT student_id FROM student WHERE student_code = ? AND student_id != ?";
        $check_code_stmt = $conn->prepare($check_code_sql);
        $check_code_stmt->execute([$input['student_code'], $input['student_id']]);
        if ($check_code_stmt->fetch()) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => 'Student code already exists']);
            return;
        }
    }
    
    // Check if email already exists (excluding current student)
    if (!empty($input['student_email'])) {
        $check_email_sql = "SELECT student_id FROM student WHERE student_email = ? AND student_id != ?";
        $check_email_stmt = $conn->prepare($check_email_sql);
        $check_email_stmt->execute([$input['student_email'], $input['student_id']]);
        if ($check_email_stmt->fetch()) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => 'Email already exists']);
            return;
        }
    }
    
    // Verify program class exists (if provided)
    if (isset($input['program_class_id'])) {
        $verify_class = $conn->prepare("SELECT program_class_id FROM program_class WHERE program_class_id = ?");
        $verify_class->execute([$input['program_class_id']]);
        if (!$verify_class->fetch()) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => 'Program class not found']);
            return;
        }
    }
    
    try {
        // Update student record
        $student_sql = "UPDATE student SET 
                        student_code = COALESCE(?, student_code),
                        student_full_name = COALESCE(?, student_full_name),
                        date_of_birth = COALESCE(?, date_of_birth),
                        student_email = COALESCE(?, student_email),
                        student_address = COALESCE(?, student_address),
                        program_class_id = COALESCE(?, program_class_id)
                        WHERE student_id = ?";
        
        $student_stmt = $conn->prepare($student_sql);
        $student_stmt->execute([
            $input['student_code'] ?? null,
            $input['student_full_name'] ?? null,
            $input['date_of_birth'] ?? null,
            $input['student_email'] ?? null,
            $input['student_address'] ?? null,
            $input['program_class_id'] ?? null,
            $input['student_id']
        ]);
        
        echo json_encode(['success' => true, 'message' => 'Student updated successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error updating student: ' . $e->getMessage()]);
    }
}

function deleteStudent($conn) {
    if (!isset($_GET['student_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Missing student_id']);
        return;
    }
    
    $student_id = (int)$_GET['student_id'];
    
    // Check if student exists and get user_id
    $check_sql = "SELECT user_id FROM student WHERE student_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$student_id]);
    $student = $check_stmt->fetch();
    
    if (!$student) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Student not found']);
        return;
    }
    
    // Check if student has grades
    $grade_check_sql = "SELECT COUNT(*) as count FROM grade WHERE student_id = ?";
    $grade_check_stmt = $conn->prepare($grade_check_sql);
    $grade_check_stmt->execute([$student_id]);
    $grade_count = $grade_check_stmt->fetch(PDO::FETCH_ASSOC)['count'];
    
    if ($grade_count > 0) {
        http_response_code(400);
        echo json_encode([
            'success' => false, 
            'message' => "Cannot delete student. Student has $grade_count grade record(s)"
        ]);
        return;
    }
    
    $conn->beginTransaction();
    
    try {
        // Delete student
        $delete_student_sql = "DELETE FROM student WHERE student_id = ?";
        $delete_student_stmt = $conn->prepare($delete_student_sql);
        $delete_student_stmt->execute([$student_id]);
        
        // Delete user account
        $delete_user_sql = "DELETE FROM user WHERE user_id = ?";
        $delete_user_stmt = $conn->prepare($delete_user_sql);
        $delete_user_stmt->execute([$student['user_id']]);
        
        $conn->commit();
        echo json_encode(['success' => true, 'message' => 'Student deleted successfully']);
        
    } catch (Exception $e) {
        $conn->rollBack();
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error deleting student: ' . $e->getMessage()]);
    }
}
?> 