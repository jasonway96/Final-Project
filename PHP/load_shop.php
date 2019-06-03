<?php
error_reporting(0);
include_once("dbconnect.php");
$Location = $_POST['Location'];
if (strcasecmp($Location, "All") == 0){
    $sql = "SELECT * FROM Shop"; 
}else{
    $sql = "SELECT * FROM Shop WHERE Location = '$Location'";
}
$result = $conn->query($sql);
if ($result->num_rows > 0) {
    $response["Shop"] = array();
    while ($row = $result ->fetch_assoc()){
        $Shoplist = array();
        $Shoplist[Shopid] = $row["Shopid"];
        $Shoplist[Name] = $row["Name"];
        $Shoplist[Phone] = $row["Phone"];
        $Shoplist[Location] = $row["Location"];
        array_push($response["Shop"], $Shoplist);
    }
    echo json_encode($response);
}else{
    echo "nodata";
}
?>