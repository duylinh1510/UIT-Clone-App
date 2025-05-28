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
            getGrades($conn);
            break;
        case 'POST':
            createGrade($conn);
            break;
        case 'PUT':
            updateGrade($conn);
            break;
        case 'DELETE':
            deleteGrade($conn);
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

// For admin - get all grades with filters and pagination
function getGrades($conn) {
    $page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
    $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 20;
    $search = isset($_GET['search']) ? $_GET['search'] : '';
    $semester_id = isset($_GET['semester_id']) ? (int)$_GET['semester_id'] : null;
    $subject_id = isset($_GET['subject_id']) ? (int)$_GET['subject_id'] : null;
    $department_id = isset($_GET['department_id']) ? (int)$_GET['department_id'] : null;
    
    $offset = ($page - 1) * $limit;
    
    // Build WHERE clause
    $where_conditions = [];
    $params = [];
    
    if (!empty($search)) {
        $where_conditions[] = "(st.student_code LIKE ? OR st.student_full_name LIKE ? OR s.subject_code LIKE ? OR s.name LIKE ?)";
        $params[] = "%$search%";
        $params[] = "%$search%";
        $params[] = "%$search%";
        $params[] = "%$search%";
    }
    
    if (!empty($_GET['semester'])) {
        $where_conditions[] = "g.semester = ?";
        $params[] = $_GET['semester'];
    }
    
    if ($subject_id) {
        $where_conditions[] = "sc.subject_id = ?";
        $params[] = $subject_id;
    }
    
    if ($department_id) {
        $where_conditions[] = "s.department_id = ?";
        $params[] = $department_id;
    }
    
    $where_clause = !empty($where_conditions) ? 'WHERE ' . implode(' AND ', $where_conditions) : '';
    
    // Count total records (sửa theo lược đồ database đúng)
    $count_sql = "SELECT COUNT(*) as total 
                  FROM grade g
                  JOIN student st ON g.student_id = st.student_id
                  JOIN subject_class sc ON g.subject_class_id = sc.subject_class_id
                  JOIN subject s ON sc.subject_id = s.subject_id
                  $where_clause";
    
    $count_stmt = $conn->prepare($count_sql);
    if (!empty($params)) {
        $count_stmt->execute($params);
    } else {
        $count_stmt->execute();
    }
    $total = $count_stmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    // Get grades with pagination (sửa theo lược đồ database đúng)
    $sql = "SELECT g.id as grade_id, g.student_id, g.subject_class_id, g.semester,
                   g.process_grade, g.practice_grade, g.midterm_grade, g.final_grade,
                   st.student_code, st.student_full_name as student_name,
                   s.subject_code, s.name as subject_name, s.credits,
                   sc.subject_class_code, sc.semester as class_semester,
                   CASE 
                       WHEN g.final_grade IS NOT NULL THEN 
                           ROUND((COALESCE(g.process_grade, 0) * 0.1 + 
                                  COALESCE(g.practice_grade, 0) * 0.2 + 
                                  COALESCE(g.midterm_grade, 0) * 0.3 + 
                                  g.final_grade * 0.4), 2)
                       ELSE NULL
                   END as average_grade
            FROM grade g
            JOIN student st ON g.student_id = st.student_id
            JOIN subject_class sc ON g.subject_class_id = sc.subject_class_id
            JOIN subject s ON sc.subject_id = s.subject_id
            $where_clause
            ORDER BY g.semester DESC, st.student_code, s.subject_code
            LIMIT $limit OFFSET $offset";
    
    $stmt = $conn->prepare($sql);
    if (!empty($params)) {
        $stmt->execute($params);
    } else {
        $stmt->execute();
    }
    
    $grades = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    $totalPages = ceil($total / $limit);
    
    echo json_encode([
        'success' => true,
        'data' => $grades,
        'pagination' => [
            'total' => (int)$total,
            'page' => $page,
            'limit' => $limit,
            'totalPages' => $totalPages
        ]
    ]);
}

