<?php
require "db_connect.php";

echo "<h2>Database Structure Check</h2>";

try {
    // Check if all required tables exist
    $tables = ['department', 'teacher', 'program_class', 'student', 'subject'];
    
    echo "<h3>1. Table Structure Check</h3>";
    foreach ($tables as $table) {
        echo "<h4>Table: $table</h4>";
        $sql = "DESCRIBE $table";
        $stmt = $conn->query($sql);
        $columns = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        echo "<table border='1'>";
        echo "<tr><th>Field</th><th>Type</th><th>Null</th><th>Key</th><th>Default</th><th>Extra</th></tr>";
        foreach ($columns as $column) {
            echo "<tr>";
            echo "<td>{$column['Field']}</td>";
            echo "<td>{$column['Type']}</td>";
            echo "<td>{$column['Null']}</td>";
            echo "<td>{$column['Key']}</td>";
            echo "<td>" . ($column['Default'] ?? 'NULL') . "</td>";
            echo "<td>{$column['Extra']}</td>";
            echo "</tr>";
        }
        echo "</table><br>";
    }
    
    echo "<h3>2. Foreign Key Relationships Check</h3>";
    
    // Check teacher -> department relationship
    echo "<h4>Teachers with departments:</h4>";
    $teacher_dept_sql = "SELECT t.teacher_id, t.teacher_full_name, t.department_id, d.department_name 
                         FROM teacher t 
                         LEFT JOIN department d ON t.department_id = d.department_id 
                         ORDER BY t.teacher_id";
    $stmt = $conn->query($teacher_dept_sql);
    $teacher_depts = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<table border='1'>";
    echo "<tr><th>Teacher ID</th><th>Teacher Name</th><th>Dept ID</th><th>Department Name</th></tr>";
    foreach ($teacher_depts as $row) {
        echo "<tr>";
        echo "<td>{$row['teacher_id']}</td>";
        echo "<td>{$row['teacher_full_name']}</td>";
        echo "<td>" . ($row['department_id'] ?? 'NULL') . "</td>";
        echo "<td>" . ($row['department_name'] ?? 'No Department') . "</td>";
        echo "</tr>";
    }
    echo "</table><br>";
    
    // Check program_class -> department relationship
    echo "<h4>Program classes with departments:</h4>";
    $class_dept_sql = "SELECT pc.program_class_id, pc.program_class_code, pc.department_id, d.department_name 
                       FROM program_class pc 
                       LEFT JOIN department d ON pc.department_id = d.department_id 
                       ORDER BY pc.program_class_id";
    $stmt = $conn->query($class_dept_sql);
    $class_depts = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<table border='1'>";
    echo "<tr><th>Class ID</th><th>Class Code</th><th>Dept ID</th><th>Department Name</th></tr>";
    foreach ($class_depts as $row) {
        echo "<tr>";
        echo "<td>{$row['program_class_id']}</td>";
        echo "<td>{$row['program_class_code']}</td>";
        echo "<td>" . ($row['department_id'] ?? 'NULL') . "</td>";
        echo "<td>" . ($row['department_name'] ?? 'No Department') . "</td>";
        echo "</tr>";
    }
    echo "</table><br>";
    
    // Check student -> program_class relationship
    echo "<h4>Students with program classes:</h4>";
    $student_class_sql = "SELECT s.student_id, s.student_full_name, s.program_class_id, pc.program_class_code, d.department_name 
                          FROM student s 
                          LEFT JOIN program_class pc ON s.program_class_id = pc.program_class_id 
                          LEFT JOIN department d ON pc.department_id = d.department_id 
                          ORDER BY s.student_id 
                          LIMIT 10";
    $stmt = $conn->query($student_class_sql);
    $student_classes = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<table border='1'>";
    echo "<tr><th>Student ID</th><th>Student Name</th><th>Class ID</th><th>Class Code</th><th>Department</th></tr>";
    foreach ($student_classes as $row) {
        echo "<tr>";
        echo "<td>{$row['student_id']}</td>";
        echo "<td>{$row['student_full_name']}</td>";
        echo "<td>" . ($row['program_class_id'] ?? 'NULL') . "</td>";
        echo "<td>" . ($row['program_class_code'] ?? 'No Class') . "</td>";
        echo "<td>" . ($row['department_name'] ?? 'No Department') . "</td>";
        echo "</tr>";
    }
    echo "</table><br>";
    
    echo "<h3>3. Department Usage Summary</h3>";
    $dept_usage_sql = "SELECT d.department_id, d.department_name,
                              (SELECT COUNT(*) FROM teacher t WHERE t.department_id = d.department_id) as teacher_count,
                              (SELECT COUNT(*) FROM program_class pc WHERE pc.department_id = d.department_id) as class_count,
                              (SELECT COUNT(*) FROM subject sub WHERE sub.department_id = d.department_id) as subject_count,
                              (SELECT COUNT(DISTINCT s.student_id) 
                               FROM student s 
                               JOIN program_class pc ON s.program_class_id = pc.program_class_id 
                               WHERE pc.department_id = d.department_id) as student_count
                       FROM department d 
                       ORDER BY d.department_id";
    $stmt = $conn->query($dept_usage_sql);
    $dept_usage = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo "<table border='1'>";
    echo "<tr><th>Dept ID</th><th>Department Name</th><th>Teachers</th><th>Classes</th><th>Subjects</th><th>Students</th></tr>";
    foreach ($dept_usage as $row) {
        echo "<tr>";
        echo "<td>{$row['department_id']}</td>";
        echo "<td>{$row['department_name']}</td>";
        echo "<td>{$row['teacher_count']}</td>";
        echo "<td>{$row['class_count']}</td>";
        echo "<td>{$row['subject_count']}</td>";
        echo "<td>{$row['student_count']}</td>";
        echo "</tr>";
    }
    echo "</table><br>";
    
} catch (Exception $e) {
    echo "❌ Error: " . $e->getMessage() . "<br>";
}

echo "<h3>Check completed!</h3>";
?> 