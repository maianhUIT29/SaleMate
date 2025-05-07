package com.salesmate.utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Helper class for managing UI appearance across the application
 */
public class UIHelper {
    
    // Store original borders to restore them if needed
    private static Map<JComponent, Border> originalBorders = new HashMap<>();
    
    /**
     * Sets up the application's look and feel with no focus indicators
     */
    public static void setupLookAndFeel() {
        try {
            // First set the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Then remove all focus indicators
            removeFocusIndicators();
            
        } catch (Exception ex) {
            System.err.println("Error setting up look and feel: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Remove all focus indicators from UI components
     */
    public static void removeFocusIndicators() {
        try {
            // Create transparent color for focus
            Color transparent = new Color(0, 0, 0, 0);
            ColorUIResource transparentColor = new ColorUIResource(transparent);
            
            // Remove focus painted from buttons
            UIManager.put("Button.focusPainted", false);
            UIManager.put("ToggleButton.focusPainted", false);
            UIManager.put("RadioButton.focusPainted", false);
            UIManager.put("CheckBox.focusPainted", false);
            
            // Remove focus colors/borders
            UIManager.put("Button.focus", transparentColor);
            UIManager.put("TabbedPane.focus", transparentColor);
            UIManager.put("TabbedPane.selectedFocus", transparentColor);
            UIManager.put("ComboBox.selectionBackground", transparentColor);
            UIManager.put("ComboBox.focusBackground", transparentColor);
            UIManager.put("List.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
            UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
            
            // Disable dotted outlines
            UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
            UIManager.put("TabbedPane.tabsOverlapBorder", true);
            UIManager.put("TabbedPane.tabAreaInsets", new Insets(0, 0, 0, 0));

            // Disable focus traversal indicators
            UIManager.put("ComboBox.focusable", false);
            UIManager.put("TabbedPane.focusable", false);
            
            // Disable focus input maps (keyboard navigation highlights)
            UIManager.put("TextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[0]));
            UIManager.put("TextArea.focusInputMap", new UIDefaults.LazyInputMap(new Object[0]));
            UIManager.put("Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[0]));
            UIManager.put("ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[0]));
            
            // Disable keyboard focus traversal
            KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalKeys(
                    KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, new HashSet<>());
            KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalKeys(
                    KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, new HashSet<>());
            
        } catch (Exception e) {
            System.err.println("Error removing focus indicators: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Applies focus removal to a specific component
     * @param component The component to process
     */
    public static void removeFocusBorder(JComponent component) {
        // Store original border
        originalBorders.put(component, component.getBorder());
        
        if (component instanceof JButton) {
            ((JButton) component).setFocusPainted(false);
        } 
        else if (component instanceof JToggleButton) {
            ((JToggleButton) component).setFocusPainted(false);
        }
        else if (component instanceof JComboBox) {
            component.setFocusable(false);
        }
        else if (component instanceof JTable) {
            ((JTable) component).setFocusable(false);
            component.putClientProperty("JTable.focusBorderColor", new Color(0, 0, 0, 0));
        }
        else if (component instanceof JList) {
            component.setFocusable(false);
        }
        
        // Common processing for all components
        component.setFocusable(true); // Keep it focusable for functionality
        component.putClientProperty("JComponent.outline", null); // Remove component outline
    }
    
    /**
     * Remove focus indicators from all components in a container recursively
     * @param container The container to process
     */
    public static void removeFocusFromAll(Container container) {
        Component[] components = container.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JComponent) {
                removeFocusBorder((JComponent)comp);
            }
            
            if (comp instanceof Container) {
                removeFocusFromAll((Container)comp);
            }
        }
    }
    
    /**
     * Applies a custom background for alternating table rows
     * @param table The table to customize
     */
    public static void setupAlternatingRowColors(JTable table) {
        UIManager.put("Table.alternateRowColor", new Color(240, 245, 250));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setGridColor(new Color(220, 225, 230));
    }
}
