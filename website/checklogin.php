<?php 
if ($_SERVER['REQUEST_METHOD'] == 'POST' && empty($_POST))
    $_POST = json_decode(file_get_contents('php://input'), true);

require_once("DatabaseScript.php");
$userid = $_POST["Code"];
$password = $_POST["Password"];
$sql = "SELECT * FROM users WHERE user_id = '$userid' AND password = '$password'";
$result = mysqli_query($link, $sql) or die("Fout bij uitvoeren query");

if (mysqli_num_rows($result)==1) 
{ 
	print "valid";
}
else
{
	print "invalid";
}?> 