function createGrade($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid JSON input']);
        return;
    }
    
    // Validate required fields
    $required = ['student_id', 'subject_class_id', 'semester'];
    foreach ($required as $field) {
        if (!isset($input[$field])) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => "Missing required field: $field"]);
            return;
        }
    }
    
    // Validate grade values (0-10)
    $grade_fields = ['process_grade', 'practice_grade', 'midterm_grade', 'final_grade'];
    foreach ($grade_fields as $field) {
        if (isset($input[$field])) {
            if ($input[$field] < 0 || $input[$field] > 10) {
                http_response_code(400);
                echo json_encode(['success' => false, 'message' => "$field must be between 0 and 10"]);
                return;
            }
        }
    }
    
    // Check if grade record already exists
    $check_sql = "SELECT id FROM grade WHERE student_id = ? AND subject_class_id = ? AND semester = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$input['student_id'], $input['subject_class_id'], $input['semester']]);
    if ($check_stmt->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Grade record already exists for this student, subject class and semester']);
        return;
    }
    
    // Verify student and subject_class exist
    $verify_student = $conn->prepare("SELECT student_id FROM student WHERE student_id = ?");
    $verify_student->execute([$input['student_id']]);
    if (!$verify_student->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Student not found']);
        return;
    }
    
    $verify_subject_class = $conn->prepare("SELECT subject_class_id FROM subject_class WHERE subject_class_id = ?");
    $verify_subject_class->execute([$input['subject_class_id']]);
    if (!$verify_subject_class->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Subject class not found']);
        return;
    }
    
    try {
        // Create grade record
        $grade_sql = "INSERT INTO grade (student_id, subject_class_id, semester, process_grade, practice_grade, midterm_grade, final_grade) 
                      VALUES (?, ?, ?, ?, ?, ?, ?)";
        $grade_stmt = $conn->prepare($grade_sql);
        $grade_stmt->execute([
            $input['student_id'],
            $input['subject_class_id'],
            $input['semester'],
            $input['process_grade'] ?? null,
            $input['practice_grade'] ?? null,
            $input['midterm_grade'] ?? null,
            $input['final_grade'] ?? null
        ]);
        
        echo json_encode(['success' => true, 'message' => 'Grade created successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error creating grade: ' . $e->getMessage()]);
    }
}

function updateGrade($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input || !isset($input['grade_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid input or missing grade_id']);
        return;
    }
    
    // Check if grade exists
    $check_sql = "SELECT id FROM grade WHERE id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$input['grade_id']]);
    
    if (!$check_stmt->fetch()) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Grade not found']);
        return;
    }
    
    // Validate grade values (0-10)
    $grade_fields = ['process_grade', 'practice_grade', 'midterm_grade', 'final_grade'];
    foreach ($grade_fields as $field) {
        if (isset($input[$field]) && $input[$field] !== null) {
            if ($input[$field] < 0 || $input[$field] > 10) {
                http_response_code(400);
                echo json_encode(['success' => false, 'message' => "$field must be between 0 and 10"]);
                return;
            }
        }
    }
    
    try {
        // Update grade record
        $grade_sql = "UPDATE grade SET 
                      process_grade = COALESCE(?, process_grade),
                      practice_grade = COALESCE(?, practice_grade),
                      midterm_grade = COALESCE(?, midterm_grade),
                      final_grade = COALESCE(?, final_grade)
                      WHERE id = ?";
        
        $grade_stmt = $conn->prepare($grade_sql);
        $grade_stmt->execute([
            $input['process_grade'] ?? null,
            $input['practice_grade'] ?? null,
            $input['midterm_grade'] ?? null,
            $input['final_grade'] ?? null,
            $input['grade_id']
        ]);
        
        echo json_encode(['success' => true, 'message' => 'Grade updated successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error updating grade: ' . $e->getMessage()]);
    }
}

function deleteGrade($conn) {
    if (!isset($_GET['grade_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Missing grade_id']);
        return;
    }
    
    $grade_id = (int)$_GET['grade_id'];
    
    // Check if grade exists
    $check_sql = "SELECT id FROM grade WHERE id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$grade_id]);
    
    if (!$check_stmt->fetch()) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Grade not found']);
        return;
    }
    
    try {
        // Delete grade
        $delete_sql = "DELETE FROM grade WHERE id = ?";
        $delete_stmt = $conn->prepare($delete_sql);
        $delete_stmt->execute([$grade_id]);
        
        echo json_encode(['success' => true, 'message' => 'Grade deleted successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error deleting grade: ' . $e->getMessage()]);
    }
}
?> 