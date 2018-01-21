<?php 
if ($_SERVER['REQUEST_METHOD'] == 'POST' && empty($_POST))
    $_POST = json_decode(file_get_contents('php://input'), true);

require_once("DatabaseScript.php");
$user_id = $_POST["userid"];
$firstname = $_POST["firstname"];
$lastname = $_POST["lastname"];
$registertime = date("Y-m-d H:i:s");
$sql = "INSERT INTO users(user_id, password, firstname, lastname, registered) VALUES ('$user_id', '$user_id', '$firstname', '$lastname', '$registertime')";
$result = mysqli_query($link, $sql) or die("Fout bij uitvoeren query");

?>