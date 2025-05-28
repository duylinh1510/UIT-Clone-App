<?php
require_once 'db_connect.php';

echo "<h2>Kiểm tra cấu trúc bảng teacher</h2>";

try {
    echo "<h3>DESCRIBE teacher table:</h3>";
    $stmt = $conn->query('DESCRIBE teacher');
    echo "<table border='1'>";
    echo "<tr><th>Field</th><th>Type</th><th>Null</th><th>Key</th><th>Default</th></tr>";
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        echo "<tr>";
        echo "<td>{$row['Field']}</td>";
        echo "<td>{$row['Type']}</td>";
        echo "<td>{$row['Null']}</td>";
        echo "<td>{$row['Key']}</td>";
        echo "<td>{$row['Default']}</td>";
        echo "</tr>";
    }
    echo "</table>";
    
    echo "<h3>Sample teacher data:</h3>";
    $stmt = $conn->query('SELECT * FROM teacher LIMIT 3');
    $teachers = $stmt->fetchAll(PDO::FETCH_ASSOC);
    if (!empty($teachers)) {
        echo "<table border='1'>";
        $first = true;
        foreach ($teachers as $teacher) {
            if ($first) {
                echo "<tr>";
                foreach (array_keys($teacher) as $key) {
                    echo "<th>$key</th>";
                }
                echo "</tr>";
                $first = false;
            }
            echo "<tr>";
            foreach ($teacher as $value) {
                echo "<td>$value</td>";
            }
            echo "</tr>";
        }
        echo "</table>";
    } else {
        echo "No teacher data found.";
    }
    
} catch (Exception $e) {
    echo "❌ Error: " . $e->getMessage();
}
?> 