<?php
if (ob_get_level()) {
    ob_end_clean();
}
ob_start();

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

require_once 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    ob_end_clean();
    echo json_encode(['success' => false, 'message' => 'Méthode de requête invalide']);
    exit;
}

$user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : 0;
$email = isset($_POST['email']) ? trim($_POST['email']) : '';
$current_password = isset($_POST['current_password']) ? $_POST['current_password'] : '';
$new_password = isset($_POST['new_password']) ? $_POST['new_password'] : '';

if (!$user_id) {
    ob_end_clean();
    echo json_encode(['success' => false, 'message' => 'ID utilisateur requis']);
    exit;
}

if (empty($current_password)) {
    echo json_encode(['success' => false, 'message' => 'Mot de passe actuel requis']);
    exit;
}

if (empty($new_password)) {
    echo json_encode(['success' => false, 'message' => 'Nouveau mot de passe requis']);
    exit;
}

if (strlen($new_password) < 6) {
    if (ob_get_level()) ob_end_clean();
    echo json_encode(['success' => false, 'message' => 'Le nouveau mot de passe doit contenir au moins 6 caractères']);
    exit;
}

try {
    $pdo = new PDO("mysql:host=$host;port=$port;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $stmt = $pdo->prepare("SELECT id, email, password FROM users WHERE id = ?");
    $stmt->execute([$user_id]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$user) {
        if (ob_get_level()) ob_end_clean();
        echo json_encode(['success' => false, 'message' => 'Utilisateur non trouvé']);
        exit;
    }
    
    if (!password_verify($current_password, $user['password'])) {
        if (ob_get_level()) ob_end_clean();
        echo json_encode(['success' => false, 'message' => 'Mot de passe actuel incorrect']);
        exit;
    }
    
    $new_password_hash = password_hash($new_password, PASSWORD_DEFAULT);
    
    $updateStmt = $pdo->prepare("UPDATE users SET password = ? WHERE id = ?");
    $result = $updateStmt->execute([$new_password_hash, $user_id]);
    
    if ($result && $updateStmt->rowCount() > 0) {
        if (ob_get_level()) ob_end_clean();
        echo json_encode([
            'success' => true, 
            'message' => 'Mot de passe changé avec succès'
        ]);
        exit;
    } else {
        if (ob_get_level()) ob_end_clean();
        echo json_encode(['success' => false, 'message' => 'Erreur lors de la mise à jour du mot de passe']);
        exit;
    }
    
} catch(PDOException $e) {
    if (ob_get_level()) ob_end_clean();
    echo json_encode(['success' => false, 'message' => 'Erreur de base de données: ' . $e->getMessage()]);
    exit;
}
?>