<?php
include 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    
    $required_fields = ['user_id', 'title'];
    foreach ($required_fields as $field) {
        if (empty($_POST[$field])) {
            echo json_encode(["success" => false, "message" => "Champ requis manquant: $field"]);
            exit();
        }
    }

    $user_id = $_POST['user_id'];
    $title = $_POST['title'];
    $description = isset($_POST['description']) ? $_POST['description'] : '';
    $category = isset($_POST['category']) ? $_POST['category'] : 'General';
    $start_date = date('Y-m-d');
    
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


    $stmt = $conn->prepare("INSERT INTO habits (user_id, title, description, category, start_date, monday, tuesday, wednesday, thursday, friday, saturday, sunday, total_completions) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    $total_completions = 0;
    $stmt->bind_param("issssiiiiiiii", $user_id, $title, $description, $category, $start_date, $monday, $tuesday, $wednesday, $thursday, $friday, $saturday, $sunday, $total_completions);

    if ($stmt->execute()) {
        echo json_encode(["success" => true, "message" => "Habitude créée avec succès."]);
    } else {
        echo json_encode(["success" => false, "message" => "Erreur lors de la création de l'habitude: " . $stmt->error]);
    }

    $stmt->close();
} else {
    echo json_encode(["success" => false, "message" => "Méthode de requête invalide."]);
}

$conn->close();
?>
