<?php
error_reporting(0);
include_once("dbconnect.php");
$itemid = $_POST['Itemid'];
$userid = $_POST['Email'];
$quantity = $_POST['Quantity'];
$price = $_POST['Itemprice'];
$itemname = $_POST['Itemname'];
$shopid = $_POST['Shopid'];
$status = "Not Paid";
    
$sqlsel = "SELECT * FROM Item WHERE Itemid = '$itemid'";
$result = $conn->query($sqlsel);
if ($result->num_rows > 0) {
    while ($row = $result ->fetch_assoc()){
        $qavail = $row["Quantity"];
    }
    $bal = $qavail - $quantity; 
}
if ($bal>0){
    $sqlgetid = "SELECT * FROM Cart WHERE Email = '$userid' AND Status='Not Paid'";
    $result = $conn->query($sqlgetid);
    $sqlupdate = "UPDATE Item SET Quantity = '$bal' WHERE Itemid = '$itemid'";
        $conn->query($sqlupdate);
        
if ($result->num_rows > 0) {
    while ($row = $result ->fetch_assoc()){
        $orderid = $row["Orderid"];
    }
     $sqlinsert = "INSERT INTO Cart(Itemid,Email,Quantity,Price,Itemname,Status,Shopid,Orderid) VALUES ('$itemid','$userid','$quantity','$price','$itemname','$status','$shopid','$orderid')";
     
    if ($conn->query($sqlinsert) === TRUE){
       echo "success";
    }
}else{
    $orderid = generateRandomString();
   $sqlinsert = "INSERT INTO Cart(Itemid,Email,Quantity,Price,Itemname,Status,Shopid,Orderid) VALUES ('$itemid','$userid','$quantity','$price','$itemname','$status','$shopid','$orderid')";
    if ($conn->query($sqlinsert) === TRUE){
       echo "success";
    }
}
}



function generateRandomString($length = 7) {
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $charactersLength = strlen($characters);
    $randomString = '';
    for ($i = 0; $i < $length; $i++) {
        $randomString .= $characters[rand(0, $charactersLength - 1)];
    }
    return date('dmY')."-".$randomString;
}

?>