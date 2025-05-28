<?php
echo "<h2>Test Teachers API by Department</h2>";

// Test 1: Get all teachers
echo "<h3>1. Test GET all teachers</h3>";
$url = "http://localhost/DoAn/admin/teachers.php?page=1&limit=100&search=";
$response = file_get_contents($url);
$data = json_decode($response, true);
echo "<pre>" . json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";

// Test 2: Get teachers by department 1
echo "<h3>2. Test GET teachers by department 1</h3>";
$url = "http://localhost/DoAn/admin/teachers.php?page=1&limit=100&search=&department_id=1";
$response = file_get_contents($url);
$data = json_decode($response, true);
echo "<pre>" . json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";

// Test 3: Get teachers by department 2
echo "<h3>3. Test GET teachers by department 2</h3>";
$url = "http://localhost/DoAn/admin/teachers.php?page=1&limit=100&search=&department_id=2";
$response = file_get_contents($url);
$data = json_decode($response, true);
echo "<pre>" . json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";

echo "<h3>Test completed!</h3>";
?> 