<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST' && empty($_POST))
	$_POST = json_decode(file_get_contents('php://input'), true);

require_once("DatabaseScript.php");
$userid = $_POST["userid"];
$starttimestamp = $_POST["starttimestamp"];
$endtimestamp = $_POST["endtimestamp"];
$kindoffilter = $_POST["kindoffilter"];
//'2017-12-22 09:07:36'
$rows = array();
$test;
if ($kindoffilter == "day") 
{
	$sql = "SELECT extract(hour FROM timestamp) AS time, count(*) AS amount FROM timestamps WHERE timestamp > '$starttimestamp' AND timestamp < '$endtimestamp' group by extract(hour from timestamp)";
	$result = mysqli_query($link, $sql) or die("Fout bij uitvoeren query");
	while($r = mysqli_fetch_assoc($result)) 
	{
		$rows[] = $r; 
		//$rows[] = 0;
	}
	//$rows += array("7" => "5");
	array_push($rows, array("time" => "Mike", "amount" => "M"));
	$testarray = array();
foreach($rows as $k => $v)
{
	if ($v[$k].time == "12") 
	{
		array_push($rows, array("time111244545" => "Mike", "amount" => "M"));
	}
	array_push($testarray, array($v));

}
}
$test = array();
$test['time'] = "0";
$test['time'] = "1";
$test['time'] = "2";
$test['time'] = "3";
$test['time'] = "4";
$test['time'] = "5";
$test['time'] = "6";
$test['time'] = "7";
$test['time'] = "8";
$test['time'] = "9";
$test['time'] = "10";
$test['time'] = "11";
$test['time'] = "12";
$test['time'] = "13";
$test['time'] = "14";
$test['time'] = "15";
$test['time'] = "16";
$test['time'] = "17";
$test['time'] = "18";
$test['time'] = "19";
$test['time'] = "20";
$test['time'] = "21";
$test['time'] = "22";
$test['time'] = "23";


/*$sql = "SELECT timestamp FROM users, timestamps WHERE users.id = timestamps.userid AND timestamp < '$endtimestamp' AND timestamp > '$starttimestamp'";
$result = mysqli_query($link, $sql) or die("Fout bij uitvoeren query");
$rows = array();
while($r = mysqli_fetch_assoc($result)) 
{
    $rows[] = $r;
} */
print json_encode($testarray);
?>