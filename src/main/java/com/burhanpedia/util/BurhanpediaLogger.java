package com.burhanpedia.util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class BurhanpediaLogger {
    private final AtomicBoolean enabled;
    private final AtomicBoolean showTimestamp;
    private final AtomicBoolean showCategory;
    private static volatile BurhanpediaLogger instance;
    private static final String LOG_FILE = "data/logs.csv";
    private static final ReentrantLock lock = new ReentrantLock();
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private BurhanpediaLogger() {
        this.enabled = new AtomicBoolean(false);
        this.showTimestamp = new AtomicBoolean(true);
        this.showCategory = new AtomicBoolean(true);
        initializeLogFile();
    }
    
    private void initializeLogFile() {
        File logFile = new File(LOG_FILE);
        if (!logFile.exists()) {
            try {
                createLogFile(logFile);
            } catch (IOException e) {
                System.err.println("Failed to initialize log file: " + e.getMessage());
            }
        }
    }
    
    private void createLogFile(File file) throws IOException {
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create log directory: " + parentDir.getAbsolutePath());
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("timestamp,status,detail\n");
        }
    }
    
    public static BurhanpediaLogger getInstance() {
        if (instance == null) {
            synchronized (BurhanpediaLogger.class) {
                if (instance == null) {
                    instance = new BurhanpediaLogger();
                }
            }
        }
        return instance;
    }
    
    public void enable(boolean showTimestamp, boolean showCategory) {
        this.enabled.set(true);
        this.showTimestamp.set(showTimestamp);
        this.showCategory.set(showCategory);
    }
    
    public void disable() {
        this.enabled.set(false);
    }
    
    public void log(String category, String message) {
        if (!enabled.get()) return;
        System.out.println(enabled.get());
        
        StringBuilder logMessage = new StringBuilder();
        
        if (showTimestamp.get()) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            logMessage.append("[").append(timestamp).append("] ");
        }
        
        if (showCategory.get()) {
            logMessage.append("[").append(category).append("] ");
        }
        
        logMessage.append(message);
        
        // Write to file
        lock.lock();
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
                writer.write(logMessage.toString() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        } finally {
            lock.unlock();
        }
        
        // Print to console
        System.out.println(logMessage.toString());
    }
}