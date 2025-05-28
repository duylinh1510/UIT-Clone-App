<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(['success' => false, 'message' => 'Method not allowed']);
    exit;
}

try {
    $user_id = isset($_GET['user_id']) ? (int)$_GET['user_id'] : null;
    $student_id = isset($_GET['student_id']) ? (int)$_GET['student_id'] : null;

    if (!$user_id && !$student_id) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'user_id or student_id parameter is required']);
        exit;
    }

    // Nếu có user_id, tìm student_id tương ứng
    if ($user_id && !$student_id) {
        $student_sql = "SELECT student_id FROM student WHERE user_id = ?";
        $student_stmt = $conn->prepare($student_sql);
        $student_stmt->execute([$user_id]);
        $student_result = $student_stmt->fetch(PDO::FETCH_ASSOC);

        if (!$student_result) {
            http_response_code(404);
            echo json_encode(['success' => false, 'message' => 'Student not found for this user']);
            exit;
        }

        $student_id = $student_result['student_id'];
    }

    // Truy vấn lịch thi
    $sql = "SELECT es.exam_schedule_id, es.exam_date, es.start_time, es.end_time, 
                   es.exam_room, es.semester,
                   s.subject_code, s.name AS subject_name, s.credits,
                   d.department_name,
                   t.teacher_full_name AS examiner_name
            FROM exam_schedule es
            JOIN subject_class sc ON es.subject_class_id = sc.subject_class_id
            JOIN subject s ON sc.subject_id = s.subject_id
            LEFT JOIN department d ON s.department_id = d.department_id
            LEFT JOIN teacher t ON sc.teacher_id = t.teacher_id
            WHERE es.student_id = ?
            ORDER BY es.exam_date ASC, es.start_time ASC";

    $stmt = $conn->prepare($sql);
    $stmt->execute([$student_id]);
    $exam_schedules = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($exam_schedules as &$exam) {
        $exam['exam_schedule_id'] = (int)$exam['exam_schedule_id'];
        $exam['credits'] = (int)$exam['credits'];

        if ($exam['exam_date']) {
            $date = new DateTime($exam['exam_date']);
            $exam['exam_date_formatted'] = $date->format('d/m/Y');
        }
    }

    echo json_encode([
        'success' => true,
        'data' => $exam_schedules,
        'student_id' => $student_id,
        'total_exams' => count($exam_schedules)
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?>
