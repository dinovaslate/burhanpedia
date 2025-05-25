package com.burhanpedia.core.command;

import com.burhanpedia.util.BurhanpediaLogger;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.Map;
import java.util.HashMap;

public class SystemCommand {
    private final Scanner input;
    private final BurhanpediaLogger logger;
    private final Map<String, Consumer<String[]>> commandHandlers;
    private final Set<String> validCommands;
    
    public SystemCommand(Scanner input) {
        this.input = input;
        this.logger = BurhanpediaLogger.getInstance();
        this.commandHandlers = initializeCommandHandlers();
        this.validCommands = new HashSet<>(Arrays.asList("burhanlogger", "exit"));
    }
    
    private Map<String, Consumer<String[]>> initializeCommandHandlers() {
        Map<String, Consumer<String[]>> handlers = new HashMap<>();
        handlers.put("burhanlogger", this::processBurhanLoggerCommand);
        handlers.put("exit", args -> {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║         KEMBALI KE MENU           ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ Kembali ke menu utama...           ║");
            System.out.println("╚════════════════════════════════════╝");
            throw new ReturnToMainMenuException();
        });
        return handlers;
    }
    
    public void handleCommand() {
        while (true) {
            try {
                System.out.print("Command: ");
                String rawCommand = input.nextLine().trim();
                
                if (rawCommand.isEmpty()) {
                    continue;
                }
                
                String[] commandParts = rawCommand.split("\\s+");
                String baseCommand = commandParts[0].toLowerCase();
                
                if (!validCommands.contains(baseCommand)) {
                    System.out.println("Command tidak valid!");
                    continue;
                }
                
                commandHandlers.get(baseCommand).accept(commandParts);
            } catch (ReturnToMainMenuException e) {
                return;
            } catch (Exception e) {
                System.out.println("Error executing command: " + e.getMessage());
            }
        }
    }
    
    private void processBurhanLoggerCommand(String[] commandParts) {
        if (commandParts.length < 2) {
            System.out.println("Command BurhanLogger tidak valid!");
            return;
        }
        
        String action = commandParts[1].toLowerCase();
        LoggerConfig config = new LoggerConfig();
        
        switch (action) {
            case "enable":
                parseLoggerFlags(commandParts, config);
                enableLogger(config);
                break;
            case "disable":
                disableLogger();
                break;
            default:
                System.out.println("Command BurhanLogger tidak valid!");
        }
    }
    
    private void parseLoggerFlags(String[] commandParts, LoggerConfig config) {
        for (int i = 2; i < commandParts.length; i++) {
            switch (commandParts[i]) {
                case "-t":
                    config.setShowTimestamp(true);
                    break;
                case "-m":
                    config.setShowCategory(true);
                    break;
            }
        }
    }
    
    private void enableLogger(LoggerConfig config) {
        logger.enable(config.isShowTimestamp(), config.isShowCategory());
        displayLoggerStatus(config);
    }
    
    private void disableLogger() {
        logger.disable();
        System.out.println("Logger dinonaktifkan");
    }
    
    private void displayLoggerStatus(LoggerConfig config) {
        System.out.println("Logger diaktifkan dengan konfigurasi:");
        System.out.println("- Timestamp: " + (config.isShowTimestamp() ? "aktif" : "nonaktif"));
        System.out.println("- Category: " + (config.isShowCategory() ? "aktif" : "nonaktif"));
    }
    
    private static class LoggerConfig {
        private boolean showTimestamp;
        private boolean showCategory;
        
        public boolean isShowTimestamp() {
            return showTimestamp;
        }
        
        public void setShowTimestamp(boolean showTimestamp) {
            this.showTimestamp = showTimestamp;
        }
        
        public boolean isShowCategory() {
            return showCategory;
        }
        
        public void setShowCategory(boolean showCategory) {
            this.showCategory = showCategory;
        }
    }
    
    private static class ReturnToMainMenuException extends RuntimeException {
        public ReturnToMainMenuException() {
            super("Returning to main menu");
        }
    }
}