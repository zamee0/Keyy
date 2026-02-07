package com.keyy.app;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class UserManager {
    private static final String USER_DATA_DIR = "user_data";
    private static final String USERS_FILE = USER_DATA_DIR + "/users.txt";
    private static final String SCORES_DIR = USER_DATA_DIR + "/scores";
    
    // Initialize directories
    public static void initialize() {
        try {
            Files.createDirectories(Paths.get(SCORES_DIR));
            
            // Create users.txt if it doesn't exist
            File usersFile = new File(USERS_FILE);
            if (!usersFile.exists()) {
                usersFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Register a new user
    public static boolean registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // Check if user already exists
        if (userExists(username)) {
            return false;
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            writer.write(username + ":" + password);
            writer.newLine();
            
            // Create user's score file
            File scoreFile = new File(SCORES_DIR + "/" + username + ".txt");
            scoreFile.createNewFile();
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Check if user exists
    public static boolean userExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Login user
    public static boolean loginUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Save score for a user
    public static void saveScore(String username, double wpm, double accuracy, int timeInSeconds) {
        String scoreFile = SCORES_DIR + "/" + username + ".txt";
        LocalDate today = LocalDate.now();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scoreFile, true))) {
            writer.write(String.format("%.1f,%.1f,%d,%s", wpm, accuracy, timeInSeconds, today.toString()));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Get all scores for a user
    public static List<ScoreRecord> getUserScores(String username) {
        List<ScoreRecord> scores = new ArrayList<>();
        String scoreFile = SCORES_DIR + "/" + username + ".txt";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    double wpm = Double.parseDouble(parts[0]);
                    double accuracy = Double.parseDouble(parts[1]);
                    int time = Integer.parseInt(parts[2]);
                    LocalDate date = LocalDate.parse(parts[3]);
                    scores.add(new ScoreRecord(wpm, accuracy, time, date, username));
                }
            }
        } catch (IOException e) {
            // File might not exist yet
        }
        return scores;
    }
    
    // Get best WPM for a user
    public static double getBestWPM(String username) {
        List<ScoreRecord> scores = getUserScores(username);
        return scores.stream()
                .mapToDouble(ScoreRecord::getWpm)
                .max()
                .orElse(0.0);
    }
    
    // Get average WPM for a user
    public static double getAverageWPM(String username) {
        List<ScoreRecord> scores = getUserScores(username);
        if (scores.isEmpty()) return 0.0;
        
        return scores.stream()
                .mapToDouble(ScoreRecord::getWpm)
                .average()
                .orElse(0.0);
    }
    
    // Get leaderboard (all users with their best scores)
    public static List<Map.Entry<String, Double>> getLeaderboard() {
        Map<String, Double> leaderboard = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0];
                    double bestWPM = getBestWPM(username);
                    leaderboard.put(username, bestWPM);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Sort by WPM (descending)
        List<Map.Entry<String, Double>> sortedLeaderboard = new ArrayList<>(leaderboard.entrySet());
        sortedLeaderboard.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        return sortedLeaderboard;
    }
    
    // Get total attempts for a user
    public static int getTotalAttempts(String username) {
        return getUserScores(username).size();
    }
}
