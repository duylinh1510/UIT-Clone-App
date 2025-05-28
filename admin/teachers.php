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
            getTeachers($conn);
            break;
        case 'POST':
            createTeacher($conn);
            break;
        case 'PUT':
            updateTeacher($conn);
            break;
        case 'DELETE':
            deleteTeacher($conn);
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

function getTeachers($conn) {
    $page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
    $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 10;
    $search = isset($_GET['search']) ? $_GET['search'] : '';
    $department_id = isset($_GET['department_id']) ? (int)$_GET['department_id'] : null;
    
    $offset = ($page - 1) * $limit;
    
    // Build WHERE clause
    $where_conditions = [];
    $params = [];
    
    if (!empty($search)) {
        $where_conditions[] = "(t.teacher_full_name LIKE ? OR t.teacher_email LIKE ? OR u.username LIKE ?)";
        $params[] = "%$search%";
        $params[] = "%$search%";
        $params[] = "%$search%";
    }
    
    if ($department_id) {
        $where_conditions[] = "t.department_id = ?";
        $params[] = $department_id;
    }
    
    $where_clause = !empty($where_conditions) ? 'WHERE ' . implode(' AND ', $where_conditions) : '';
    
    // Count total records
    $count_sql = "SELECT COUNT(*) as total 
                  FROM teacher t 
                  JOIN user u ON t.user_id = u.user_id 
                  LEFT JOIN department d ON t.department_id = d.department_id
                  $where_clause";
    
    $count_stmt = $conn->prepare($count_sql);
    if (!empty($params)) {
        $count_stmt->execute($params);
    } else {
        $count_stmt->execute();
    }
    $total = $count_stmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    // Get teachers with pagination (bao gồm department info)
    $sql = "SELECT t.teacher_id, t.user_id, t.teacher_full_name, t.date_of_birth, 
                   t.teacher_email, t.department_id, d.department_name, u.username
            FROM teacher t 
            JOIN user u ON t.user_id = u.user_id 
            LEFT JOIN department d ON t.department_id = d.department_id
            $where_clause
            ORDER BY t.teacher_full_name 
            LIMIT $limit OFFSET $offset";
    
    $stmt = $conn->prepare($sql);
    if (!empty($params)) {
        $stmt->execute($params);
    } else {
        $stmt->execute();
    }
    
    $teachers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    $totalPages = ceil($total / $limit);
    
    echo json_encode([
        'success' => true,
        'data' => $teachers,
        'pagination' => [
            'total' => (int)$total,
            'page' => $page,
            'limit' => $limit,
            'totalPages' => $totalPages
        ]
    ]);
}

function createTeacher($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid JSON input']);
        return;
    }
    
    // Validate required fields
    $required = ['teacher_full_name', 'teacher_email', 'username', 'password'];
    foreach ($required as $field) {
        if (empty($input[$field])) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => "Missing required field: $field"]);
            return;
        }
    }
    
    // Check if username already exists
    $check_sql = "SELECT user_id FROM user WHERE username = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$input['username']]);
    if ($check_stmt->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Username already exists']);
        return;
    }
    
    // Check if email already exists
    $check_email_sql = "SELECT teacher_id FROM teacher WHERE teacher_email = ?";
    $check_email_stmt = $conn->prepare($check_email_sql);
    $check_email_stmt->execute([$input['teacher_email']]);
    if ($check_email_stmt->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Email already exists']);
        return;
    }
    
    $conn->beginTransaction();
    
    try {
        // Create user account
        $user_sql = "INSERT INTO user (username, password, role) VALUES (?, ?, 'teacher')";
        $user_stmt = $conn->prepare($user_sql);
        $hashed_password = password_hash($input['password'], PASSWORD_DEFAULT);
        $user_stmt->execute([$input['username'], $hashed_password]);
        $user_id = $conn->lastInsertId();
        
        // Create teacher record
        $teacher_sql = "INSERT INTO teacher (user_id, teacher_full_name, date_of_birth, teacher_email, department_id) 
                        VALUES (?, ?, ?, ?, ?)";
        $teacher_stmt = $conn->prepare($teacher_sql);
        $teacher_stmt->execute([
            $user_id,
            $input['teacher_full_name'],
            $input['date_of_birth'] ?? null,
            $input['teacher_email'],
            $input['department_id'] ?? null
        ]);
        
        $conn->commit();
        echo json_encode(['success' => true, 'message' => 'Teacher created successfully']);
        
    } catch (Exception $e) {
        $conn->rollBack();
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error creating teacher: ' . $e->getMessage()]);
    }
}

function updateTeacher($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input || !isset($input['teacher_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid input or missing teacher_id']);
        return;
    }
    
    // Check if teacher exists
    $check_sql = "SELECT user_id FROM teacher WHERE teacher_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$input['teacher_id']]);
    $teacher = $check_stmt->fetch();
    
    if (!$teacher) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Teacher not found']);
        return;
    }
    
    // Check if email already exists (excluding current teacher)
    if (!empty($input['teacher_email'])) {
        $check_email_sql = "SELECT teacher_id FROM teacher WHERE teacher_email = ? AND teacher_id != ?";
        $check_email_stmt = $conn->prepare($check_email_sql);
        $check_email_stmt->execute([$input['teacher_email'], $input['teacher_id']]);
        if ($check_email_stmt->fetch()) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => 'Email already exists']);
            return;
        }
    }
    
    try {
        // Update teacher record
        $teacher_sql = "UPDATE teacher SET 
                        teacher_full_name = COALESCE(?, teacher_full_name),
                        date_of_birth = COALESCE(?, date_of_birth),
                        teacher_email = COALESCE(?, teacher_email),
                        department_id = COALESCE(?, department_id)
                        WHERE teacher_id = ?";
        
        $teacher_stmt = $conn->prepare($teacher_sql);
        $teacher_stmt->execute([
            $input['teacher_full_name'] ?? null,
            $input['date_of_birth'] ?? null,
            $input['teacher_email'] ?? null,
            $input['department_id'] ?? null,
            $input['teacher_id']
        ]);
        
        echo json_encode(['success' => true, 'message' => 'Teacher updated successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error updating teacher: ' . $e->getMessage()]);
    }
}

function deleteTeacher($conn) {
    if (!isset($_GET['teacher_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Missing teacher_id']);
        return;
    }
    
    $teacher_id = (int)$_GET['teacher_id'];
    
    // Check if teacher exists and get user_id
    $check_sql = "SELECT user_id FROM teacher WHERE teacher_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$teacher_id]);
    $teacher = $check_stmt->fetch();
    
    if (!$teacher) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Teacher not found']);
        return;
    }
    
    $conn->beginTransaction();
    
    try {
        // Delete teacher (will cascade to user due to foreign key)
        $delete_teacher_sql = "DELETE FROM teacher WHERE teacher_id = ?";
        $delete_teacher_stmt = $conn->prepare($delete_teacher_sql);
        $delete_teacher_stmt->execute([$teacher_id]);
        
        // Delete user account
        $delete_user_sql = "DELETE FROM user WHERE user_id = ?";
        $delete_user_stmt = $conn->prepare($delete_user_sql);
        $delete_user_stmt->execute([$teacher['user_id']]);
        
        $conn->commit();
        echo json_encode(['success' => true, 'message' => 'Teacher deleted successfully']);
        
    } catch (Exception $e) {
        $conn->rollBack();
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error deleting teacher: ' . $e->getMessage()]);
    }
}
?> 