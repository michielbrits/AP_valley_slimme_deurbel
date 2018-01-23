<?php

$header = array();
$header[] = 'Content-type: application/json';
$header[] = 'Authorization: key=' . 'AAAA1hb1tH8:APA91bEMn7MfXGM0oQE8bxJn88jSvmWnFZoiD_T4fbHUDD2Ecbhz-ds59T221YjG269CH5cSL75P4X-doep-EqBAFyhvDnhPCWX9nSM7IsBUAXBUTWabryqFzSuVzbx5dN53FOFpDcMj';

$payload = [
  'to' => '/topics/news',
  'notification' => [
    'title' => "Portugal VS Germany",
    'body' => "1 to 2"
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