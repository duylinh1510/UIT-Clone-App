<?php
require "db_connect.php";

echo "<h2>Test Delete Department API</h2>";

// Test 1: Check current departments
echo "<h3>1. Current departments</h3>";
$url = "http://localhost/DoAn/admin/departments.php";
$response = file_get_contents($url);
$data = json_decode($response, true);
echo "<pre>" . json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";

// Test 2: Try to delete a department that has dependencies
echo "<h3>2. Try to delete department with dependencies (should fail)</h3>";
$delete_context = stream_context_create([
    'http' => [
        'method' => 'DELETE'
    ]
]);

$delete_response = file_get_contents("http://localhost/DoAn/admin/departments.php?department_id=1", false, $delete_context);
$delete_result = json_decode($delete_response, true);
echo "<pre>" . json_encode($delete_result, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";

// Test 3: Create a test department and then delete it
echo "<h3>3. Create test department</h3>";
$create_data = [
    'department_name' => 'Test Department for Delete',
    'department_code' => 'TEST_DEL'
];

$create_context = stream_context_create([
    'http' => [
        'method' => 'POST',
        'header' => 'Content-Type: application/json',
        'content' => json_encode($create_data)
    ]
]);

$create_response = file_get_contents("http://localhost/DoAn/admin/departments.php", false, $create_context);
$create_result = json_decode($create_response, true);
echo "<pre>" . json_encode($create_result, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";

if ($create_result['success']) {
    // Get the created department ID
    $sql = "SELECT department_id FROM department WHERE department_code = 'TEST_DEL'";
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $test_dept = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($test_dept) {
        $test_dept_id = $test_dept['department_id'];
        
        echo "<h3>4. Delete test department (should succeed)</h3>";
        $delete_test_response = file_get_contents("http://localhost/DoAn/admin/departments.php?department_id=" . $test_dept_id, false, $delete_context);
        $delete_test_result = json_decode($delete_test_response, true);
        echo "<pre>" . json_encode($delete_test_result, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";
    }
}

// Test 4: Check departments after deletion
echo "<h3>5. Departments after test</h3>";
$final_response = file_get_contents("http://localhost/DoAn/admin/departments.php");
$final_data = json_decode($final_response, true);
echo "<pre>" . json_encode($final_data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";

echo "<h3>Test completed!</h3>";
?> 