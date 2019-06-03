<?php
error_reporting(0);
include_once("dbconnect.php");
$email = $_POST['Email'];
$password = sha1($_POST['Password']);

$sql = "SELECT * FROM Register WHERE Email = '$email' AND Password = '$password'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    while ($row = $result ->fetch_assoc()){
    echo $data = $row["Name"].",".$row["Phone"];
        
    }
}else{
    echo "failed";
}
?>