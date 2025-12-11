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
    if (ob_get_level()) ob_end_clean();
    echo json_encode(['success' => false, 'message' => 'Méthode de requête invalide']);
    exit;
}

$user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : 0;

if (!$user_id) {
    if (ob_get_level()) ob_end_clean();
    echo json_encode(['success' => false, 'message' => 'ID utilisateur requis']);
    exit;
}

try {
    $pdo = new PDO("mysql:host=$servername;port=$port;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $pdo->beginTransaction();
    
    $progressStmt = $pdo->prepare("DELETE FROM habit_progress WHERE user_id = ?");
    $progressResult = $progressStmt->execute([$user_id]);
    $progressDeleted = $progressStmt->rowCount();
    
    $habitsStmt = $pdo->prepare("DELETE FROM habits WHERE user_id = ?");
    $habitsResult = $habitsStmt->execute([$user_id]);
    $habitsDeleted = $habitsStmt->rowCount();
    
    $pdo->commit();
    
    if (ob_get_level()) ob_end_clean();
    echo json_encode([
        'success' => true, 
        'message' => "Suppression terminée: $habitsDeleted habitudes et $progressDeleted entrées de progrès supprimées"
    ]);
    exit;
    
} catch(PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollback();
    }
    
    if (ob_get_level()) ob_end_clean();
    echo json_encode(['success' => false, 'message' => 'Erreur de base de données: ' . $e->getMessage()]);
    exit;
}
?>