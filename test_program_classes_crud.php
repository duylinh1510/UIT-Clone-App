<?php
require "db_connect.php";

echo "<h2>Test Program Classes CRUD API</h2>";

// Test 1: Get program classes by department
echo "<h3>1. Test GET program classes by department</h3>";
$url = "http://localhost/DoAn/admin/program_classes.php?department_id=1";
$response = file_get_contents($url);
$data = json_decode($response, true);
echo "<pre>" . json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";

// Test 2: Create new program class
echo "<h3>2. Test CREATE program class</h3>";
$create_data = [
    'program_class_code' => 'TEST2024.1',
    'year' => '2024',
    'teacher_id' => 1,
    'department_id' => 1
];

$context = stream_context_create([
    'http' => [
        'method' => 'POST',
        'header' => 'Content-Type: application/json',
        'content' => json_encode($create_data)
    ]
]);

$create_response = file_get_contents("http://localhost/DoAn/admin/program_classes.php", false, $context);
$create_result = json_decode($create_response, true);
echo "<pre>" . json_encode($create_result, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";

// Get the created class ID for update/delete tests
if ($create_result['success']) {
    // Get the newly created class
    $sql = "SELECT program_class_id FROM program_class WHERE program_class_code = 'TEST2024.1'";
    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $new_class = $stmt->fetch(PDO::FETCH_ASSOC);
    $test_class_id = $new_class['program_class_id'];
    
    // Test 3: Update program class
    echo "<h3>3. Test UPDATE program class</h3>";
    $update_data = [
        'program_class_id' => $test_class_id,
        'program_class_code' => 'TEST2024.1_UPDATED',
        'year' => '2024',
        'teacher_id' => 2,
        'department_id' => 1
    ];
    
    $update_context = stream_context_create([
        'http' => [
            'method' => 'PUT',
            'header' => 'Content-Type: application/json',
            'content' => json_encode($update_data)
        ]
    ]);
    
    $update_response = file_get_contents("http://localhost/DoAn/admin/program_classes.php", false, $update_context);
    $update_result = json_decode($update_response, true);
    echo "<pre>" . json_encode($update_result, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";
    
    // Test 4: Delete program class
    echo "<h3>4. Test DELETE program class</h3>";
    $delete_context = stream_context_create([
        'http' => [
            'method' => 'DELETE'
        ]
    ]);
    
    $delete_response = file_get_contents("http://localhost/DoAn/admin/program_classes.php?program_class_id=" . $test_class_id, false, $delete_context);
    $delete_result = json_decode($delete_response, true);
    echo "<pre>" . json_encode($delete_result, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";
}

// Test 5: Get all program classes again to verify
echo "<h3>5. Test GET all program classes after operations</h3>";
$final_response = file_get_contents("http://localhost/DoAn/admin/program_classes.php?department_id=1");
$final_data = json_decode($final_response, true);
echo "<pre>" . json_encode($final_data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "</pre>";

echo "<h3>Test completed!</h3>";
?> 