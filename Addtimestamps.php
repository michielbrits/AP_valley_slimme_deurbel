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

  $entryData = array(
        'username' => $userid
      , 'title'    => $time
      , 'article'  => 'kittensCategory'
      , 'when'     => time()
    );



    // This is our new stuff
    $context = new ZMQContext();
    $socket = $context->getSocket(ZMQ::SOCKET_PUSH, 'my pusher');
    $socket->connect("tcp://localhost:5555");

    $socket->send(json_encode($entryData));



?>