<?php
error_reporting(0);
include_once("dbconnect.php");
$Email = $_POST['Email'];
$Oldpassword = sha1($_POST['Opassword']);
$Newpassword = sha1($_POST['Npassword']);
$Phone = $_POST['Phone'];
$Name = $_POST['Name'];

$sqlcheck = "SELECT * FROM Register WHERE Phone = '$Phone' AND Password = '$Oldpassword'";
$result = $conn->query($sqlcheck);
if ($result->num_rows > 0) {
 $sqlupdate = "UPDATE Register SET Email = '$Email', Password = '$Newpassword', Name = '$Name' WHERE Phone = '$Phone' AND Password = '$Oldpassword'";
  if ($conn->query($sqlupdate) === TRUE){
        echo 'success';
  }else{
      echo 'failed';
  }   
}else{
    echo "failed";
}

 
?>