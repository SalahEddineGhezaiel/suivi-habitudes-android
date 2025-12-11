<?php
include 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    
    if (empty($_POST['habit_id'])) {
        echo json_encode(["success" => false, "message" => "ID d'habitude requis."]);
        exit();
    }
    
    $habit_id = $_POST['habit_id'];
    $title = $_POST['title'];
    $description = $_POST['description'];
    $category = $_POST['category'];
    
    $monday = isset($_POST['monday']) && ($_POST['monday'] === 'true' || $_POST['monday'] === true) ? 1 : 0;
    $tuesday = isset($_POST['tuesday']) && ($_POST['tuesday'] === 'true' || $_POST['tuesday'] === true) ? 1 : 0;
    $wednesday = isset($_POST['wednesday']) && ($_POST['wednesday'] === 'true' || $_POST['wednesday'] === true) ? 1 : 0;
    $thursday = isset($_POST['thursday']) && ($_POST['thursday'] === 'true' || $_POST['thursday'] === true) ? 1 : 0;
    $friday = isset($_POST['friday']) && ($_POST['friday'] === 'true' || $_POST['friday'] === true) ? 1 : 0;
    $saturday = isset($_POST['saturday']) && ($_POST['saturday'] === 'true' || $_POST['saturday'] === true) ? 1 : 0;
    $sunday = isset($_POST['sunday']) && ($_POST['sunday'] === 'true' || $_POST['sunday'] === true) ? 1 : 0;
    
    if (!$monday && !$tuesday && !$wednesday && !$thursday && !$friday && !$saturday && !$sunday) {
        echo json_encode(["success" => false, "message" => "Veuillez sélectionner au moins un jour pour l'habitude."]);
        exit();
    }

    $stmt = $conn->prepare("UPDATE habits SET title = ?, description = ?, category = ?, monday = ?, tuesday = ?, wednesday = ?, thursday = ?, friday = ?, saturday = ?, sunday = ? WHERE id = ?");
    $stmt->bind_param("sssiiiiiiii", $title, $description, $category, $monday, $tuesday, $wednesday, $thursday, $friday, $saturday, $sunday, $habit_id);

    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            echo json_encode(["success" => true, "message" => "Habitude mise à jour avec succès."]);
        } else {
            echo json_encode(["success" => false, "message" => "Aucun changement effectué ou habitude non trouvée."]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "Erreur lors de la mise à jour de l'habitude: " . $stmt->error]);
    }

    $stmt->close();
} else {
    echo json_encode(["success" => false, "message" => "Méthode de requête invalide."]);
}

$conn->close();
?>
