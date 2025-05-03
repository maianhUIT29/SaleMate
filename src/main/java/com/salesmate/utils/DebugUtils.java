package com.salesmate.utils;

import java.awt.Component;
import java.awt.Container;

import javax.swing.SwingUtilities;

/**
 * Utility class for debugging UI components and application flow
 */
public class DebugUtils {
    
    private static final boolean DEBUG_ENABLED = true;
    
    /**
     * Log a debug message if debugging is enabled
     * 
     * @param message The message to log
     */
    public static void log(String message) {
        if (DEBUG_ENABLED) {
            System.out.println("[DEBUG] " + message);
        }
    }
    
    /**
     * Log information about a Swing component
     * 
     * @param component The component to log information about
     * @param componentName A descriptive name for the component
     */
    public static void logComponentInfo(Component component, String componentName) {
        if (!DEBUG_ENABLED) return;
        
        if (component == null) {
            log(componentName + " is null");
            return;
        }
        
        StringBuilder info = new StringBuilder();
        info.append(componentName)
            .append(" [")
            .append(component.getClass().getSimpleName())
            .append("]: ")
            .append("visible=").append(component.isVisible())
            .append(", showing=").append(component.isShowing())
            .append(", displayable=").append(component.isDisplayable())
            .append(", enabled=").append(component.isEnabled())
            .append(", size=").append(component.getSize().width).append("x").append(component.getSize().height)
            .append(", location=").append(component.getLocation().x).append(",").append(component.getLocation().y);
        
        log(info.toString());
    }
    
    /**
     * Print the component hierarchy starting from the given container
     * 
     * @param container The starting container
     * @param prefix Indentation prefix for the current level
     */
    public static void printComponentHierarchy(Container container, String prefix) {
        if (!DEBUG_ENABLED) return;
        
        log(prefix + container.getClass().getSimpleName() + 
            " [visible=" + container.isVisible() + 
            ", showing=" + container.isShowing() + "]");
        
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof Container) {
                printComponentHierarchy((Container) component, prefix + "    ");
            } else {
                log(prefix + "    " + component.getClass().getSimpleName() + 
                    " [visible=" + component.isVisible() + 
                    ", showing=" + component.isShowing() + "]");
            }
        }
    }
    
    /**
     * Check if we're on the Event Dispatch Thread
     * 
     * @return true if the current thread is the EDT
     */
    public static boolean isOnEDT() {
        boolean onEDT = SwingUtilities.isEventDispatchThread();
        log("Running on EDT: " + onEDT);
        return onEDT;
    }
}
