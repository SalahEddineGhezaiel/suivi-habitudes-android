package com.example.suivi_habitudes.models;

import com.google.gson.annotations.SerializedName;

public class Habit {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;
    
    @SerializedName("category")
    private String category;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("total_completions")
    private int totalCompletions;

    
    @SerializedName("monday")
    private boolean monday;

    @SerializedName("tuesday")
    private boolean tuesday;

    @SerializedName("wednesday")
    private boolean wednesday;

    @SerializedName("thursday")
    private boolean thursday;

    @SerializedName("friday")
    private boolean friday;

    @SerializedName("saturday")
    private boolean saturday;

    @SerializedName("sunday")
    private boolean sunday;

    
    @SerializedName("is_completed_today")
    private boolean isCompletedToday;

    @SerializedName("current_streak")
    private int currentStreak;

    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    
    public int getTotalCompletions() { return totalCompletions; }
    public void setTotalCompletions(int totalCompletions) { this.totalCompletions = totalCompletions; }

    
    public boolean isMonday() { return monday; }
    public void setMonday(boolean monday) { this.monday = monday; }
    
    public boolean isTuesday() { return tuesday; }
    public void setTuesday(boolean tuesday) { this.tuesday = tuesday; }
    
    public boolean isWednesday() { return wednesday; }
    public void setWednesday(boolean wednesday) { this.wednesday = wednesday; }
    
    public boolean isThursday() { return thursday; }
    public void setThursday(boolean thursday) { this.thursday = thursday; }
    
    public boolean isFriday() { return friday; }
    public void setFriday(boolean friday) { this.friday = friday; }
    
    public boolean isSaturday() { return saturday; }
    public void setSaturday(boolean saturday) { this.saturday = saturday; }
    
    public boolean isSunday() { return sunday; }
    public void setSunday(boolean sunday) { this.sunday = sunday; }

    
    public boolean isCompletedToday() { return isCompletedToday; }
    public void setCompletedToday(boolean completedToday) { isCompletedToday = completedToday; }
    
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
}