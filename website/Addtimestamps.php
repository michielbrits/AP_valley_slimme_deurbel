<?php 
//phpinfo();
if ($_SERVER['REQUEST_METHOD'] == 'POST' && empty($_POST))
    $_POST = json_decode(file_get_contents('php://input'), true);

require_once("DatabaseScript.php");

if (isset($_POST['userid'])) {
 $userid = $_POST["userid"];
}
else
{
 $userid = "AAA000";
}
$time = date("Y-m-d H:i:s");
$id = 0;

$sql = "SELECT * FROM users WHERE user_id = '$userid'";
$result = mysqli_query($link, $sql) or die("Fout bij uitvoeren query");

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $id = $row["id"];
    }
}

$sql = "INSERT INTO timestamps(userid, timestamp) VALUES ('$id', '$time')";
$result = mysqli_query($link, $sql) or die("Fout bij uitvoeren query");

$header = array();
$header[] = 'Content-type: application/json';
//$header[] = 'Authorization: key=' . 'AAAA1hb1tH8:APA91bEMn7MfXGM0oQE8bxJn88jSvmWnFZoiD_T4fbHUDD2Ecbhz-ds59T221YjG269CH5cSL75P4X-doep-EqBAFyhvDnhPCWX9nSM7IsBUAXBUTWabryqFzSuVzbx5dN53FOFpDcMj';
$header[] = 'Authorization: key=' . 'AAAAP5JDUjM:APA91bGY3bykL1T-pq88WLJihBxx4LZouEh_-BJ61XlbJ3D5G6Ra_HBw_6eFgohAFYJ3XzOS0ajQKtVgFcbJsC-NPxz2gXFHF6NN1aPgGYpwnQ9T2ybytDGGe_wEu2r-AayZuNYYn7GE';

$payload = [
  'to' => '/topics/'.$userid,
  'data' => [
    'title' => "Someone rang your doorbell!",
    'text' => "text",
    'line1' => "Journal",
    'sound' => "default",
    'body' => $time
  ]
];

$crl = curl_init();
curl_setopt($crl, CURLOPT_HTTPHEADER, $header);
curl_setopt($crl, CURLOPT_POST,true);
    curl_setopt($crl, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
curl_setopt($crl, CURLOPT_POSTFIELDS, json_encode( $payload ) );

curl_setopt($crl, CURLOPT_RETURNTRANSFER, true );
// curl_setopt($crl, CURLOPT_SSL_VERIFYHOST, false); should be off on production
// curl_setopt($crl, CURLOPT_SSL_VERIFYPEER, false); shoule be off on production

$rest = curl_exec($crl);
if ($rest === false) {
    return curl_error($crl);
}
curl_close($crl);
return $rest;




?>