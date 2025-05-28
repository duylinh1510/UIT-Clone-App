<?php
require_once 'db_connect.php';

echo "=== Tạo tài khoản Admin ===\n";

try {
    // Kiểm tra kết nối database
    if ($conn === null) {
        throw new Exception('Không thể kết nối database');
    }
    
    echo "✅ Kết nối database thành công\n";
    
    // Kiểm tra xem admin đã tồn tại chưa
    $check_sql = "SELECT * FROM user WHERE role = 'admin' LIMIT 1";
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->execute();
    $existing_admin = $check_stmt->fetch();
    
    if ($existing_admin) {
        echo "⚠️ Tài khoản admin đã tồn tại:\n";
        echo "   Username: " . $existing_admin['username'] . "\n";
        echo "   User ID: " . $existing_admin['user_id'] . "\n";
        echo "   Role: " . $existing_admin['role'] . "\n";
        
        // Kiểm tra mật khẩu và cập nhật nếu cần
        if ($existing_admin['password'] !== 'admin123') {
            echo "\n🔄 Cập nhật mật khẩu admin...\n";
            $update_password_sql = "UPDATE user SET password = ? WHERE user_id = ?";
            $update_stmt = $conn->prepare($update_password_sql);
            $update_stmt->execute(['admin123', $existing_admin['user_id']]);
            echo "✅ Mật khẩu admin đã được cập nhật thành 'admin123'\n";
        }
    } else {
        echo "📝 Tạo tài khoản admin mới...\n";
        
        // Tạo tài khoản admin
        $admin_sql = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
        $admin_stmt = $conn->prepare($admin_sql);
        $admin_stmt->execute(['admin', 'admin123', 'admin']);
        
        $admin_id = $conn->lastInsertId();
        
        echo "✅ Tài khoản admin đã được tạo thành công!\n";
        echo "   User ID: " . $admin_id . "\n";
        echo "   Username: admin\n";
        echo "   Password: admin123\n";
        echo "   Role: admin\n";
    }
    
    echo "\n=== Kiểm tra các bảng trong database ===\n";
    
    // Kiểm tra các bảng có tồn tại không
    $tables = ['user', 'department', 'teacher', 'program_class', 'student', 'subject', 'subject_class', 'grade', 'timetable', 'exam_schedule'];
    
    foreach ($tables as $table) {
        try {
            $count_sql = "SELECT COUNT(*) as count FROM $table";
            $count_stmt = $conn->prepare($count_sql);
            $count_stmt->execute();
            $count = $count_stmt->fetch()['count'];
            echo "✅ Bảng '$table': $count bản ghi\n";
        } catch (Exception $e) {
            echo "❌ Lỗi bảng '$table': " . $e->getMessage() . "\n";
        }
    }
    
    echo "\n=== Tạo dữ liệu mẫu cơ bản (nếu chưa có) ===\n";
    
    // Tạo khoa mẫu nếu chưa có
    $dept_count_sql = "SELECT COUNT(*) as count FROM department";
    $dept_count_stmt = $conn->prepare($dept_count_sql);
    $dept_count_stmt->execute();
    $dept_count = $dept_count_stmt->fetch()['count'];
    
    if ($dept_count == 0) {
        echo "📝 Tạo khoa mẫu...\n";
        $dept_sql = "INSERT INTO department (department_name) VALUES (?)";
        $dept_stmt = $conn->prepare($dept_sql);
        
        $departments = ['Công nghệ thông tin', 'Kỹ thuật điện', 'Kỹ thuật cơ khí', 'Quản trị kinh doanh'];
        
        foreach ($departments as $dept_name) {
            $dept_stmt->execute([$dept_name]);
            echo "   ✅ Tạo khoa: $dept_name\n";
        }
    }
    
    echo "\n🎉 Hoàn tất! Bạn có thể đăng nhập với:\n";
    echo "   Username: admin\n";
    echo "   Password: admin123\n";
    
} catch (Exception $e) {
    echo "❌ Lỗi: " . $e->getMessage() . "\n";
    echo "Stack trace: " . $e->getTraceAsString() . "\n";
}
?> 