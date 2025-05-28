<?php
require_once 'db_connect.php';

echo "=== Testing Database Connection ===\n";

if ($conn === null) {
    echo "❌ Database connection failed\n";
    exit;
}

echo "✅ Database connected successfully\n\n";

try {
    // Check users table
    echo "=== USERS IN DATABASE ===\n";
    $stmt = $conn->query("SELECT * FROM user LIMIT 5");
    $users = $stmt->fetchAll();
    
    if (empty($users)) {
        echo "❌ No users found in database\n";
        
        // Create sample users
        echo "\n=== Creating sample users ===\n";
        
        // Create admin user
        $admin_sql = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
        $admin_stmt = $conn->prepare($admin_sql);
        $admin_stmt->execute(['admin', 'admin123', 'admin']);
        echo "✅ Admin user created: admin/admin123\n";
        
        // Create student user
        $student_sql = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
        $student_stmt = $conn->prepare($student_sql);
        $student_stmt->execute(['student1', 'student123', 'student']);
        $student_user_id = $conn->lastInsertId();
        echo "✅ Student user created: student1/student123\n";
        
        // Check if we have departments and program_class
        $dept_check = $conn->query("SELECT * FROM department LIMIT 1")->fetch();
        $class_check = $conn->query("SELECT * FROM program_class LIMIT 1")->fetch();
        
        if ($dept_check && $class_check) {
            // Create student profile
            $student_profile_sql = "INSERT INTO student (user_id, student_code, student_full_name, date_of_birth, student_email, student_address, program_class_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            $student_profile_stmt = $conn->prepare($student_profile_sql);
            $student_profile_stmt->execute([
                $student_user_id, 
                'SV001', 
                'Nguyễn Văn A', 
                '2000-01-01', 
                'student1@example.com', 
                'Hà Nội', 
                $class_check['program_class_id']
            ]);
            echo "✅ Student profile created\n";
        }
        
    } else {
        foreach ($users as $user) {
            echo "ID: " . $user['user_id'] . "\n";
            echo "Username: " . $user['username'] . "\n"; 
            echo "Password: " . $user['password'] . "\n";
            echo "Role: " . $user['role'] . "\n";
            echo "---\n";
        }
    }
    
    // Check student table
    $stmt = $conn->query("SELECT COUNT(*) as count FROM student");
    $student_count = $stmt->fetch();
    echo "\nStudent records: " . $student_count['count'] . "\n";
    
    // Check teacher table
    $stmt = $conn->query("SELECT COUNT(*) as count FROM teacher");
    $teacher_count = $stmt->fetch();
    echo "Teacher records: " . $teacher_count['count'] . "\n";
    
    // Check department table
    $stmt = $conn->query("SELECT COUNT(*) as count FROM department");
    $dept_count = $stmt->fetch();
    echo "Department records: " . $dept_count['count'] . "\n";
    
    // Check program_class table
    $stmt = $conn->query("SELECT COUNT(*) as count FROM program_class");
    $class_count = $stmt->fetch();
    echo "Program class records: " . $class_count['count'] . "\n";
    
} catch (Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "\n";
}
?> 