<?php
error_reporting(0);
include_once("dbconnect.php");
$itemid = $_POST['Itemid'];
$userid = $_POST['Email'];
$quantity = $_POST['Quantity'];
$price = $_POST['Price'];
$itemname = $_POST['Itemname'];
$status = "Not Complete";

$sqlsel = "SELECT * FROM Item WHERE Itemid = '$itemid'";
$result = $conn->query($sqlsel);
if ($result->num_rows > 0) {
    while ($row = $result ->fetch_assoc()){
        $qavail = $row["Quantity"];
    }
    $bal = $qavail - $quantity; 
}

$sqlsel = "SELECT * FROM Item WHERE Itemid = '$itemid'";
$result = $conn->query($sqlsel);
if ($result->num_rows > 0) {
    while ($row = $result ->fetch_assoc()){
        $qavail = $row["Quantity"];
    }
    $bal = $qavail - $quantity; 
    if ($bal>0){
        $sqlupdate = "UPDATE Item SET Quantity = '$bal' WHERE Itemid = '$itemid'";
        $conn->query($sqlupdate);
        $sqlinsert = "INSERT INTO Cart(Itemid,Email,Quantity,Price,Itemname,Status) VALUES ('$itemid','$userid','$quantity','$price','$itemname','$status')";
        if ($conn->query($sqlinsert) === TRUE){
            echo $bal."Success";
        }else {
            echo "Failed";
        }
    }
}else{
    echo "Failed";
}



?>