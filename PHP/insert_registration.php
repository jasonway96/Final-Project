<?php
error_reporting(0);
include_once("dbconnect.php");
$email =$_POST["Email"];
$name =$_POST["Name"];
$password =$_POST["Password"];
$passwordsha = sha1($password);
$phone = $_POST["Phone"];

if (strlen($email) > 0){
    $sql = "INSERT INTO Register(Email,Name, Password, Phone) 
    VALUES ('$email','$name','$passwordsha','$phone')";
    
    if ($conn->query($sql) === TRUE){
       echo "success";
    }else {
        echo "failed";
    }}
    else{
        echo"No Data";
    }

?>