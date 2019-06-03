<?php
error_reporting(0);
include_once("dbconnect.php");
$Email = $_POST['Email'];
$sql = "SELECT * FROM Register WHERE Email = '$Email'";
$result = $conn->query($sql);
if ($result->num_rows > 0) {
    $response["User"] = array();
    while ($row = $result ->fetch_assoc()){
        $userarray = array();
        $userarray[Email] = $row["Email"];
        $userarray[Phone] = $row["Phone"];
        $userarray[Name] = $row["Name"];
         array_push($response["User"], $userarray);
    }
    echo json_encode($response);
}

?>