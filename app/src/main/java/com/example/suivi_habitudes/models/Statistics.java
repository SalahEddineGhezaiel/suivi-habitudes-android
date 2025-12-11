package com.example.suivi_habitudes.models;

import java.util.List;

public class Statistics {
    private int total_habits;
    private int active_today;
    private int longest_streak;
    private int total_completions;
    private int week_completed;
    private int week_total;
    private double week_percentage;
    private List<CategoryStatistic> categories;

    
    public Statistics() {}

    
    public Statistics(int total_habits, int active_today, int longest_streak, int total_completions,
                     int week_completed, int week_total, double week_percentage, 
                     List<CategoryStatistic> categories) {
        this.total_habits = total_habits;
        this.active_today = active_today;
        this.longest_streak = longest_streak;
        this.total_completions = total_completions;
        this.week_completed = week_completed;
        this.week_total = week_total;
        this.week_percentage = week_percentage;
        this.categories = categories;
    }

    
    public int getTotalHabits() {
        return total_habits;
    }

    public int getActiveToday() {
        return active_today;
    }

    public int getLongestStreak() {
        return longest_streak;
    }

    public int getTotalCompletions() {
        return total_completions;
    }

    public int getWeekCompleted() {
        return week_completed;
    }

    public int getWeekTotal() {
        return week_total;
    }

    public double getWeekPercentage() {
        return week_percentage;
    }

    public List<CategoryStatistic> getCategories() {
        return categories;
    }

    
    public void setTotalHabits(int total_habits) {
        this.total_habits = total_habits;
    }

    public void setActiveToday(int active_today) {
        this.active_today = active_today;
    }

    public void setLongestStreak(int longest_streak) {
        this.longest_streak = longest_streak;
    }

    public void setTotalCompletions(int total_completions) {
        this.total_completions = total_completions;
    }

    public void setWeekCompleted(int week_completed) {
        this.week_completed = week_completed;
    }

    public void setWeekTotal(int week_total) {
        this.week_total = week_total;
    }

    public void setWeekPercentage(double week_percentage) {
        this.week_percentage = week_percentage;
    }

    public void setCategories(List<CategoryStatistic> categories) {
        this.categories = categories;
    }
}