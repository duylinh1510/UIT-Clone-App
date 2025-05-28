<?php
require_once 'db_connect.php';

try {
    // Check if user table exists and has data
    $stmt = $conn->query("SELECT * FROM user LIMIT 5");
    $users = $stmt->fetchAll();
    
    echo "=== USERS IN DATABASE ===\n";
    foreach ($users as $user) {
        echo "ID: " . $user['user_id'] . "\n";
        echo "Username: " . $user['username'] . "\n"; 
        echo "Password: " . $user['password'] . "\n";
        echo "Role: " . $user['role'] . "\n";
        echo "Is password hashed: " . (password_get_info($user['password'])['algo'] !== null ? 'Yes' : 'No') . "\n";
        echo "---\n";
    }
    
    // Check if student table exists
    $stmt = $conn->query("SELECT COUNT(*) as count FROM student");
    $student_count = $stmt->fetch();
    echo "\nStudent records: " . $student_count['count'] . "\n";
    
    // Check if teacher table exists  
    $stmt = $conn->query("SELECT COUNT(*) as count FROM teacher");
    $teacher_count = $stmt->fetch();
    echo "Teacher records: " . $teacher_count['count'] . "\n";
    
} catch (Exception $e) {
    echo "Error: " . $e->getMessage() . "\n";
}
?> 