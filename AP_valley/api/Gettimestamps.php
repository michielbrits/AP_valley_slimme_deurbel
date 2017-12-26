<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST' && empty($_POST))
    $_POST = json_decode(file_get_contents('php://input'), true);

require_once("DatabaseScript.php");
$userid = $_POST["userid"];
$starttimestamp = $_POST["starttimestamp"];
$endtimestamp = $_POST["endtimestamp"];
//'2017-12-22 09:07:36'

$sql = "SELECT timestamp FROM users, timestamps WHERE users.id = timestamps.userid AND timestamp < '$endtimestamp' AND timestamp > '$starttimestamp'";
$result = mysqli_query($link, $sql) or die("Fout bij uitvoeren query");
$rows = array();
while($r = mysqli_fetch_assoc($result)) 
{
    $rows[] = $r;
}
print json_encode($rows)

 ?>