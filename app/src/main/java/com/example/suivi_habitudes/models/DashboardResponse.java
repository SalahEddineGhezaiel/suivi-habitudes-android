package com.example.suivi_habitudes.models;

import java.util.List;

public class DashboardResponse {
    private boolean success;
    private String message;
    private UserInfo user;
    private List<Habit> habits;
    private Statistics statistics;
    private TodayProgress today_progress;
    private Streaks streaks;

    
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public List<Habit> getHabits() {
        return habits;
    }

    public void setHabits(List<Habit> habits) {
        this.habits = habits;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public TodayProgress getTodayProgress() {
        return today_progress;
    }

    public void setTodayProgress(TodayProgress today_progress) {
        this.today_progress = today_progress;
    }

    public Streaks getStreaks() {
        return streaks;
    }

    public void setStreaks(Streaks streaks) {
        this.streaks = streaks;
    }

    
    public static class UserInfo {
        private int id;
        private String name;
        private String email;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class Statistics {
        private int total_habits;
        private List<Category> categories;

        public int getTotalHabits() {
            return total_habits;
        }

        public void setTotalHabits(int total_habits) {
            this.total_habits = total_habits;
        }

        public List<Category> getCategories() {
            return categories;
        }

        public void setCategories(List<Category> categories) {
            this.categories = categories;
        }

        public static class Category {
            private String category;
            private int count;

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }
        }
    }

    public static class TodayProgress {
        private int completed_today;
        private int total_due_today;
        private float completion_rate;

        public int getCompletedToday() {
            return completed_today;
        }

        public void setCompletedToday(int completed_today) {
            this.completed_today = completed_today;
        }

        public int getTotalDueToday() {
            return total_due_today;
        }

        public void setTotalDueToday(int total_due_today) {
            this.total_due_today = total_due_today;
        }

        public float getCompletionRate() {
            return completion_rate;
        }

        public void setCompletionRate(float completion_rate) {
            this.completion_rate = completion_rate;
        }
    }

    public static class Streaks {
        private int longest_streak;
        private int current_streak;

        public int getLongestStreak() {
            return longest_streak;
        }

        public void setLongestStreak(int longest_streak) {
            this.longest_streak = longest_streak;
        }

        public int getCurrentStreak() {
            return current_streak;
        }

        public void setCurrentStreak(int current_streak) {
            this.current_streak = current_streak;
        }
    }
}