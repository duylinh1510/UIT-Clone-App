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
            getTimetables($conn);
            break;
        case 'POST':
            createTimetable($conn);
            break;
        case 'PUT':
            updateTimetable($conn);
            break;
        case 'DELETE':
            deleteTimetable($conn);
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

function getTimetables($conn) {
    $search = isset($_GET['search']) ? $_GET['search'] : '';
    $day_of_week = isset($_GET['day_of_week']) ? (int)$_GET['day_of_week'] : null;
    $subject_class_id = isset($_GET['subject_class_id']) ? (int)$_GET['subject_class_id'] : null;
    
    // Build WHERE clause
    $where_conditions = [];
    $params = [];
    
    if (!empty($search)) {
        $where_conditions[] = "(s.subject_code LIKE ? OR s.name LIKE ? OR sc.subject_class_code LIKE ? OR t.teacher_full_name LIKE ?)";
        $params[] = "%$search%";
        $params[] = "%$search%";
        $params[] = "%$search%";
        $params[] = "%$search%";
    }
    
    if ($day_of_week) {
        $where_conditions[] = "tt.day_of_week = ?";
        $params[] = $day_of_week;
    }
    
    if ($subject_class_id) {
        $where_conditions[] = "tt.subject_class_id = ?";
        $params[] = $subject_class_id;
    }
    
    $where_clause = !empty($where_conditions) ? 'WHERE ' . implode(' AND ', $where_conditions) : '';
    
    // Get timetables with related info
    $sql = "SELECT tt.timetable_id, tt.subject_class_id, tt.day_of_week, tt.period,
                   tt.start_time, tt.end_time,
                   sc.subject_class_code, sc.semester,
                   s.subject_id, s.subject_code, s.name as subject_name, s.credits,
                   t.teacher_id, t.teacher_full_name,
                   d.department_name,
                   CASE tt.day_of_week
                       WHEN 1 THEN 'Chủ nhật'
                       WHEN 2 THEN 'Thứ hai'
                       WHEN 3 THEN 'Thứ ba'
                       WHEN 4 THEN 'Thứ tư'
                       WHEN 5 THEN 'Thứ năm'
                       WHEN 6 THEN 'Thứ sáu'
                       WHEN 7 THEN 'Thứ bảy'
                   END as day_name
            FROM timetable tt
            JOIN subject_class sc ON tt.subject_class_id = sc.subject_class_id
            JOIN subject s ON sc.subject_id = s.subject_id
            LEFT JOIN teacher t ON sc.teacher_id = t.teacher_id
            LEFT JOIN department d ON s.department_id = d.department_id
            $where_clause
            ORDER BY tt.day_of_week, tt.period, s.subject_code";
    
    $stmt = $conn->prepare($sql);
    if (!empty($params)) {
        $stmt->execute($params);
    } else {
        $stmt->execute();
    }
    
    $timetables = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Convert numeric fields
    foreach ($timetables as &$tt) {
        $tt['timetable_id'] = (int)$tt['timetable_id'];
        $tt['subject_class_id'] = (int)$tt['subject_class_id'];
        $tt['day_of_week'] = (int)$tt['day_of_week'];
        $tt['period'] = (int)$tt['period'];
        $tt['subject_id'] = (int)$tt['subject_id'];
        $tt['credits'] = (int)$tt['credits'];
        $tt['teacher_id'] = (int)$tt['teacher_id'];
    }
    
    echo json_encode([
        'success' => true,
        'data' => $timetables
    ]);
}

