package com.burhanpedia.util;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateManager {
    private static final String DATE_FILE = "data/date.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
    private static DateManager instance;
    private LocalDate currentDate;
    
    private DateManager() {
        loadDate();
    }
    
    public static DateManager getInstance() {
        if (instance == null) {
            instance = new DateManager();
        }
        return instance;
    }
    
    private void loadDate() {
        File file = new File(DATE_FILE);
        if (!file.exists()) {
            // If file doesn't exist, initialize with current date
            currentDate = LocalDate.now();
            saveDate();
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String dateStr = reader.readLine();
                if (dateStr != null && !dateStr.trim().isEmpty()) {
                    currentDate = LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
                } else {
                    currentDate = LocalDate.now();
                    saveDate();
                }
            } catch (IOException | DateTimeParseException e) {
                System.err.println("Error reading date file: " + e.getMessage());
                currentDate = LocalDate.now();
                saveDate();
            }
        }
    }
    
    private void saveDate() {
        File file = new File(DATE_FILE);
        try {
            // Create parent directories if they don't exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(currentDate.format(DATE_FORMATTER));
            }
        } catch (IOException e) {
            System.err.println("Error saving date file: " + e.getMessage());
        }
    }
    
    public void nextDay() {
        currentDate = currentDate.plusDays(1);
        saveDate();
    }
    
    public String getCurrentDateFormatted() {
        return currentDate.format(DATE_FORMATTER);
    }
    
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void reset() {
        currentDate = LocalDate.now();
        saveDate();
    }
} 