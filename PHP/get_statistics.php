<?php
include 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : 0;

    if ($user_id <= 0) {
        echo json_encode(["success" => false, "message" => "ID utilisateur invalide."]);
        exit();
    }

    try {
        
        $today = date('Y-m-d');
        $current_day = date('w'); 
        $current_day_name = ['sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday'][$current_day];
        
        
        $start_of_week = date('Y-m-d', strtotime('monday this week'));
        $end_of_week = date('Y-m-d', strtotime('sunday this week'));

        
        $stmt = $conn->prepare("SELECT COUNT(*) as total_habits FROM habits WHERE user_id = ?");
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $result = $stmt->get_result();
        $total_habits = $result->fetch_assoc()['total_habits'];
        $stmt->close();

        
        $stmt = $conn->prepare("SELECT COUNT(*) as active_today FROM habits WHERE user_id = ? AND $current_day_name = 1");
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $result = $stmt->get_result();
        $active_today = $result->fetch_assoc()['active_today'];
        $stmt->close();

        
        $longest_streak = 0;
        
        
        $stmt = $conn->prepare("SELECT id, monday, tuesday, wednesday, thursday, friday, saturday, sunday FROM habits WHERE user_id = ?");
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $habits_result = $stmt->get_result();
        
        while ($habit = $habits_result->fetch_assoc()) {
            $habit_id = $habit['id'];
            
            
            $scheduled_days = [];
            $days = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];
            foreach ($days as $day) {
                if ($habit[$day] == 1) {
                    $scheduled_days[] = $day;
                }
            }
            
            if (empty($scheduled_days)) continue;
            
            
            $current_streak = 0;
            $check_date = new DateTime($today);
            $found_incomplete_today = false;
            
            
            while (true) {
                $check_date_str = $check_date->format('Y-m-d');
                $day_of_week = $check_date->format('w');
                $day_name = $days[($day_of_week + 6) % 7]; 
                
                
                if (!in_array($day_name, $scheduled_days)) {
                    $check_date->modify('-1 day');
                    continue;
                }
                
                
                $progress_stmt = $conn->prepare("SELECT is_completed FROM habit_progress WHERE habit_id = ? AND log_date = ?");
                $progress_stmt->bind_param("is", $habit_id, $check_date_str);
                $progress_stmt->execute();
                $progress_result = $progress_stmt->get_result();
                
                if ($progress_result->num_rows > 0) {
                    $progress = $progress_result->fetch_assoc();
                    if ($progress['is_completed'] == 1) {
                        $current_streak++;
                    } else {
                        
                        if ($check_date_str == $today) {
                            
                            $found_incomplete_today = true;
                        } else {
                            
                            break;
                        }
                    }
                } else {
                    
                    if ($check_date_str == $today) {
                        
                    } else {
                        
                        break;
                    }
                }
                
                $progress_stmt->close();
                $check_date->modify('-1 day');
                
                
                if ($current_streak > 365) break;
            }
            
            $longest_streak = max($longest_streak, $current_streak);
        }
        $stmt->close();

        
        $stmt = $conn->prepare("SELECT SUM(total_completions) as total_completions FROM habits WHERE user_id = ?");
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $result = $stmt->get_result();
        $total_completions = $result->fetch_assoc()['total_completions'] ?: 0;
        $stmt->close();

        
        $week_completed = 0;
        $week_total = 0;
        
        
        $stmt = $conn->prepare("SELECT id, monday, tuesday, wednesday, thursday, friday, saturday, sunday FROM habits WHERE user_id = ?");
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $habits_result = $stmt->get_result();
        
        while ($habit = $habits_result->fetch_assoc()) {
            $habit_id = $habit['id'];
            
            
            $current_date = new DateTime($start_of_week);
            $end_date = new DateTime($end_of_week);
            
            while ($current_date <= $end_date) {
                $date_str = $current_date->format('Y-m-d');
                $day_of_week = $current_date->format('w');
                $day_name = $days[($day_of_week + 6) % 7]; 
                
                
                if ($habit[$day_name] == 1) {
                    $week_total++;
                    
                    
                    $progress_stmt = $conn->prepare("SELECT is_completed FROM habit_progress WHERE habit_id = ? AND log_date = ?");
                    $progress_stmt->bind_param("is", $habit_id, $date_str);
                    $progress_stmt->execute();
                    $progress_result = $progress_stmt->get_result();
                    
                    if ($progress_result->num_rows > 0) {
                        $progress = $progress_result->fetch_assoc();
                        if ($progress['is_completed'] == 1) {
                            $week_completed++;
                        }
                    }
                    $progress_stmt->close();
                }
                
                $current_date->modify('+1 day');
            }
        }
        $stmt->close();

        
        $categories = [];
        
        
        $stmt = $conn->prepare("
            SELECT category, COUNT(*) as total_habits
            FROM habits 
            WHERE user_id = ? 
            GROUP BY category
        ");
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $categories_result = $stmt->get_result();
        
        while ($category_data = $categories_result->fetch_assoc()) {
            $category_name = $category_data['category'];
            $total_habits_in_category = $category_data['total_habits'];
            
            
            $completed_count = 0;
            $total_count = 0;
            
            
            $habit_stmt = $conn->prepare("
                SELECT h.id, h.monday, h.tuesday, h.wednesday, h.thursday, h.friday, h.saturday, h.sunday,
                       COUNT(hp.id) as total_scheduled,
                       SUM(hp.is_completed) as total_completed
                FROM habits h
                LEFT JOIN habit_progress hp ON h.id = hp.habit_id
                WHERE h.user_id = ? AND h.category = ?
                GROUP BY h.id
            ");
            $habit_stmt->bind_param("is", $user_id, $category_name);
            $habit_stmt->execute();
            $habit_result = $habit_stmt->get_result();
            
            while ($habit_stats = $habit_result->fetch_assoc()) {
                $total_completed = $habit_stats['total_completed'] ?: 0;
                $total_scheduled = $habit_stats['total_scheduled'] ?: 0;
                
                $completed_count += $total_completed;
                $total_count += $total_scheduled;
            }
            $habit_stmt->close();
            
            $completion_percentage = $total_count > 0 ? round(($completed_count / $total_count) * 100, 1) : 0;
            
            $categories[] = [
                'name' => $category_name,
                'completed_count' => $completed_count,
                'total_count' => $total_count,
                'completion_percentage' => $completion_percentage
            ];
        }
        $stmt->close();

        
        $week_percentage = $week_total > 0 ? round(($week_completed / $week_total) * 100, 1) : 0;

        
        $statistics = [
            'total_habits' => $total_habits,
            'active_today' => $active_today,
            'longest_streak' => $longest_streak,
            'total_completions' => $total_completions,
            'week_completed' => $week_completed,
            'week_total' => $week_total,
            'week_percentage' => $week_percentage,
            'categories' => $categories
        ];

        echo json_encode([
            "success" => true,
            "message" => "Statistiques récupérées avec succès.",
            "statistics" => $statistics
        ]);

    } catch (Exception $e) {
        echo json_encode([
            "success" => false, 
            "message" => "Erreur lors du calcul des statistiques: " . $e->getMessage()
        ]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Méthode de requête invalide."]);
}

$conn->close();
?>