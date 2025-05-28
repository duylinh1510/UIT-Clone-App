<?php
echo "<h2>Test Admin APIs</h2>";

echo "<h3>1. Test Departments API</h3>";
echo "<a href='admin/departments.php' target='_blank'>GET admin/departments.php</a><br><br>";

echo "<h3>2. Test Students API</h3>";
echo "<a href='admin/students.php?page=1&limit=5' target='_blank'>GET admin/students.php?page=1&limit=5</a><br><br>";

echo "<h3>3. Test Teachers API</h3>";
echo "<a href='admin/teachers.php?page=1&limit=5' target='_blank'>GET admin/teachers.php?page=1&limit=5</a><br><br>";

echo "<h3>4. Test Subjects API</h3>";
echo "<a href='admin/subjects.php' target='_blank'>GET admin/subjects.php</a><br><br>";

echo "<h3>5. Test Grades API</h3>";
echo "<a href='admin/grades.php' target='_blank'>GET admin/grades.php</a><br><br>";

echo "<hr>";
echo "<h3>Test Database Connection</h3>";

require_once 'db_connect.php';

if ($conn === null) {
    echo "❌ Database connection failed<br>";
} else {
    echo "✅ Database connected successfully<br><br>";
    
    try {
        // Test departments
        $dept_stmt = $conn->query("SELECT COUNT(*) as count FROM department");
        $dept_count = $dept_stmt->fetch()['count'];
        echo "Departments: $dept_count<br>";
        
        // Test students  
        $student_stmt = $conn->query("SELECT COUNT(*) as count FROM student");
        $student_count = $student_stmt->fetch()['count'];
        echo "Students: $student_count<br>";
        
        // Test teachers
        $teacher_stmt = $conn->query("SELECT COUNT(*) as count FROM teacher");
        $teacher_count = $teacher_stmt->fetch()['count'];
        echo "Teachers: $teacher_count<br>";
        
        // Test subjects
        $subject_stmt = $conn->query("SELECT COUNT(*) as count FROM subject");
        $subject_count = $subject_stmt->fetch()['count'];
        echo "Subjects: $subject_count<br>";
        
        // Test program_class
        $class_stmt = $conn->query("SELECT COUNT(*) as count FROM program_class");
        $class_count = $class_stmt->fetch()['count'];
        echo "Program Classes: $class_count<br>";
        
        // Test subject_class
        $sc_stmt = $conn->query("SELECT COUNT(*) as count FROM subject_class");
        $sc_count = $sc_stmt->fetch()['count'];
        echo "Subject Classes: $sc_count<br>";
        
        // Test grades
        $grade_stmt = $conn->query("SELECT COUNT(*) as count FROM grade");
        $grade_count = $grade_stmt->fetch()['count'];
        echo "Grades: $grade_count<br>";
        
    } catch (Exception $e) {
        echo "❌ Error: " . $e->getMessage() . "<br>";
    }
}
?> 