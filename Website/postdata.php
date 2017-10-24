<?php

$DeviceName = $_POST["DeviceName"];
$Message = $_POST["Message"];
$DateTime = date("Y-m-d H:i:s");
require_once("DatabaseScript.php");


$sql = "UPDATE data SET Message = '$Message', DateTime = '$DateTime' WHERE DeviceName = '$DeviceName'";
$result = mysqli_query($link, $sql) or die("Fout bij uitvoeren query");

 $sql = "SELECT IsEnabled FROM data WHERE DeviceName = '$DeviceName'";
      $result = mysqli_query($link, $sql);

while($row = mysqli_fetch_array($result)) 
        {
        	if ($row['IsEnabled'] == 1) 
            {
            	echo "Almighty Michiel pressed a button, receiving not possible anymore hahaha";

            }
            else
            {
            	echo "We have updated your message in our database. We received " . "'" . $Message . "'" . " from " . $DeviceName . " on " . $DateTime . " thank you for posting to us my friend";
            }
        }

?>