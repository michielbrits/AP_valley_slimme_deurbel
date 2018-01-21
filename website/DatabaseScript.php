<?php
   $link = mysqli_connect("localhost","root","ciscorouter","AP_valley") or die("Verbinding mislukt");
   mysqli_select_db($link, "AP_valley") or die ("lukt niet");
?>