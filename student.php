<?php
// Bật lỗi để dễ debug
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

require "db_connect.php";   

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// Log request cho debug
error_log("Request URI: " . $_SERVER['REQUEST_URI']);
error_log("Request Method: " . $_SERVER['REQUEST_METHOD']);
error_log("GET Parameters: " . json_encode($_GET));

// Get student_id from URL
$student_id = isset($_GET['student_id']) ? intval($_GET['student_id']) : 0;

error_log("Student ID: " . $student_id);

if ($student_id == 0) {
    http_response_code(400);
    echo json_encode(array(
        "success" => false,
        "message" => "Invalid student ID: " . $student_id
    ));
    exit();
}

$database = new Database();
$db = $database->getConnection();

if ($db == null) {
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Database connection failed"
    ));
    exit();
}

try {
    // Kiểm tra xem student có tồn tại không
    $check_student = "SELECT COUNT(*) as count FROM student WHERE student_id = :student_id";
    $check_stmt = $db->prepare($check_student);
    $check_stmt->bindParam(":student_id", $student_id);
    $check_stmt->execute();
    $student_exists = $check_stmt->fetch(PDO::FETCH_ASSOC);
    
    error_log("Student exists check: " . json_encode($student_exists));
    
    if ($student_exists['count'] == 0) {
        http_response_code(404);
        echo json_encode(array(
            "success" => false,
            "message" => "Student not found with ID: " . $student_id
        ));
        exit();
    }

    // Query to get student grades grouped by semester
    $query = "SELECT 
    g.id,
    g.student_id,
    g.subject_class_id,
    s.subject_code as subject_code,   -- SỬA chỗ này
    s.name as subject_name,
    sc.subject_class_code as class_code,  -- SỬA nếu cần
    s.credits,
    g.process_grade,
    g.practice_grade,
    g.midterm_grade,
    g.final_grade,
    g.semester,
    CASE 
        WHEN g.final_grade IS NOT NULL AND g.process_grade IS NOT NULL THEN
            ROUND((COALESCE(g.process_grade, 0) * 0.3 + 
                   COALESCE(g.practice_grade, 0) * 0.2 + 
                   COALESCE(g.midterm_grade, 0) * 0.2 + 
                   COALESCE(g.final_grade, 0) * 0.3), 1)
        ELSE NULL
    END as average_grade
FROM grade g
INNER JOIN subject_class sc ON g.subject_class_id = sc.subject_class_id
INNER JOIN subject s ON sc.subject_id = s.subject_id
WHERE g.student_id = :student_id
ORDER BY g.semester, s.subject_code";

    error_log("Executing query for student_id: " . $student_id);
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":student_id", $student_id);
    $stmt->execute();

    $grades = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    error_log("Number of grades found: " . count($grades));

    if (count($grades) > 0) {
        // Group grades by semester
        $semester_grades = array();
        
        foreach ($grades as $grade) {
            $semester = $grade['semester'];
            
            if (!isset($semester_grades[$semester])) {
                $semester_grades[$semester] = array(
                    'semester' => $semester,
                    'grades' => array()
                );
            }
            
            // Convert null values to appropriate format
            $grade_data = array(
                'id' => intval($grade['id']),
                'student_id' => intval($grade['student_id']),
                'subject_class_id' => intval($grade['subject_class_id']),
                'subject_code' => $grade['subject_code'],
                'subject_name' => $grade['subject_name'],
                'class_code' => $grade['class_code'],
                'credits' => intval($grade['credits']),
                'process_grade' => $grade['process_grade'] ? floatval($grade['process_grade']) : null,
                'practice_grade' => $grade['practice_grade'] ? floatval($grade['practice_grade']) : null,
                'midterm_grade' => $grade['midterm_grade'] ? floatval($grade['midterm_grade']) : null,
                'final_grade' => $grade['final_grade'] ? floatval($grade['final_grade']) : null,
                'average_grade' => $grade['average_grade'] ? floatval($grade['average_grade']) : null,
                'semester' => $grade['semester']
            );
            
            $semester_grades[$semester]['grades'][] = $grade_data;
        }
        
        // Convert associative array to indexed array
        $result = array_values($semester_grades);
        
        error_log("Final result: " . json_encode($result));
        
        http_response_code(200);
        echo json_encode(array(
            "success" => true,
            "message" => "Grades retrieved successfully",
            "data" => $result
        ));
    } else {
        // Kiểm tra xem có bất kỳ bảng nào không
        $check_tables = "SHOW TABLES LIKE 'grade'";
        $table_stmt = $db->prepare($check_tables);
        $table_stmt->execute();
        $table_exists = $table_stmt->fetch();
        
        if (!$table_exists) {
            http_response_code(500);
            echo json_encode(array(
                "success" => false,
                "message" => "Grade table does not exist in database"
            ));
        } else {
            http_response_code(404);
            echo json_encode(array(
                "success" => false,
                "message" => "No grades found for student ID: " . $student_id
            ));
        }
    }
} catch (Exception $e) {
    error_log("Exception: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(array(
        "success" => false,
        "message" => "Error retrieving grades: " . $e->getMessage()
    ));
}
exit;
?>