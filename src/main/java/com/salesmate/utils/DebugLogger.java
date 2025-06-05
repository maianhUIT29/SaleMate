package com.salesmate.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for logging debug information
 */
public class DebugLogger {
    private static final boolean LOG_TO_CONSOLE = true;
    private static final boolean LOG_TO_FILE = true;
    private static final String LOG_FILE = "salesmate_debug.log";
    
    /**
     * Log an informational message
     * @param message The message to log
     */
    public static void info(String message) {
        log("INFO", message);
    }
    
    /**
     * Log an error message
     * @param message The message to log
     */
    public static void error(String message) {
        log("ERROR", message);
    }
    
    /**
     * Log an error message with exception details
     * @param message The message to log
     * @param e The exception that occurred
     */
    public static void error(String message, Throwable e) {
        log("ERROR", message + ": " + e.getMessage());
        if (LOG_TO_FILE) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                writer.println(getLogPrefix("ERROR") + " Stack trace:");
                e.printStackTrace(writer);
                writer.println();
            } catch (IOException ex) {
                System.err.println("Failed to write to log file: " + ex.getMessage());
            }
        }
        if (LOG_TO_CONSOLE) {
            e.printStackTrace(System.err);
        }
    }
    
    /**
     * Log a message with the specified level
     * @param level The log level
     * @param message The message to log
     */
    private static void log(String level, String message) {
        String logEntry = getLogPrefix(level) + " " + message;
        
        if (LOG_TO_CONSOLE) {
            if ("ERROR".equals(level)) {
                System.err.println(logEntry);
            } else {
                System.out.println(logEntry);
            }
        }
        
        if (LOG_TO_FILE) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                writer.println(logEntry);
            } catch (IOException e) {
                System.err.println("Failed to write to log file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Create a log prefix with timestamp and log level
     * @param level The log level
     * @return The formatted log prefix
     */
    private static String getLogPrefix(String level) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(new Date()) + " [" + level + "]";
    }
    
    /**
     * Get the path to the log file
     * @return The absolute path to the log file
     */
    public static String getLogFilePath() {
        return new File(LOG_FILE).getAbsolutePath();
    }

    
}
