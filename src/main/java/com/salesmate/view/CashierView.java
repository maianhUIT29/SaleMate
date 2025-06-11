package com.salesmate.view;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.salesmate.component.CashierAccount;
import com.salesmate.component.CashierHeader;
import com.salesmate.component.CashierInvoicesPanel;
import com.salesmate.component.CheckoutPanel;
import com.salesmate.component.ProductSelectionPanel;
import com.salesmate.controller.ProductController;
import com.salesmate.model.Product;
import com.salesmate.utils.UIHelper;

public class CashierView extends JFrame {

    private ProductController productController;
    
    // Components
    private CashierHeader cashierHeader;
    private JTabbedPane tpCashier;
    private ProductSelectionPanel productSelectionPanel;
    private CheckoutPanel checkoutPanel;
    private CashierInvoicesPanel cashierInvoicesPanel;
    private CashierAccount cashierAccount;

    public CashierView() {
        try {
            System.out.println("Initializing CashierView...");
            
            // Use our custom look and feel helper
            UIHelper.setupLookAndFeel();
            
            initComponents();
            setupEventHandlers();
            
            System.out.println("CashierView: Setting up bidirectional connection between panels");
            productSelectionPanel.setCheckoutPanel(checkoutPanel);
            checkoutPanel.setProductSelectionPanel(productSelectionPanel);
            System.out.println("CashierView: Panel connection established");
            
            // Add window listener to maximize window after it becomes visible
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    System.out.println("CashierView window opened, maximizing...");
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                    validate();
                    repaint();
                    
                    // Apply focus removal AFTER everything is loaded and visible
                    SwingUtilities.invokeLater(() -> {
                        UIHelper.removeFocusFromAll(CashierView.this);
                    });
                }
            });
            
            // Make sure window is visible before loading products
            setVisible(true);
            
            // Load products after UI is set up
            SwingUtilities.invokeLater(this::loadProductList);
            
            System.out.println("CashierView initialization complete");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in CashierView constructor: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                    "Lỗi khởi tạo giao diện bán hàng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("SalesMate - Hệ thống bán hàng");
        setLayout(new BorderLayout());
        
        // Initialize header
        cashierHeader = new CashierHeader();
        
        // Initialize main components
        productSelectionPanel = new ProductSelectionPanel();
        checkoutPanel = new CheckoutPanel();
        cashierInvoicesPanel = new CashierInvoicesPanel();
        cashierAccount = new CashierAccount();
        
        // Create tabbed pane
        tpCashier = new JTabbedPane();
        tpCashier.setFont(new java.awt.Font("Segoe UI", 1, 14));
        
        // Tab 1: Bán hàng (Sales)
        JPanel salesPanel = createSalesPanel();
        tpCashier.addTab("Bán hàng", salesPanel);
        
        // Tab 2: Lịch sử bán hàng (Sales History)
        JPanel invoicesContainer = new JPanel(new BorderLayout());
        invoicesContainer.add(cashierInvoicesPanel, BorderLayout.CENTER);
        tpCashier.addTab("Lịch sử bán hàng", invoicesContainer);
        
        // Tab 3: Tài khoản (Account)
        JPanel accountContainer = new JPanel(new BorderLayout());
        accountContainer.add(cashierAccount, BorderLayout.CENTER);
        tpCashier.addTab("Tài khoản", accountContainer);
        
        // Add components to main frame
        add(cashierHeader, BorderLayout.NORTH);
        add(tpCashier, BorderLayout.CENTER);
        
        // Set initial size
        setBounds(0, 0, 1200, 800);
    }
    
    private JPanel createSalesPanel() {
        JPanel salesPanel = new JPanel(new BorderLayout());
        
        // Create main sales container with product selection and checkout
        JPanel salesContainer = new JPanel();
        salesContainer.setLayout(new javax.swing.BoxLayout(salesContainer, javax.swing.BoxLayout.X_AXIS));
        
        // Add spacing and borders
        productSelectionPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createTitledBorder("Chọn sản phẩm"),
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        checkoutPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createTitledBorder("Thanh toán"),
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Set preferred sizes
        productSelectionPanel.setPreferredSize(new java.awt.Dimension(750, 600));
        checkoutPanel.setPreferredSize(new java.awt.Dimension(450, 600));
        
        // Add panels to container
        salesContainer.add(productSelectionPanel);
        salesContainer.add(javax.swing.Box.createHorizontalStrut(10)); // Add spacing
        salesContainer.add(checkoutPanel);
        
        salesPanel.add(salesContainer, BorderLayout.CENTER);
        
        return salesPanel;
    }
    
    private void setupEventHandlers() {
        // Tab change listener to refresh data when switching tabs
        tpCashier.addChangeListener(e -> {
            int selectedIndex = tpCashier.getSelectedIndex();
            
            switch (selectedIndex) {
                case 0: // Sales tab
                    System.out.println("Switched to Sales tab");
                    // Optionally refresh product data
                    break;
                    
                case 1: // Invoices tab
                    System.out.println("Switched to Invoices tab");
                    // Refresh invoices data
                    SwingUtilities.invokeLater(() -> {
                        try {
                            // Trigger refresh of invoices panel
                            cashierInvoicesPanel.revalidate();
                            cashierInvoicesPanel.repaint();
                        } catch (Exception ex) {
                            System.err.println("Error refreshing invoices: " + ex.getMessage());
                        }
                    });
                    break;
                    
                case 2: // Account tab
                    System.out.println("Switched to Account tab");
                    // Refresh account data
                    SwingUtilities.invokeLater(() -> {
                        try {
                            // Trigger refresh of account panel
                            cashierAccount.revalidate();
                            cashierAccount.repaint();
                        } catch (Exception ex) {
                            System.err.println("Error refreshing account: " + ex.getMessage());
                        }
                    });
                    break;
            }
        });
    }

    private void loadProductList() {
        try {
            System.out.println("Loading product list...");
            productController = new ProductController();
            List<Product> products = productController.getAllProducts();
            if (products != null && !products.isEmpty()) {
                System.out.println("Found " + products.size() + " products");
                productSelectionPanel.setProducts(products);
            } else {
                System.out.println("No products found");
                JOptionPane.showMessageDialog(
                    this,
                    "Không tìm thấy sản phẩm nào trong cơ sở dữ liệu.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading products: " + e.getMessage());
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khi tải danh sách sản phẩm: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Switch to a specific tab programmatically
     * @param tabIndex 0 = Sales, 1 = Invoices, 2 = Account
     */
    public void switchToTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < tpCashier.getTabCount()) {
            tpCashier.setSelectedIndex(tabIndex);
        }
    }
    
    /**
     * Switch to Sales tab
     */
    public void switchToSalesTab() {
        switchToTab(0);
    }
    
    /**
     * Switch to Invoices tab
     */
    public void switchToInvoicesTab() {
        switchToTab(1);
    }
    
    /**
     * Switch to Account tab
     */
    public void switchToAccountTab() {
        switchToTab(2);
    }
    
    /**
     * Get reference to product selection panel
     */
    public ProductSelectionPanel getProductSelectionPanel() {
        return productSelectionPanel;
    }
    
    /**
     * Get reference to checkout panel
     */
    public CheckoutPanel getCheckoutPanel() {
        return checkoutPanel;
    }
    
    /**
     * Get reference to invoices panel
     */
    public CashierInvoicesPanel getInvoicesPanel() {
        return cashierInvoicesPanel;
    }
    
    /**
     * Get reference to account panel
     */
    public CashierAccount getAccountPanel() {
        return cashierAccount;
    }

    /**
     * Main method for testing
     */
    public static void main(String args[]) {
        try {
            System.out.println("Starting CashierView application...");
            UIHelper.setupLookAndFeel();
            
            java.awt.EventQueue.invokeLater(() -> {
                try {
                    CashierView view = new CashierView();
                    System.out.println("CashierView created successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error creating CashierView: " + e.getMessage());
                    JOptionPane.showMessageDialog(null, 
                            "Lỗi khởi tạo CashierView: " + e.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error in main: " + e.getMessage());
        }
    }
}
