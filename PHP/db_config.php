<?php

$servername = "localhost";
$username = "root"; 
$password = "";    
$dbname = "suivi_habitudes";
$port = "3307"; 

$conn = new mysqli($servername, $username, $password, $dbname, $port);

if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Échec de la connexion: " . $conn->connect_error]));
}
?>
