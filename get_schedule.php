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
    $day_of_week = isset($_GET['day_of_week']) ? (int)$_GET['day_of_week'] : null;
    $student_id = isset($_GET['student_id']) ? (int)$_GET['student_id'] : null;
    
    if ($day_of_week === null) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'day_of_week parameter is required']);
        exit;
    }
    
    if ($day_of_week < 1 || $day_of_week > 7) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'day_of_week must be between 1 (Sunday) and 7 (Saturday)']);
        exit;
    }
    
    // Build query based on whether student_id is provided
    if ($student_id) {
        // Get timetable for specific student
        $sql = "SELECT tt.timetable_id as schedule_id, tt.subject_class_id, tt.day_of_week, tt.period,
                       tt.start_time, tt.end_time, '' as classroom,
                       s.subject_code, s.name as subject_name, s.credits,
                       subcl.subject_class_code as subject_class,
                       t.teacher_full_name,
                       d.department_name
                FROM timetable tt
                JOIN subject_class subcl ON tt.subject_class_id = subcl.subject_class_id
                JOIN subject s ON subcl.subject_id = s.subject_id
                LEFT JOIN teacher t ON subcl.teacher_id = t.teacher_id
                LEFT JOIN department d ON s.department_id = d.department_id
                JOIN student st ON st.program_class_id IN (
                    SELECT DISTINCT pc.program_class_id 
                    FROM program_class pc 
                    JOIN subject_class sc ON sc.teacher_id = pc.teacher_id 
                    WHERE sc.subject_class_id = tt.subject_class_id
                )
                WHERE tt.day_of_week = ? AND st.student_id = ?
                ORDER BY tt.period";
        
        $stmt = $conn->prepare($sql);
        $stmt->execute([$day_of_week, $student_id]);
        
    } else {
        // Get all timetables for the day (admin view)
        $sql = "SELECT tt.timetable_id as schedule_id, tt.subject_class_id, tt.day_of_week, tt.period,
                       tt.start_time, tt.end_time, '' as classroom,
                       s.subject_code, s.name as subject_name, s.credits,
                       subcl.subject_class_code as subject_class,
                       t.teacher_full_name,
                       d.department_name
                FROM timetable tt
                JOIN subject_class subcl ON tt.subject_class_id = subcl.subject_class_id
                JOIN subject s ON subcl.subject_id = s.subject_id
                LEFT JOIN teacher t ON subcl.teacher_id = t.teacher_id
                LEFT JOIN department d ON s.department_id = d.department_id
                WHERE tt.day_of_week = ?
                ORDER BY tt.period";
        
        $stmt = $conn->prepare($sql);
        $stmt->execute([$day_of_week]);
    }
    
    $schedules = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Convert numeric types
    foreach ($schedules as &$schedule) {
        $schedule['schedule_id'] = (int)$schedule['schedule_id'];
        $schedule['subject_class_id'] = (int)$schedule['subject_class_id'];
        $schedule['day_of_week'] = (int)$schedule['day_of_week'];
        $schedule['period'] = (int)$schedule['period'];
        $schedule['credits'] = (int)$schedule['credits'];
    }
    
    echo json_encode([
        'success' => true,
        'data' => $schedules,
        'day_of_week' => $day_of_week,
        'total_schedules' => count($schedules)
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false, 
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?> 