function createTimetable($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid JSON input']);
        return;
    }
    
    // Validate required fields
    $required = ['subject_class_id', 'day_of_week', 'period', 'start_time', 'end_time'];
    foreach ($required as $field) {
        if (!isset($input[$field])) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => "Missing required field: $field"]);
            return;
        }
    }
    
    // Validate day_of_week (1-7)
    if ($input['day_of_week'] < 1 || $input['day_of_week'] > 7) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'day_of_week must be between 1 and 7']);
        return;
    }
    
    // Validate period (1-12)
    if ($input['period'] < 1 || $input['period'] > 12) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'period must be between 1 and 12']);
        return;
    }
    
    // Check if subject_class exists
    $check_sc_sql = "SELECT subject_class_id FROM subject_class WHERE subject_class_id = ?";
    $check_sc_stmt = $conn->prepare($check_sc_sql);
    $check_sc_stmt->execute([$input['subject_class_id']]);
    if (!$check_sc_stmt->fetch()) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Subject class not found']);
        return;
    }
    
    // Check for conflicts (same day, period with different subject)
    $conflict_sql = "SELECT tt.timetable_id, s.subject_code, s.name 
                     FROM timetable tt
                     JOIN subject_class sc ON tt.subject_class_id = sc.subject_class_id
                     JOIN subject s ON sc.subject_id = s.subject_id
                     WHERE tt.day_of_week = ? AND tt.period = ? AND tt.subject_class_id != ?";
    $conflict_stmt = $conn->prepare($conflict_sql);
    $conflict_stmt->execute([$input['day_of_week'], $input['period'], $input['subject_class_id']]);
    $conflict = $conflict_stmt->fetch();
    
    if ($conflict) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Time conflict with: ' . $conflict['subject_code'] . ' - ' . $conflict['name']]);
        return;
    }
    
    try {
        // Create timetable record
        $timetable_sql = "INSERT INTO timetable (subject_class_id, day_of_week, period, start_time, end_time) 
                          VALUES (?, ?, ?, ?, ?)";
        $timetable_stmt = $conn->prepare($timetable_sql);
        $timetable_stmt->execute([
            $input['subject_class_id'],
            $input['day_of_week'],
            $input['period'],
            $input['start_time'],
            $input['end_time']
        ]);
        
        echo json_encode(['success' => true, 'message' => 'Timetable created successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error creating timetable: ' . $e->getMessage()]);
    }
}

function updateTimetable($conn) {
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input || !isset($input['timetable_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Invalid input or missing timetable_id']);
        return;
    }
    
    // Check if timetable exists
    $check_sql = "SELECT timetable_id FROM timetable WHERE timetable_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$input['timetable_id']]);
    
    if (!$check_stmt->fetch()) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Timetable not found']);
        return;
    }
    
    // Validate day_of_week if provided
    if (isset($input['day_of_week']) && ($input['day_of_week'] < 1 || $input['day_of_week'] > 7)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'day_of_week must be between 1 and 7']);
        return;
    }
    
    // Validate period if provided
    if (isset($input['period']) && ($input['period'] < 1 || $input['period'] > 12)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'period must be between 1 and 12']);
        return;
    }
    
    // Check for conflicts if day/period is being changed
    if (isset($input['day_of_week']) && isset($input['period'])) {
        $conflict_sql = "SELECT tt.timetable_id, s.subject_code, s.name 
                         FROM timetable tt
                         JOIN subject_class sc ON tt.subject_class_id = sc.subject_class_id
                         JOIN subject s ON sc.subject_id = s.subject_id
                         WHERE tt.day_of_week = ? AND tt.period = ? AND tt.timetable_id != ?";
        $conflict_stmt = $conn->prepare($conflict_sql);
        $conflict_stmt->execute([$input['day_of_week'], $input['period'], $input['timetable_id']]);
        $conflict = $conflict_stmt->fetch();
        
        if ($conflict) {
            http_response_code(400);
            echo json_encode(['success' => false, 'message' => 'Time conflict with: ' . $conflict['subject_code'] . ' - ' . $conflict['name']]);
            return;
        }
    }
    
    try {
        // Update timetable record
        $timetable_sql = "UPDATE timetable SET 
                          subject_class_id = COALESCE(?, subject_class_id),
                          day_of_week = COALESCE(?, day_of_week),
                          period = COALESCE(?, period),
                          start_time = COALESCE(?, start_time),
                          end_time = COALESCE(?, end_time)
                          WHERE timetable_id = ?";
        
        $timetable_stmt = $conn->prepare($timetable_sql);
        $timetable_stmt->execute([
            $input['subject_class_id'] ?? null,
            $input['day_of_week'] ?? null,
            $input['period'] ?? null,
            $input['start_time'] ?? null,
            $input['end_time'] ?? null,
            $input['timetable_id']
        ]);
        
        echo json_encode(['success' => true, 'message' => 'Timetable updated successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error updating timetable: ' . $e->getMessage()]);
    }
}

function deleteTimetable($conn) {
    if (!isset($_GET['timetable_id'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Missing timetable_id']);
        return;
    }
    
    $timetable_id = (int)$_GET['timetable_id'];
    
    // Check if timetable exists
    $check_sql = "SELECT timetable_id FROM timetable WHERE timetable_id = ?";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute([$timetable_id]);
    
    if (!$check_stmt->fetch()) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Timetable not found']);
        return;
    }
    
    try {
        // Delete timetable
        $delete_sql = "DELETE FROM timetable WHERE timetable_id = ?";
        $delete_stmt = $conn->prepare($delete_sql);
        $delete_stmt->execute([$timetable_id]);
        
        echo json_encode(['success' => true, 'message' => 'Timetable deleted successfully']);
        
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(['success' => false, 'message' => 'Error deleting timetable: ' . $e->getMessage()]);
    }
}
?> 