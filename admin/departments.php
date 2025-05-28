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
            getDepartments($conn);
            break;
        case 'POST':
            createDepartment($conn);
            break;
        case 'PUT':
            updateDepartment($conn);
            break;
        case 'DELETE':
            deleteDepartment($conn);
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

function getDepartments($conn) {
    $search = isset($_GET['search']) ? $_GET['search'] : '';
    
    // Build WHERE clause
    $where_conditions = [];
    $params = [];
    
    if (!empty($search)) {
        $where_conditions[] = "(department_name LIKE ? OR department_code LIKE ?)";
        $params[] = "%$search%";
        $params[] = "%$search%";
    }
    
    $where_clause = !empty($where_conditions) ? 'WHERE ' . implode(' AND ', $where_conditions) : '';
    
    // Get departments with statistics (bao gồm department_code)
    $sql = "SELECT d.department_id, d.department_code, d.department_name,
                   (SELECT COUNT(*) FROM program_class pc WHERE pc.department_id = d.department_id) as class_count,
                   (SELECT COUNT(*) FROM subject sub WHERE sub.department_id = d.department_id) as subject_count
            FROM department d 
            $where_clause
            ORDER BY d.department_name";
    
    $stmt = $conn->prepare($sql);
    if (!empty($params)) {
        $stmt->execute($params);
    } else {
        $stmt->execute();
    }
    
    $departments = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'data' => $departments
    ]);
}

function createDepartment($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid JSON input']);
        return;
    }
    
    // Validate required fields
    $required = ['department_name'];
    foreach ($required as $field) {
        if (empty($input[$field])) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => "Missing required field: $field"]);
            return;
        }
    }
    
    // Check if department name already exists
    $check_name_sql = "SELECT department_id FROM department WHERE department_name = ?";
    $check_name_stmt = $conn->prepare($check_name_sql);
    $check_name_stmt->execute([$input['department_name']]);
    if ($check_name_stmt->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Department name already exists']);
        return;
    }
    
    // Check if department code already exists (if provided)
    if (!empty($input['department_code'])) {
        $check_code_sql = "SELECT department_id FROM department WHERE department_code = ?";
        $check_code_stmt = $conn->prepare($check_code_sql);
        $check_code_stmt->execute([$input['department_code']]);
        if ($check_code_stmt->fetch()) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => 'Department code already exists']);
            return;
        }
    }
    
    try {
        // Create department record
        $department_sql = "INSERT INTO department (department_name, department_code) VALUES (?, ?)";
        $department_stmt = $conn->prepare($department_sql);
        $department_stmt->execute([
            $input['department_name'],
            $input['department_code'] ?? null
        ]);
        
        echo json_encode(['success' => true, 'message' => 'Department created successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error creating department: ' . $e->getMessage()]);
    }
}

function updateDepartment($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input || !isset($input['department_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid input or missing department_id']);
        return;
    }
    
    // Check if department exists
    $check_sql = "SELECT department_id FROM department WHERE department_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$input['department_id']]);
    
    if (!$check_stmt->fetch()) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Department not found']);
        return;
    }
    
    // Check if department name already exists (excluding current department)
    if (!empty($input['department_name'])) {
        $check_name_sql = "SELECT department_id FROM department WHERE department_name = ? AND department_id != ?";
        $check_name_stmt = $conn->prepare($check_name_sql);
        $check_name_stmt->execute([$input['department_name'], $input['department_id']]);
        if ($check_name_stmt->fetch()) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => 'Department name already exists']);
            return;
        }
    }
    
    // Check if department code already exists (excluding current department)
    if (!empty($input['department_code'])) {
        $check_code_sql = "SELECT department_id FROM department WHERE department_code = ? AND department_id != ?";
        $check_code_stmt = $conn->prepare($check_code_sql);
        $check_code_stmt->execute([$input['department_code'], $input['department_id']]);
        if ($check_code_stmt->fetch()) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => 'Department code already exists']);
            return;
        }
    }
    
    try {
        // Update department record
        $department_sql = "UPDATE department SET 
                          department_name = COALESCE(?, department_name),
                          department_code = COALESCE(?, department_code)
                          WHERE department_id = ?";
        
        $department_stmt = $conn->prepare($department_sql);
        $department_stmt->execute([
            $input['department_name'] ?? null,
            $input['department_code'] ?? null,
            $input['department_id']
        ]);
        
        echo json_encode(['success' => true, 'message' => 'Department updated successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error updating department: ' . $e->getMessage()]);
    }
}

function deleteDepartment($conn) {
    if (!isset($_GET['department_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Missing department_id']);
        return;
    }
    
    $department_id = (int)$_GET['department_id'];
    
    // Check if department exists
    $check_sql = "SELECT department_id FROM department WHERE department_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$department_id]);
    
    if (!$check_stmt->fetch()) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Department not found']);
        return;
    }
    
    // Check if department is being used
    $usage_messages = [];
    
    // Check program classes
    $class_sql = "SELECT COUNT(*) as count FROM program_class WHERE department_id = ?";
    $class_stmt = $conn->prepare($class_sql);
    $class_stmt->execute([$department_id]);
    $class_count = $class_stmt->fetch(PDO::FETCH_ASSOC)['count'];
    
    if ($class_count > 0) {
        $usage_messages[] = "$class_count lớp học";
    }
    
    // Check teachers
    $teacher_sql = "SELECT COUNT(*) as count FROM teacher WHERE department_id = ?";
    $teacher_stmt = $conn->prepare($teacher_sql);
    $teacher_stmt->execute([$department_id]);
    $teacher_count = $teacher_stmt->fetch(PDO::FETCH_ASSOC)['count'];
    
    if ($teacher_count > 0) {
        $usage_messages[] = "$teacher_count giáo viên";
    }
    
    // Check subjects
    $subject_sql = "SELECT COUNT(*) as count FROM subject WHERE department_id = ?";
    $subject_stmt = $conn->prepare($subject_sql);
    $subject_stmt->execute([$department_id]);
    $subject_count = $subject_stmt->fetch(PDO::FETCH_ASSOC)['count'];
    
    if ($subject_count > 0) {
        $usage_messages[] = "$subject_count môn học";
    }
    
    // Check students through program_class
    $student_sql = "SELECT COUNT(DISTINCT s.student_id) as count 
                    FROM student s 
                    JOIN program_class pc ON s.program_class_id = pc.program_class_id 
                    WHERE pc.department_id = ?";
    $student_stmt = $conn->prepare($student_sql);
    $student_stmt->execute([$department_id]);
    $student_count = $student_stmt->fetch(PDO::FETCH_ASSOC)['count'];
    
    if ($student_count > 0) {
        $usage_messages[] = "$student_count sinh viên";
    }
    
    if (!empty($usage_messages)) {
        http_response_code(400);
        echo json_encode([
            'success' => false, 
            'message' => 'Không thể xóa khoa. Khoa này đang được sử dụng bởi: ' . implode(', ', $usage_messages)
        ]);
        return;
    }
    
    try {
        // Delete department
        $delete_sql = "DELETE FROM department WHERE department_id = ?";
        $delete_stmt = $conn->prepare($delete_sql);
        $delete_stmt->execute([$department_id]);
        
        echo json_encode(['success' => true, 'message' => 'Xóa khoa thành công']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Lỗi khi xóa khoa: ' . $e->getMessage()]);
    }
}
?> 