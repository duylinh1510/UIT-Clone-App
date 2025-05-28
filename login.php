<?php
// Bật lỗi để dễ debug
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['success' => false, 'message' => 'Method not allowed']);
    exit;
}

try {
    // Kiểm tra database connection
    if ($conn === null) {
        throw new Exception('Database connection failed');
    }
    
    $input = json_decode(file_get_contents('php://input'), true);
    
    error_log("Login request received: " . json_encode($input));
    
    if (!$input || !isset($input['username']) || !isset($input['password'])) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Username and password are required']);
        exit;
    }
    
    $username = trim($input['username']);
    $password = $input['password'];
    
    if (empty($username) || empty($password)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => 'Username and password cannot be empty']);
        exit;
    }
    
    // Check user credentials - sử dụng $conn từ db_connect.php
    $user_sql = "SELECT user_id, username, password, role FROM user WHERE username = ?";
    $user_stmt = $conn->prepare($user_sql);
    $user_stmt->execute([$username]);
    $user = $user_stmt->fetch(PDO::FETCH_ASSOC);
    
    error_log("User found: " . json_encode($user));
    
    if (!$user) {
        http_response_code(401);
        echo json_encode(['success' => false, 'message' => 'Invalid username or password']);
        exit;
    }
    
    // Verify password (check if password is hashed or plain text)
    $password_valid = false;
    if (password_get_info($user['password'])['algo'] !== null) {
        // Password is hashed, use password_verify
        $password_valid = password_verify($password, $user['password']);
    } else {
        // Password is plain text, compare directly
        $password_valid = ($password === $user['password']);
    }
    
    if (!$password_valid) {
        http_response_code(401);
        echo json_encode(['success' => false, 'message' => 'Invalid username or password']);
        exit;
    }
    
    $response_data = [
        'user_id' => (int)$user['user_id'],
        'username' => $user['username'],
        'role' => $user['role']
    ];
    
    // Get profile data based on role
    if ($user['role'] === 'student') {
        // Get student profile - theo cấu trúc database quanlysinhvien
        $student_sql = "SELECT s.student_id, s.student_code, s.student_full_name, s.date_of_birth, 
                               s.student_email, s.student_address, s.program_class_id,
                               pc.program_class_code,
                               d.department_id, d.department_name
                        FROM student s 
                        LEFT JOIN program_class pc ON s.program_class_id = pc.program_class_id 
                        LEFT JOIN department d ON pc.department_id = d.department_id 
                        WHERE s.user_id = ?";
        $student_stmt = $conn->prepare($student_sql);
        $student_stmt->execute([$user['user_id']]);
        $student_profile = $student_stmt->fetch(PDO::FETCH_ASSOC);
        
        error_log("Student profile: " . json_encode($student_profile));
        
        if ($student_profile) {
            $response_data['profile'] = [
                'student_id' => (int)$student_profile['student_id'],
                'student_code' => $student_profile['student_code'],
                'full_name' => $student_profile['student_full_name'],
                'birth_date' => $student_profile['date_of_birth'],
                'student_email' => $student_profile['student_email'],
                'student_address' => $student_profile['student_address'],
                'class_name' => $student_profile['program_class_code'],
                'department' => $student_profile['department_name']
            ];
        }
        
    } elseif ($user['role'] === 'teacher') {
        // Get teacher profile
        $teacher_sql = "SELECT t.teacher_id, t.teacher_full_name, t.date_of_birth, 
                               t.teacher_email
                        FROM teacher t 
                        WHERE t.user_id = ?";
        $teacher_stmt = $conn->prepare($teacher_sql);
        $teacher_stmt->execute([$user['user_id']]);
        $teacher_profile = $teacher_stmt->fetch(PDO::FETCH_ASSOC);
        
        error_log("Teacher profile: " . json_encode($teacher_profile));
        
        if ($teacher_profile) {
            $response_data['profile'] = [
                'teacher_id' => (int)$teacher_profile['teacher_id'],
                'teacher_full_name' => $teacher_profile['teacher_full_name'],
                'date_of_birth' => $teacher_profile['date_of_birth'],
                'teacher_email' => $teacher_profile['teacher_email']
            ];
        }
        
    } elseif ($user['role'] === 'admin') {
        // Admin doesn't need profile data, just basic info
        $response_data['profile'] = [
            'admin_name' => 'Administrator'
        ];
    }
    
    echo json_encode([
        'success' => true,
        'message' => 'Login successful',
        'data' => $response_data
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false, 
        'message' => 'Server error: ' . $e->getMessage()
    ]);
}
?> 