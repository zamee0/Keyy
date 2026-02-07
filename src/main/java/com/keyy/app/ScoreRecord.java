package com.keyy.app;

import java.time.LocalDate;

public class ScoreRecord {
    private double wpm;
    private double accuracy;
    private int timeInSeconds;
    private LocalDate date;
    private String username;
    
    public ScoreRecord(double wpm, double accuracy, int timeInSeconds, LocalDate date, String username) {
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.timeInSeconds = timeInSeconds;
        this.date = date;
        this.username = username;
    }
    
    public double getWpm() {
        return wpm;
    }
    
    public double getAccuracy() {
        return accuracy;
    }
    
    public int getTimeInSeconds() {
        return timeInSeconds;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public String getUsername() {
        return username;
    }
    
    @Override
    public String toString() {
        return String.format("%.1f,%.1f,%d,%s", wpm, accuracy, timeInSeconds, date.toString());
    }
}
