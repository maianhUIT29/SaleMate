package com.salesmate.view;

import java.awt.CardLayout;
import javax.swing.UIManager;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import com.salesmate.component.ChatbotPanel;

public class AdminView extends javax.swing.JFrame {

    private ChatbotPanel chatbotPanel;

    public AdminView() {
        try {
            // Set system look and feel but exclude buttons
            try {
                // Capture existing button UI before setting look and feel
                javax.swing.LookAndFeel oldLF = UIManager.getLookAndFeel();
                Object buttonUI = UIManager.get("ButtonUI");
                
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
                
                // Preserve button UI to keep their appearance
                if (buttonUI != null) {
                    UIManager.put("ButtonUI", buttonUI);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            initComponents();
            
            if (!java.beans.Beans.isDesignTime()) { 
                // Initialize UI components
                adminSidebar.setParentView(this);
                adAccountPopup1.setParentView(this);

                // Set up card layout
                CardLayout cl = (CardLayout) panelCard.getLayout();
                panelCard.add(cardDashBoard,    "cardDashBoard");
                panelCard.add(cardRevenuePanel, "cardRevenuePanel");
                panelCard.add(cardInvoicePanel, "cardInvoicePanel");
                panelCard.add(cardProductPanel, "cardProductPanel");
                panelCard.add(cardUserPanel,    "cardUserPanel");
                
                // Make sure to show a default card
                cl.show(panelCard, "cardDashBoard");
                
                // Add chatbot
                setupChatbot();
            }
            
            // Set preferred size for better initial display
            setPreferredSize(new java.awt.Dimension(1024, 768));
            
            // Center the window on the screen
            setLocationRelativeTo(null);
            
            // Perform a complete layout of all components
            invalidate();
            validate();
            repaint();
            
            System.out.println("AdminView constructor completed successfully");
        } catch (Exception e) {
            System.err.println("Error initializing AdminView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets up the chatbot in the bottom right corner
     */
    private void setupChatbot() {
        try {
            // Create an overlay glass pane to hold the chatbot
            JPanel glassPane = new JPanel();
            glassPane.setLayout(null);  // Use absolute positioning
            glassPane.setOpaque(false); // Make it transparent
            
            // Create chatbot panel
            chatbotPanel = new ChatbotPanel();
            glassPane.add(chatbotPanel);
            
            // Replace the glass pane
            setGlassPane(glassPane);
            glassPane.setVisible(true);
            
            // Add resize listener to adjust chatbot position when window resizes
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    repositionChatbot();
                }
            });
            
            // Initial positioning
            repositionChatbot();
            
            System.out.println("Chatbot initialized successfully");
        } catch (Exception e) {
            System.err.println("Error setting up chatbot: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Repositions the chatbot to the bottom right corner of the window
     */
    private void repositionChatbot() {
        if (chatbotPanel != null) {
            Dimension chatSize = chatbotPanel.getPreferredSize();
            int width = getWidth();
            int height = getHeight();
            
            // Position chatbot at the bottom right corner with better padding
            // Move it higher and a bit inward from the right edge
            chatbotPanel.setBounds(
                width - chatSize.width - 20, 
                height - chatSize.height - 50,
                chatSize.width,
                chatSize.height
            );
            
            // Debug log
            System.out.println("Chatbot repositioned to: " + 
                (width - chatSize.width - 20) + ", " + (height - chatSize.height - 50) + 
                " with size " + chatSize.width + "x" + chatSize.height);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        adAccountPopup1 = new com.salesmate.component.AdAccountPopup();
        adminHeader = new com.salesmate.component.AdminHeader();
        panelDisplay = new javax.swing.JPanel();
        adminSidebar = new com.salesmate.component.AdminSidebar();
        panelCard = new javax.swing.JPanel();
        cardDashBoard = new com.salesmate.component.AdDashBoard();
        cardInvoicePanel = new com.salesmate.component.AdInvoicePanel();
        cardProductPanel = new com.salesmate.component.AdProductPanel();
        cardUserPanel = new com.salesmate.component.AdUserPanel();
        cardRevenuePanel = new com.salesmate.component.AdRevenuePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(adminHeader, java.awt.BorderLayout.PAGE_START);

        panelCard.setLayout(new java.awt.CardLayout());
        panelCard.add(cardDashBoard, "card2");
        panelCard.add(cardInvoicePanel, "card4");
        panelCard.add(cardProductPanel, "card5");
        panelCard.add(cardUserPanel, "card6");
        panelCard.add(cardRevenuePanel, "card6");

        javax.swing.GroupLayout panelDisplayLayout = new javax.swing.GroupLayout(panelDisplay);
        panelDisplay.setLayout(panelDisplayLayout);
        panelDisplayLayout.setHorizontalGroup(
                panelDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelDisplayLayout.createSequentialGroup()
                                .addComponent(adminSidebar, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelCard, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE))
        );
        panelDisplayLayout.setVerticalGroup(
                panelDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDisplayLayout.createSequentialGroup()
                                .addGroup(panelDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(adminSidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(panelCard, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE))
                                .addContainerGap())
        );

        getContentPane().add(panelDisplay, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        // Set up look and feel
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // Create and display the form
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    AdminView view = new AdminView();
                    view.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH); // Maximize
                    view.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(null,
                        "Error initializing AdminView: " + e.getMessage(),
                        "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    //phuong thuc chuyen Card
    public void switchCard(String cardName) {
        CardLayout cl = (CardLayout) panelCard.getLayout();
        cl.show(panelCard, cardName); // Chuyển sang card tương ứng
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.salesmate.component.AdAccountPopup adAccountPopup1;
    private com.salesmate.component.AdminHeader adminHeader;
    private com.salesmate.component.AdminSidebar adminSidebar;
    private com.salesmate.component.AdDashBoard cardDashBoard;
    private com.salesmate.component.AdInvoicePanel cardInvoicePanel;
    private com.salesmate.component.AdProductPanel cardProductPanel;
    private com.salesmate.component.AdRevenuePanel cardRevenuePanel;
    private com.salesmate.component.AdUserPanel cardUserPanel;
    private javax.swing.JPanel panelCard;
    private javax.swing.JPanel panelDisplay;
    // End of variables declaration//GEN-END:variables
}
