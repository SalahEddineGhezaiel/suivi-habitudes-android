package com.example.suivi_habitudes.models;

public class StatisticsResponse {
    private boolean success;
    private String message;
    private Statistics statistics;

    
    public StatisticsResponse() {}

    
    public StatisticsResponse(boolean success, String message, Statistics statistics) {
        this.success = success;
        this.message = message;
        this.statistics = statistics;
    }

    
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}