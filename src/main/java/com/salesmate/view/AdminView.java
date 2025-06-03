package com.salesmate.view;

import com.salesmate.component.AdminChatbot;
import com.salesmate.component.AdAccountPopup;
import com.salesmate.component.AdAttendancePanel;
import com.salesmate.component.AdAccountPanel;
import com.salesmate.component.AdDashBoard;
import com.salesmate.component.AdInvoicePanel;
import com.salesmate.component.AdProductPanel;
import com.salesmate.component.AdSalaryPanel;
import com.salesmate.component.AdUserPanel;
import com.salesmate.component.AdminHeader;
import com.salesmate.component.AdminSidebar;
import com.salesmate.utils.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.Beans;

public class AdminView extends JFrame {

    private AdminChatbot      anAnChatbot;

    // --- Các card do bạn tự tạo hoặc Form Editor cũ từng sinh ra ---
    private AdDashBoard       cardDashBoard;
    private AdInvoicePanel    cardInvoicePanel;
    private AdProductPanel    cardProductPanel;
    private AdAccountPanel    cardRevenuePanel;
    private AdUserPanel       cardUserPanel;

    // --- Hai panel mới (bạn tạo riêng) ---
    private AdAttendancePanel cardAttendancePanel;
    private AdSalaryPanel     cardSalaryPanel;

    // --- Sidebar, header, popup, container ---
    private AdminSidebar      adminSidebar;
    private AdminHeader       adminHeader;
    private AdAccountPopup    adAccountPopup1;
    private JPanel            panelCard;     // chứa CardLayout
    private JPanel            panelDisplay;  // chứa sidebar + panelCard

    public AdminView() {
        try {
            // 1. Thiết lập tiêu đề và kích thước
            setTitle("Admin Dashboard");
            setSize(1200, 800);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // 2. Thiết lập chung Look & Feel
            UIHelper.setupLookAndFeel();
            try {
                LookAndFeel oldLF = UIManager.getLookAndFeel();
                Object buttonUI = UIManager.get("ButtonUI");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                if (buttonUI != null) {
                    UIManager.put("ButtonUI", buttonUI);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // 3. Gọi initComponents để khởi tạo UI
            initComponents();

            // 4. Nếu không phải ở chế độ thiết kế, gán reference và khởi tạo chatbot
            if (!Beans.isDesignTime()) {
                adminSidebar.setParentView(this);
                adAccountPopup1.setParentView(this);
                setupChatbot();
            }

            // 5. Loại bỏ focus highlight, căn giữa, validate/repaint
            UIHelper.removeFocusFromAll(this);
            setLocationRelativeTo(null);
            invalidate();
            validate();
            repaint();

            // 6. Resize listener để reposition chatbot
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    repositionChatbot();
                }
            });

            System.out.println("AdminView constructor completed successfully");
        } catch (Exception e) {
            System.err.println("Error initializing AdminView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Thiết lập chatbot
    private void setupChatbot() {
        try {
            anAnChatbot = new AdminChatbot();
            getLayeredPane().add(anAnChatbot, new Integer(100));
            repositionChatbot();
        } catch (Exception e) {
            System.err.println("Error setting up chatbot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Đặt lại vị trí chatbot
    private void repositionChatbot() {
        if (anAnChatbot != null && isVisible()) {
            anAnChatbot.positionInBottomRight();
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // 1) Khởi tạo các component
        adAccountPopup1      = new AdAccountPopup();
        adminHeader          = new AdminHeader();
        panelDisplay         = new JPanel(new BorderLayout());
        adminSidebar         = new AdminSidebar();
        panelCard            = new JPanel(new CardLayout());
        cardDashBoard        = new AdDashBoard();
        cardInvoicePanel     = new AdInvoicePanel();
        cardProductPanel     = new AdProductPanel();
        cardUserPanel        = new AdUserPanel();
        cardRevenuePanel     = new AdAccountPanel();

        // Khởi tạo hai panel mới
        cardAttendancePanel  = new AdAttendancePanel(); 
        cardSalaryPanel      = new AdSalaryPanel();     

        // 2) Add header lên NORTH
        getContentPane().add(adminHeader, BorderLayout.NORTH);

        // 3) Add các card vào panelCard (CardLayout)
        panelCard.add(cardDashBoard,        "cardDashBoard");
        panelCard.add(cardInvoicePanel,     "cardInvoicePanel");
        panelCard.add(cardProductPanel,     "cardProductPanel");
        panelCard.add(cardUserPanel,        "cardUserPanel");
        panelCard.add(cardRevenuePanel,     "cardRevenuePanel");
        panelCard.add(cardAttendancePanel,  "cardAttendancePanel");
        panelCard.add(cardSalaryPanel,      "cardSalaryPanel");

        // 4) Add sidebar và panelCard vào panelDisplay
        panelDisplay.add(adminSidebar, BorderLayout.WEST);
        panelDisplay.add(panelCard,    BorderLayout.CENTER);

        // 5) Add panelDisplay lên CENTER của JFrame
        getContentPane().add(panelDisplay, BorderLayout.CENTER);

        // 6) Show card "cardDashBoard" làm mặc định
        ((CardLayout) panelCard.getLayout()).show(panelCard, "cardDashBoard");

        // 7) Gọi pack()
        pack();
    }

    // Chuyển card
    public void switchCard(String cardName) {
        CardLayout cl = (CardLayout) panelCard.getLayout();
        cl.show(panelCard, cardName);
    }

    public static void main(String args[]) {
        UIHelper.setupLookAndFeel();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            try {
                AdminView view = new AdminView();
                view.setExtendedState(JFrame.MAXIMIZED_BOTH);
                view.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error initializing AdminView: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    
}
