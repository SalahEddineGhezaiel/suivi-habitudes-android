<?php
include 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = isset($_POST['email']) ? $_POST['email'] : '';
    $password = isset($_POST['password']) ? $_POST['password'] : '';

    if (empty($email) || empty($password)) {
        echo json_encode(["success" => false, "message" => "Email et mot de passe requis."]);
        exit();
    }

    
    $stmt = $conn->prepare("SELECT id, name, password FROM users WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        $stmt->bind_result($id, $name, $hashed_password);
        $stmt->fetch();

        
        if (password_verify($password, $hashed_password)) {
            $user = ["id" => $id, "name" => $name, "email" => $email];
            echo json_encode(["success" => true, "message" => "Connexion réussie.", "user" => $user]);
        } else {
            echo json_encode(["success" => false, "message" => "Identifiants invalides."]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "Identifiants invalides."]);
    }

    $stmt->close();
} else {
    echo json_encode(["success" => false, "message" => "Méthode de requête invalide."]);
}

$conn->close();
?>
