<?php 
if ($_SERVER['REQUEST_METHOD'] == 'POST' && empty($_POST))
    $_POST = json_decode(file_get_contents('php://input'), true);
$id = $_POST["id"];

require_once("DatabaseScript.php");

$sql = "DELETE FROM data WHERE ID = '$id'";
$result = mysqli_query($link, $sql) or die("Fout bij uitvoeren query");
//header("Location: http://michielserver.com/IOT");
//die();

?>