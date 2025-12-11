<?php
include 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    
    $required_fields = ['user_id', 'habit_id', 'is_completed'];
    foreach ($required_fields as $field) {
        if (!isset($_POST[$field])) {
            echo json_encode(["success" => false, "message" => "Champ requis manquant: $field"]);
            exit();
        }
    }

    $user_id = $_POST['user_id'];
    $habit_id = $_POST['habit_id'];
    $is_completed = isset($_POST['is_completed']) && ($_POST['is_completed'] === 'true' || $_POST['is_completed'] === true) ? 1 : 0;
    $today = date('Y-m-d');

    try {
        $conn->begin_transaction();

        $day_column = strtolower(date('l'));
        $habit_stmt = $conn->prepare("SELECT id, $day_column as is_today_scheduled, total_completions FROM habits WHERE id = ? AND user_id = ?");
        $habit_stmt->bind_param("ii", $habit_id, $user_id);
        $habit_stmt->execute();
        $habit_result = $habit_stmt->get_result();
        $habit = $habit_result->fetch_assoc();
        $habit_stmt->close();

        if (!$habit) {
            echo json_encode(["success" => false, "message" => "Habitude non trouvée."]);
            exit();
        }

        if (!$habit['is_today_scheduled']) {
            echo json_encode(["success" => false, "message" => "Cette habitude n'est pas programmée pour aujourd'hui."]);
            exit();
        }

        $current_date = date('Y-m-d');
        if ($today !== $current_date) {
            echo json_encode(["success" => false, "message" => "Seules les habitudes d'aujourd'hui peuvent être modifiées."]);
            exit();
        }

        $progress_stmt = $conn->prepare("SELECT id, is_completed FROM habit_progress WHERE habit_id = ? AND user_id = ? AND log_date = ?");
        $progress_stmt->bind_param("iis", $habit_id, $user_id, $today);
        $progress_stmt->execute();
        $progress_result = $progress_stmt->get_result();
        $existing_progress = $progress_result->fetch_assoc();
        $progress_stmt->close();

        $was_completed_before = $existing_progress ? $existing_progress['is_completed'] : false;

        if ($existing_progress) {
            $update_stmt = $conn->prepare("UPDATE habit_progress SET is_completed = ?, completed_at = ? WHERE id = ?");
            $completed_at = $is_completed ? date('Y-m-d H:i:s') : null;
            $update_stmt->bind_param("isi", $is_completed, $completed_at, $existing_progress['id']);
            $update_stmt->execute();
            $update_stmt->close();
        } else {
            $insert_stmt = $conn->prepare("INSERT INTO habit_progress (habit_id, user_id, log_date, is_completed, completed_at) VALUES (?, ?, ?, ?, ?)");
            $completed_at = $is_completed ? date('Y-m-d H:i:s') : null;
            $insert_stmt->bind_param("iisis", $habit_id, $user_id, $today, $is_completed, $completed_at);
            $insert_stmt->execute();
            $insert_stmt->close();
        }

        $total_completions = $habit['total_completions'];
        
        if ($is_completed && !$was_completed_before) {
            $total_completions++;
            $completion_stmt = $conn->prepare("UPDATE habits SET total_completions = ? WHERE id = ?");
            $completion_stmt->bind_param("ii", $total_completions, $habit_id);
            $completion_stmt->execute();
            $completion_stmt->close();
        } elseif (!$is_completed && $was_completed_before) {
            $total_completions = max(0, $total_completions - 1);
            $completion_stmt = $conn->prepare("UPDATE habits SET total_completions = ? WHERE id = ?");
            $completion_stmt->bind_param("ii", $total_completions, $habit_id);
            $completion_stmt->execute();
            $completion_stmt->close();
        }

        $streak = calculateStreak($conn, $habit_id, $user_id);

        $conn->commit();

        echo json_encode([
            "success" => true, 
            "message" => $is_completed ? "Habitude marquée comme terminée!" : "Habitude démarquée.",
            "total_completions" => $total_completions,
            "current_streak" => $streak
        ]);

    } catch (Exception $e) {
        $conn->rollback();
        echo json_encode(["success" => false, "message" => "Erreur de base de données: " . $e->getMessage()]);
    }

} else {
    echo json_encode(["success" => false, "message" => "Méthode de requête invalide."]);
}

function calculateStreak($conn, $habit_id, $user_id) {
    $streak = 0;
    $current_date = date('Y-m-d');
    
    $habit_stmt = $conn->prepare("SELECT monday, tuesday, wednesday, thursday, friday, saturday, sunday FROM habits WHERE id = ?");
    $habit_stmt->bind_param("i", $habit_id);
    $habit_stmt->execute();
    $habit_result = $habit_stmt->get_result();
    $habit_days = $habit_result->fetch_assoc();
    $habit_stmt->close();
    
    if (!$habit_days) return 0;
    
    $scheduled_days = [];
    foreach (['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'] as $day) {
        if ($habit_days[$day]) {
            $scheduled_days[] = $day;
        }
    }
    
    $check_date = $current_date;
    $max_days_to_check = 365;
    $days_checked = 0;
    
    while ($days_checked < $max_days_to_check) {
        $day_name = strtolower(date('l', strtotime($check_date)));
        
        if (in_array($day_name, $scheduled_days)) {
            $progress_stmt = $conn->prepare("SELECT is_completed FROM habit_progress WHERE habit_id = ? AND user_id = ? AND log_date = ?");
            $progress_stmt->bind_param("iis", $habit_id, $user_id, $check_date);
            $progress_stmt->execute();
            $progress_result = $progress_stmt->get_result();
            $progress = $progress_result->fetch_assoc();
            $progress_stmt->close();
            
            if ($progress && $progress['is_completed']) {
                $streak++;
            } else if ($check_date == $current_date) {
            } else {
                break;
            }
        }
        
        $check_date = date('Y-m-d', strtotime($check_date . ' -1 day'));
        $days_checked++;
    }
    
    return $streak;
}

$conn->close();
?>