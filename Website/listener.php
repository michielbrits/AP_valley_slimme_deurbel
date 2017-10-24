<?php 

$checkbox = $_POST["checkbox"];
require_once("DatabaseScript.php");

$sql = "UPDATE data SET IsEnabled = '$checkbox' WHERE DeviceName = 'node3'";
$result = mysqli_query($link, $sql) or die("Fout bij uitvoeren query");
header("Location: http://michielserver.com/IOT");
die();

?>