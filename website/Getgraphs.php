<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST' && empty($_POST))
    $_POST = json_decode(file_get_contents('php://input'), true);

require_once("DatabaseScript.php");
$userid = $_POST["userid"];
$sql = "SELECT * FROM timestamps, users WHERE users.id = timestamps.userid AND timestamp > DATE_SUB( NOW(), INTERVAL 7 DAY)";
$result = mysqli_query($link, $sql) or die("Fout bij uitvoeren query");
$rows = array();
while($r = mysqli_fetch_assoc($result)) 
{
    $rows[] = $r;
}
print json_encode($rows)

?>