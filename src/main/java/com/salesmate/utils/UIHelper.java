package com.salesmate.utils;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

/**
 * Helper class for managing focus behavior across the application
 */
public class UIHelper {
    
    /**
     * Sets up the application's look and feel with focus management
     */
    public static void setupLookAndFeel() {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Configure only focus-related properties
            configureFocusProperties();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Configure only focus-related UI properties
     */
    private static void configureFocusProperties() {
        // Set focus painting properties only
        UIManager.put("Button.focusPainted", false);
        UIManager.put("ToggleButton.focusPainted", false);
        UIManager.put("RadioButton.focusPainted", false);
        UIManager.put("CheckBox.focusPainted", false);
        
        // Enable focus for text components
        UIManager.put("TextField.focusPainted", true);
        UIManager.put("PasswordField.focusPainted", true);
        UIManager.put("TextArea.focusPainted", true);
        UIManager.put("TextPane.focusPainted", true);
        UIManager.put("EditorPane.focusPainted", true);
        
        // Set focus traversal keys
        Set<AWTKeyStroke> emptySet = new HashSet<>();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, emptySet);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, emptySet);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalKeys(
            KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, emptySet);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalKeys(
            KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS, emptySet);
    }

    /**
     * Remove all focus indicators from UI components
     */
    public static void removeFocusIndicators() {
        configureFocusProperties();
    }
    
    /**
     * Applies focus removal to a specific component
     * @param component The component to process
     */
    public static void removeFocusBorder(JComponent component) {
        if (component == null) {
            return;
        }
        
        try {
            // Only handle focus painting
            if (component instanceof JButton) {
                ((JButton) component).setFocusPainted(false);
            } else if (component instanceof JToggleButton) {
                ((JToggleButton) component).setFocusPainted(false);
            } else if (component instanceof JRadioButton) {
                ((JRadioButton) component).setFocusPainted(false);
            } else if (component instanceof JCheckBox) {
                ((JCheckBox) component).setFocusPainted(false);
            }
        } catch (Exception e) {
            // Log error but don't throw to prevent UI disruption
            System.err.println("Error removing focus border: " + e.getMessage());
        }
    }
    
    /**
     * Remove focus indicators from all components in a container recursively
     * Only affects focus, not colors or styling
     * @param container The container to process
     */
    public static void removeFocusFromAll(Container container) {
        if (container == null) {
            return;
        }
        
        try {
            for (Component comp : container.getComponents()) {
                if (comp instanceof JComponent) {
                    // Only remove focus painting for specific component types
                    JComponent jcomp = (JComponent) comp;
                    if (jcomp instanceof JButton || jcomp instanceof JToggleButton || 
                        jcomp instanceof JRadioButton || jcomp instanceof JCheckBox) {
                        removeFocusBorder(jcomp);
                    }
                }
                if (comp instanceof Container) {
                    removeFocusFromAll((Container) comp);
                }
            }
        } catch (Exception e) {
            // Log error but don't throw to prevent UI disruption
            System.err.println("Error removing focus from container: " + e.getMessage());
        }
    }
}
