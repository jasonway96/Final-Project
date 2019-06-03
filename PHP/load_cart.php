<?php
error_reporting(0);
include_once("dbconnect.php");
$Email = $_POST['Email'];

$sql = "SELECT * FROM Cart WHERE Email = '$Email'";
$result = $conn->query($sql);
if ($result->num_rows > 0) {
    $response["Cart"] = array();
    while ($row = $result ->fetch_assoc()){
        $cartlist = array();
        $cartlist[itemid] = $row["Itemid"];
        $cartlist[itemname] = $row["Itemname"];
        $cartlist[itemprice] = $row["Price"];
        $cartlist[quantity] = $row["Quantity"];
        $cartlist[status] = $row["Status"];
        array_push($response["Cart"], $cartlist);
    }
    echo json_encode($response);
}else{
    echo "nodata";
}
?>