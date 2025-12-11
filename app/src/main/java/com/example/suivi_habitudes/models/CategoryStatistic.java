package com.example.suivi_habitudes.models;

public class CategoryStatistic {
    private String name;
    private int completed_count;
    private int total_count;
    private double completion_percentage;

    
    public CategoryStatistic() {}

    
    public CategoryStatistic(String name, int completed_count, int total_count, double completion_percentage) {
        this.name = name;
        this.completed_count = completed_count;
        this.total_count = total_count;
        this.completion_percentage = completion_percentage;
    }

    
    public String getName() {
        return name;
    }

    public int getCompletedCount() {
        return completed_count;
    }

    public int getTotalCount() {
        return total_count;
    }

    public double getCompletionPercentage() {
        return completion_percentage;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    public void setCompletedCount(int completed_count) {
        this.completed_count = completed_count;
    }

    public void setTotalCount(int total_count) {
        this.total_count = total_count;
    }

    public void setCompletionPercentage(double completion_percentage) {
        this.completion_percentage = completion_percentage;
    }
}