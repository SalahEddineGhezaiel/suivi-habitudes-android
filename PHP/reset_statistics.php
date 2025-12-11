<?php
include 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : 0;

    if ($user_id <= 0) {
        echo json_encode(["success" => false, "message" => "ID utilisateur invalide."]);
        exit();
    }

    try {
        
        $conn->autocommit(false);
        
        
        $delete_progress_stmt = $conn->prepare("
            DELETE hp FROM habit_progress hp 
            INNER JOIN habits h ON hp.habit_id = h.id 
            WHERE h.user_id = ?
        ");
        $delete_progress_stmt->bind_param("i", $user_id);
        
        if (!$delete_progress_stmt->execute()) {
            throw new Exception("Erreur lors de la suppression des données de progression.");
        }
        
        $deleted_progress_count = $delete_progress_stmt->affected_rows;
        $delete_progress_stmt->close();
        
        
        $reset_totals_stmt = $conn->prepare("
            UPDATE habits 
            SET total_completions = 0 
            WHERE user_id = ?
        ");
        $reset_totals_stmt->bind_param("i", $user_id);
        
        if (!$reset_totals_stmt->execute()) {
            throw new Exception("Erreur lors de la réinitialisation des compteurs.");
        }
        
        $updated_habits_count = $reset_totals_stmt->affected_rows;
        $reset_totals_stmt->close();
        
        
        $conn->commit();
        
        echo json_encode([
            "success" => true,
            "message" => "Statistiques réinitialisées avec succès.",
            "deleted_progress_entries" => $deleted_progress_count,
            "reset_habits_count" => $updated_habits_count
        ]);

    } catch (Exception $e) {
        
        $conn->rollback();
        echo json_encode([
            "success" => false, 
            "message" => "Erreur lors de la réinitialisation: " . $e->getMessage()
        ]);
    }
    
    
    $conn->autocommit(true);
    
} else {
    echo json_encode(["success" => false, "message" => "Méthode de requête invalide."]);
}

$conn->close();
?>