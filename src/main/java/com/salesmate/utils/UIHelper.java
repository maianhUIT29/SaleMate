package com.salesmate.utils;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

/**
 * Helper class for managing UI appearance across the application
 */
public class UIHelper {
    
    // Store original borders to restore them if needed
    private static Map<JComponent, Border> originalBorders = new HashMap<>();
    
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    
    /**
     * Sets up the application's look and feel with no focus indicators
     */
    public static void setupLookAndFeel() {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Configure global UI properties
            configureUIManagerProperties();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Configure global UI properties
     */
    private static void configureUIManagerProperties() {
        // Set focus painting properties
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
        configureUIManagerProperties();
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
     * @param container The container to process
     */
    public static void removeFocusFromAll(Container container) {
        if (container == null) {
            return;
        }
        
        try {
            for (Component comp : container.getComponents()) {
                if (comp instanceof JComponent) {
                    removeFocusBorder((JComponent) comp);
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
    
    /**
     * Applies a custom background for alternating table rows
     * @param table The table to customize
     */
    public static void setupAlternatingRowColors(JTable table) {
        if (table == null) {
            return;
        }
        
        try {
            UIManager.put("Table.alternateRowColor", new Color(240, 245, 250));
            table.setShowHorizontalLines(true);
            table.setShowVerticalLines(false);
            table.setIntercellSpacing(new Dimension(0, 1));
            table.setGridColor(new Color(220, 225, 230));
        } catch (Exception e) {
            System.err.println("Error setting up alternating row colors: " + e.getMessage());
        }
    }

    public static void setupTableHeader(JTable table) {
        if (table == null || table.getTableHeader() == null) {
            return;
        }
        
        try {
            JTableHeader header = table.getTableHeader();
            header.setBackground(PRIMARY_COLOR);
            header.setForeground(Color.WHITE);
            header.setFont(new Font("Segoe UI", Font.BOLD, 15));
            header.setOpaque(true);
        } catch (Exception e) {
            System.err.println("Error setting up table header: " + e.getMessage());
        }
    }
}
