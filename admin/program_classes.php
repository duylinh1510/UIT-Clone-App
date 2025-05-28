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
            getProgramClasses($conn);
            break;
        case 'POST':
            createProgramClass($conn);
            break;
        case 'PUT':
            updateProgramClass($conn);
            break;
        case 'DELETE':
            deleteProgramClass($conn);
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

function getProgramClasses($conn) {
    $department_id = isset($_GET['department_id']) ? (int)$_GET['department_id'] : null;
    
    // Build WHERE clause
    $where_conditions = [];
    $params = [];
    
    if ($department_id) {
        $where_conditions[] = "pc.department_id = ?";
        $params[] = $department_id;
    }
    
    $where_clause = !empty($where_conditions) ? 'WHERE ' . implode(' AND ', $where_conditions) : '';
    
    // Get program classes with student count
    $sql = "SELECT pc.program_class_id, pc.program_class_code, pc.year, pc.teacher_id,
                   d.department_name, t.teacher_full_name as teacher_name,
                   COUNT(s.student_id) as student_count
            FROM program_class pc 
            LEFT JOIN department d ON pc.department_id = d.department_id 
            LEFT JOIN teacher t ON pc.teacher_id = t.teacher_id 
            LEFT JOIN student s ON pc.program_class_id = s.program_class_id
            $where_clause
            GROUP BY pc.program_class_id, pc.program_class_code, pc.year, pc.teacher_id, d.department_name, t.teacher_full_name
            ORDER BY pc.program_class_code";
    
    $stmt = $conn->prepare($sql);
    if (!empty($params)) {
        $stmt->execute($params);
    } else {
        $stmt->execute();
    }
    
    $program_classes = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'data' => $program_classes
    ]);
}

function createProgramClass($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    $program_class_code = $input['program_class_code'] ?? '';
    $year = $input['year'] ?? '';
    $teacher_id = $input['teacher_id'] ?? null;
    $department_id = $input['department_id'] ?? null;
    
    // Validate required fields
    if (empty($program_class_code) || empty($year) || empty($department_id)) {
        echo json_encode(['success' => false, 'message' => 'Vui lòng điền đầy đủ thông tin bắt buộc']);
        return;
    }
    
    // Check if class code already exists
    $check_sql = "SELECT COUNT(*) FROM program_class WHERE program_class_code = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$program_class_code]);
    
    if ($check_stmt->fetchColumn() > 0) {
        echo json_encode(['success' => false, 'message' => 'Mã lớp đã tồn tại']);
        return;
    }
    
    // Insert new program class
    $sql = "INSERT INTO program_class (program_class_code, year, teacher_id, department_id) 
            VALUES (?, ?, ?, ?)";
    
    $stmt = $conn->prepare($sql);
    $teacher_id = $teacher_id > 0 ? $teacher_id : null;
    
    if ($stmt->execute([$program_class_code, $year, $teacher_id, $department_id])) {
        echo json_encode(['success' => true, 'message' => 'Thêm lớp thành công']);
    } else {
        echo json_encode(['success' => false, 'message' => 'Không thể thêm lớp']);
    }
}

function updateProgramClass($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    $program_class_id = $input['program_class_id'] ?? null;
    $program_class_code = $input['program_class_code'] ?? '';
    $year = $input['year'] ?? '';
    $teacher_id = $input['teacher_id'] ?? null;
    $department_id = $input['department_id'] ?? null;
    
    // Validate required fields
    if (empty($program_class_id) || empty($program_class_code) || empty($year) || empty($department_id)) {
        echo json_encode(['success' => false, 'message' => 'Vui lòng điền đầy đủ thông tin bắt buộc']);
        return;
    }
    
    // Check if class code already exists (excluding current class)
    $check_sql = "SELECT COUNT(*) FROM program_class WHERE program_class_code = ? AND program_class_id != ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$program_class_code, $program_class_id]);
    
    if ($check_stmt->fetchColumn() > 0) {
        echo json_encode(['success' => false, 'message' => 'Mã lớp đã tồn tại']);
        return;
    }
    
    // Update program class
    $sql = "UPDATE program_class 
            SET program_class_code = ?, year = ?, teacher_id = ?, department_id = ?
            WHERE program_class_id = ?";
    
    $stmt = $conn->prepare($sql);
    $teacher_id = $teacher_id > 0 ? $teacher_id : null;
    
    if ($stmt->execute([$program_class_code, $year, $teacher_id, $department_id, $program_class_id])) {
        echo json_encode(['success' => true, 'message' => 'Cập nhật lớp thành công']);
    } else {
        echo json_encode(['success' => false, 'message' => 'Không thể cập nhật lớp']);
    }
}

function deleteProgramClass($conn) {
    $program_class_id = $_GET['program_class_id'] ?? null;
    
    if (empty($program_class_id)) {
        echo json_encode(['success' => false, 'message' => 'ID lớp không hợp lệ']);
        return;
    }
    
    // Check if there are students in this class
    $check_sql = "SELECT COUNT(*) FROM student WHERE program_class_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$program_class_id]);
    
    if ($check_stmt->fetchColumn() > 0) {
        echo json_encode(['success' => false, 'message' => 'Không thể xóa lớp vì còn sinh viên trong lớp']);
        return;
    }
    
    // Delete program class
    $sql = "DELETE FROM program_class WHERE program_class_id = ?";
    $stmt = $conn->prepare($sql);
    
    if ($stmt->execute([$program_class_id])) {
        echo json_encode(['success' => true, 'message' => 'Xóa lớp thành công']);
    } else {
        echo json_encode(['success' => false, 'message' => 'Không thể xóa lớp']);
    }
}
?> 