package com.example.suivi_habitudes.models;

import com.google.gson.annotations.SerializedName;

public class HabitCheckResponse extends ApiResponse {
    
    @SerializedName("total_completions")
    private int totalCompletions;
    
    @SerializedName("current_streak")
    private int currentStreak;
    
    
    public int getTotalCompletions() {
        return totalCompletions;
    }
    
    public void setTotalCompletions(int totalCompletions) {
        this.totalCompletions = totalCompletions;
    }
    
    public int getCurrentStreak() {
        return currentStreak;
    }
    
    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }
}