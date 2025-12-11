<?php
include 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    if (!isset($_GET['user_id']) || empty($_GET['user_id'])) {
        echo json_encode(["success" => false, "message" => "ID utilisateur requis."]);
        exit();
    }

    $user_id = $_GET['user_id'];
    $filter = isset($_GET['filter']) ? $_GET['filter'] : 'today';
    
    try {
        $user_stmt = $conn->prepare("SELECT id, name, email FROM users WHERE id = ?");
        $user_stmt->bind_param("i", $user_id);
        $user_stmt->execute();
        $user_result = $user_stmt->get_result();
        $user_info = $user_result->fetch_assoc();
        $user_stmt->close();

        if (!$user_info) {
            echo json_encode(["success" => false, "message" => "Utilisateur non trouvé."]);
            exit();
        }

        $today = date('Y-m-d');
        $day_column = strtolower(date('l'));
        
        if ($filter === 'all') {
            $sql = "SELECT id, title, description, category, start_date, created_at, total_completions, monday, tuesday, wednesday, thursday, friday, saturday, sunday FROM habits WHERE user_id = ? ORDER BY created_at DESC LIMIT 50";
        } else if ($filter === 'today') {
            $sql = "SELECT id, title, description, category, start_date, created_at, total_completions, monday, tuesday, wednesday, thursday, friday, saturday, sunday FROM habits WHERE user_id = ? AND `$day_column` = 1 ORDER BY created_at DESC LIMIT 50";
        } else {
            $valid_days = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];
            if (in_array($filter, $valid_days)) {
                $sql = "SELECT id, title, description, category, start_date, created_at, total_completions, monday, tuesday, wednesday, thursday, friday, saturday, sunday FROM habits WHERE user_id = ? AND `" . $filter . "` = 1 ORDER BY created_at DESC LIMIT 50";
            } else {
                $filter = 'today';
                $sql = "SELECT id, title, description, category, start_date, created_at, total_completions, monday, tuesday, wednesday, thursday, friday, saturday, sunday FROM habits WHERE user_id = ? AND `$day_column` = 1 ORDER BY created_at DESC LIMIT 50";
            }
        }
        $habits_stmt = $conn->prepare($sql);
        $habits_stmt->bind_param("i", $user_id);
        $habits_stmt->execute();
        $habits_result = $habits_stmt->get_result();

        $habits = [];
        while ($row = $habits_result->fetch_assoc()) {
            $row['monday'] = (bool)$row['monday'];
            $row['tuesday'] = (bool)$row['tuesday'];
            $row['wednesday'] = (bool)$row['wednesday'];
            $row['thursday'] = (bool)$row['thursday'];
            $row['friday'] = (bool)$row['friday'];
            $row['saturday'] = (bool)$row['saturday'];
            $row['sunday'] = (bool)$row['sunday'];
            
            $progress_stmt = $conn->prepare("SELECT is_completed FROM habit_progress WHERE habit_id = ? AND user_id = ? AND log_date = ?");
            $progress_stmt->bind_param("iis", $row['id'], $user_id, $today);
            $progress_stmt->execute();
            $progress_result = $progress_stmt->get_result();
            $progress = $progress_result->fetch_assoc();
            $progress_stmt->close();
            
            $row['is_completed_today'] = $progress ? (bool)$progress['is_completed'] : false;
            $row['current_streak'] = calculateDashboardStreak($conn, $row['id'], $user_id);
            
            $habits[] = $row;
        }
        $habits_stmt->close();

        
        $stats_stmt = $conn->prepare("SELECT COUNT(*) as total_habits FROM habits WHERE user_id = ?");
        $stats_stmt->bind_param("i", $user_id);
        $stats_stmt->execute();
        $stats_result = $stats_stmt->get_result();
        $stats = $stats_result->fetch_assoc();
        $stats_stmt->close();

        
        $category_stmt = $conn->prepare("SELECT category, COUNT(*) as count FROM habits WHERE user_id = ? GROUP BY category");
        $category_stmt->bind_param("i", $user_id);
        $category_stmt->execute();
        $category_result = $category_stmt->get_result();
        
        $categories = [];
        while ($row = $category_result->fetch_assoc()) {
            $categories[] = $row;
        }
        $category_stmt->close();

        
        $completed_today = 0;
        $total_due_today = count($habits);
        $longest_streak = 0;
        
        foreach ($habits as $habit) {
            if ($habit['is_completed_today']) {
                $completed_today++;
            }
            if ($habit['current_streak'] > $longest_streak) {
                $longest_streak = $habit['current_streak'];
            }
        }
        
        $completion_rate = $total_due_today > 0 ? round(($completed_today / $total_due_today) * 100, 1) : 0;

        
        $dashboard_data = [
            "success" => true,
            "user" => [
                "id" => (int)$user_info['id'],
                "name" => $user_info['name'],
                "email" => $user_info['email']
            ],
            "habits" => $habits,
            "statistics" => [
                "total_habits" => (int)$stats['total_habits'],
                "categories" => $categories,
                "habits_due_today" => $total_due_today
            ],
            "today_progress" => [
                "completed_today" => $completed_today,
                "total_due_today" => $total_due_today,
                "completion_rate" => $completion_rate
            ],
            "streaks" => [
                "longest_streak" => $longest_streak,
                "current_date" => $today,
                "day_of_week" => ucfirst($day_column)
            ]
        ];

        
        error_log("Dashboard response: " . json_encode($dashboard_data));
        echo json_encode($dashboard_data);

    } catch (Exception $e) {
        echo json_encode([
            "success" => false, 
            "message" => "Erreur de base de données: " . $e->getMessage()
        ]);
    }

} else {
    echo json_encode(["success" => false, "message" => "Méthode de requête invalide."]);
}


function calculateDashboardStreak($conn, $habit_id, $user_id) {
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
    $max_days_to_check = 100; 
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