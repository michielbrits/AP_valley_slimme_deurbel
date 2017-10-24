<?php 
if ($_SERVER['REQUEST_METHOD'] == 'POST' && empty($_POST))
    $_POST = json_decode(file_get_contents('php://input'), true);

require_once("DatabaseScript.php");
$Message = $_POST["Message"];
$DateTime = date("Y-m-d H:i:s");
$DeviceName = $_POST["DeviceName"];
$sql = "INSERT INTO data(DeviceName, Message, DateTime, IsEnabled) VALUES ('$DeviceName', '$Message', '$DateTime', 0)";
$result = mysqli_query($link, $sql) or die("Fout bij uitvoeren query");

?>