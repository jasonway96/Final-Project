<?php
error_reporting(0);
include_once("dbconnect.php");
$Shopid = $_POST['Shopid'];

$sql = "SELECT * FROM Item WHERE Shopid = '$Shopid'";
$result = $conn->query($sql);
if ($result->num_rows > 0) {
    $response["Item"] = array();
    while ($row = $result ->fetch_assoc()){
        $Itemlist = array();
        $Itemlist[Itemid] = $row["Itemid"];
        $Itemlist[Itemname] = $row["Itemname"];
        $Itemlist[Itemprice] = $row["Itemprice"];
        $Itemlist[Quantity] = $row["Quantity"];
        array_push($response["Item"], $Itemlist);
    }
    echo json_encode($response);
}else{
    echo "nodata";
}
?>