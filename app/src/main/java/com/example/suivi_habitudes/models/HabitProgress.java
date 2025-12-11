package com.example.suivi_habitudes.models;

import com.google.gson.annotations.SerializedName;

public class HabitProgress {
    @SerializedName("id")
    private int id;

    @SerializedName("habit_id")
    private int habitId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("log_date")
    private String logDate;

    @SerializedName("is_completed")
    private boolean isCompleted;

    @SerializedName("completed_at")
    private String completedAt;

    @SerializedName("created_at")
    private String createdAt;

    
    public HabitProgress() {}

    public HabitProgress(int habitId, int userId, String logDate, boolean isCompleted) {
        this.habitId = habitId;
        this.userId = userId;
        this.logDate = logDate;
        this.isCompleted = isCompleted;
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}