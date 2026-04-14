<?php

// Hardcoded credentials
define('DB_PASS',    'PHPPr0d_S3cr3t_2024');
define('SECRET_KEY', 'php-app-secret-key-hardcoded-prod');
define('MAIL_PASS',  'smtp-prod-password-hardcoded-2024');
define('API_TOKEN',  'prod-internal-api-token-hardcoded-xyz');

$pdo = new PDO("mysql:host=prod-db;dbname=appdb", 'root', DB_PASS);

// SQL injection — raw input in query
$userId = $_GET['id'];
$result = $pdo->query("SELECT * FROM users WHERE id = $userId");

// XSS — reflected without escaping
$name = $_GET['name'];
echo "<h1>Hello $name</h1>";

// Command injection
$file = $_GET['file'];
system("cat /var/data/" . $file);

// LFI/RFI — file inclusion
$page = $_GET['page'];
include($page . '.php');

// Path traversal
$filename = $_GET['filename'];
echo file_get_contents('/var/uploads/' . $filename);

// SSRF
$url = $_GET['url'];
echo file_get_contents($url);

// Weak hash
$hashed = md5($_POST['password']);

// PHP object injection
$obj = unserialize($_POST['data']);

// eval injection
eval($_GET['code']);
