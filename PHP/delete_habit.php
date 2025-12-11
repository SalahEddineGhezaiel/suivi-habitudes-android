<?php
include 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (empty($_POST['habit_id'])) {
        echo json_encode(["success" => false, "message" => "ID d'habitude requis."]);
        exit();
    }

    $habit_id = $_POST['habit_id'];

    
    $stmt = $conn->prepare("DELETE FROM habits WHERE id = ?");
    $stmt->bind_param("i", $habit_id);

    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            echo json_encode(["success" => true, "message" => "Habitude supprimée avec succès."]);
        } else {
            echo json_encode(["success" => false, "message" => "Habitude non trouvée."]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "Erreur lors de la suppression de l'habitude: " . $stmt->error]);
    }

    $stmt->close();
} else {
    echo json_encode(["success" => false, "message" => "Méthode de requête invalide."]);
}

$conn->close();
?>
