<?php
include 'db_config.php';


if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    
    $name = isset($_POST['name']) ? $_POST['name'] : '';
    $email = isset($_POST['email']) ? $_POST['email'] : '';
    $password = isset($_POST['password']) ? $_POST['password'] : '';

    
    if (empty($name) || empty($email) || empty($password)) {
        echo json_encode(["success" => false, "message" => "Nom, email et mot de passe requis."]);
        exit();
    }

    
    $hashed_password = password_hash($password, PASSWORD_DEFAULT);

    
    $stmt = $conn->prepare("INSERT INTO users (name, email, password) VALUES (?, ?, ?)");
    $stmt->bind_param("sss", $name, $email, $hashed_password);

    
    if ($stmt->execute()) {
        echo json_encode(["success" => true, "message" => "Utilisateur enregistré avec succès."]);
    } else {
        
        if ($conn->errno == 1062) {
            echo json_encode(["success" => false, "message" => "Cet email est déjà enregistré."]);
        } else {
            echo json_encode(["success" => false, "message" => "Erreur: " . $stmt->error]);
        }
    }

    $stmt->close();
} else {
    echo json_encode(["success" => false, "message" => "Méthode de requête invalide."]);
}

$conn->close();
?>
