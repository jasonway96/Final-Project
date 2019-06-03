<?php
error_reporting(0);
include_once("dbconnect.php");
$userid = $_POST['userid'];
$foodid = $_POST['itemid'];
    $sqldelete = "DELETE FROM CART WHERE USERID = '$userid' AND ITEMID='$itemid'";
    if ($conn->query($sqldelete) === TRUE){
       echo "success";
    }else {
        echo "failed";
    }
?>