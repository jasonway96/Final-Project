<?php
$servername = "localhost";
$username   = "socstude";
$password   = "123soc123uum123";
$dbname     = "socstude_yourwaystechnology";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
?>