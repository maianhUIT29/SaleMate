/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.salesmate.component;
import com.salesmate.model.Product;
import com.salesmate.controller.InvoiceController;
import com.salesmate.controller.ProductController;
import com.salesmate.controller.UserController;
import com.salesmate.model.ChartDataModel;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.text.NumberFormat;
import java.util.Locale;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
/**
 *
 * @author meiln
 */
public class AdDashBoard extends javax.swing.JPanel {
    private InvoiceController invoiceController;
    private ProductController productController;
    private UserController userController;
    
    // Statistics panels
    private JPanel employeePanel;
    private JPanel productPanel;
    private JPanel invoicePanel;
    private JPanel revenuePanel;
    
    // Chart panels
    private JPanel revenueChartPanel;
    private JPanel topProductsPanel;
    private JPanel topCustomersPanel;
    private JPanel topInvoicesPanel;
    private JPanel invoiceStatusPanel;
    
    // Filter components
    private JComboBox<String> timeRangeCombo;
    private JComboBox<Integer> yearCombo;
    private JPanel filterPanel;

    /**
     * Creates new form AdDashBoard
     */
    public AdDashBoard() {
        initComponents();
        if (!java.beans.Beans.isDesignTime()) { 
            invoiceController = new InvoiceController();         
            productController = new ProductController();
            userController = new UserController();
            setupDashboard();
        }
    }

    private void setupDashboard() {
        // Create statistics panels
        createStatisticsPanels();
        
        // Create filter panel
        createFilterPanel();
        
        // Create revenue and order widgets
        createRevenueWidgets();
        
        // Create charts
        createCharts();
        
        // Add all panels to the main layout
        panelStatistic.setLayout(new BoxLayout(panelStatistic, BoxLayout.Y_AXIS));
        
        // Add statistics panels in a row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsRow.add(employeePanel);
        statsRow.add(productPanel);
        statsRow.add(invoicePanel);
        statsRow.add(revenuePanel);
        panelStatistic.add(statsRow);
        
        // Add filter panel
        panelStatistic.add(filterPanel);
        
        // Add revenue widgets
        JPanel revenueWidgetsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        revenueWidgetsRow.add(createDailyRevenueWidget());
        revenueWidgetsRow.add(createMonthlyRevenueWidget());
        panelStatistic.add(revenueWidgetsRow);
        
        // Add charts in a grid
        JPanel chartsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        chartsGrid.add(revenueChartPanel);
        chartsGrid.add(topProductsPanel);
        chartsGrid.add(topCustomersPanel);
        chartsGrid.add(invoiceStatusPanel);
        panelStatistic.add(chartsGrid);
        // Add top invoices table below charts
        panelStatistic.add(topInvoicesPanel);
        // Add low stock prediction table below
        List<Map<String, Object>> lowStockProducts = productController.getProductsLowStockPrediction(7);
        String[] lowStockColumns = {"Mã SP", "Tên SP", "Tồn kho", "Dự đoán hết hàng (ngày)", "Ngày dự kiến hết"};
        Object[][] lowStockData = new Object[lowStockProducts.size()][5];
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        for (int i = 0; i < lowStockProducts.size(); i++) {
            Map<String, Object> row = lowStockProducts.get(i);
            lowStockData[i][0] = row.get("product_id");
            lowStockData[i][1] = row.get("product_name");
            lowStockData[i][2] = row.get("quantity");
            lowStockData[i][3] = row.get("days_left");
            java.sql.Date outDate = (java.sql.Date) row.get("out_of_stock_date");
            lowStockData[i][4] = outDate != null ? sdf.format(outDate) : "-";
        }
        JTable lowStockTable = new JTable(lowStockData, lowStockColumns);
        JScrollPane lowStockScroll = new JScrollPane(lowStockTable);
        lowStockScroll.setPreferredSize(new Dimension(500, 150));
        JPanel lowStockPanel = new JPanel(new BorderLayout());
        lowStockPanel.add(new JLabel("Sản phẩm sắp hết hàng", SwingConstants.CENTER), BorderLayout.NORTH);
        lowStockPanel.add(lowStockScroll, BorderLayout.CENTER);
        lowStockPanel.setPreferredSize(new Dimension(500, 200));
        panelStatistic.add(lowStockPanel);
    }

    private void createStatisticsPanels() {
        // Employee count panel
        employeePanel = createStatPanel("Nhân viên", 
            String.valueOf(userController.countUser()),
            "Tổng số nhân viên");

        // Product count panel
        productPanel = createStatPanel("Sản phẩm", 
            String.valueOf(productController.countProduct()), 
            "Tổng số sản phẩm");

        // Invoice count panel
        invoicePanel = createStatPanel("Hóa đơn", 
            String.valueOf(invoiceController.countInvoices()), 
            "Tổng số hóa đơn");

        // Total revenue panel
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String totalRevenue = formatter.format(invoiceController.getCurrentMonthRevenue());
        revenuePanel = createStatPanel("Doanh thu", totalRevenue, "Tổng doanh thu");
    }

    private JPanel createStatPanel(String title, String value, String description) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(200, 100));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panel.add(titleLabel);
        panel.add(valueLabel);
        panel.add(descLabel);
        
        return panel;
    }

    private void createRevenueWidgets() {
        // Daily revenue widget
        JPanel dailyWidget = createDailyRevenueWidget();
        
        // Monthly revenue widget
        JPanel monthlyWidget = createMonthlyRevenueWidget();
    }

    private JPanel createDailyRevenueWidget() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String dailyRevenue = formatter.format(invoiceController.getTodayRevenue());
        
        JLabel titleLabel = new JLabel("Doanh thu hôm nay");
        JLabel valueLabel = new JLabel(dailyRevenue);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        panel.add(titleLabel);
        panel.add(valueLabel);
        
        return panel;
    }

    private JPanel createMonthlyRevenueWidget() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String monthlyRevenue = formatter.format(invoiceController.getCurrentMonthRevenue());
        
        JLabel titleLabel = new JLabel("Doanh thu tháng này");
        JLabel valueLabel = new JLabel(monthlyRevenue);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        panel.add(titleLabel);
        panel.add(valueLabel);
        
        return panel;
    }

    private void createFilterPanel() {
        filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Time range filter
        String[] timeRanges = {"Theo năm", "Theo tháng", "Theo tuần"};
        timeRangeCombo = new JComboBox<>(timeRanges);
        timeRangeCombo.addActionListener(e -> updateCharts());
        
        // Year filter
        yearCombo = new JComboBox<>();
        updateYearCombo();
        yearCombo.addActionListener(e -> updateCharts());
        
        filterPanel.add(new JLabel("Thời gian:"));
        filterPanel.add(timeRangeCombo);
        filterPanel.add(new JLabel("Năm:"));
        filterPanel.add(yearCombo);
    }

    private void updateYearCombo() {
        yearCombo.removeAllItems();
        List<Integer> years = invoiceController.getAvailableYears();
        for (Integer year : years) {
            yearCombo.addItem(year);
        }
    }

    private void updateCharts() {
        String selectedRange = (String) timeRangeCombo.getSelectedItem();
        Integer selectedYear = (Integer) yearCombo.getSelectedItem();
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String title = "";
        
        switch (selectedRange) {
            case "Theo năm":
                List<ChartDataModel> yearlyData = invoiceController.getYearlyRevenue();
                for (ChartDataModel data : yearlyData) {
                    dataset.addValue(data.getValue().doubleValue(), "Doanh thu", data.getLabel());
                }
                title = "Doanh thu theo năm";
                break;
                
            case "Theo tháng":
                List<ChartDataModel> monthlyData = invoiceController.getMonthlyRevenueByYear(selectedYear);
                for (ChartDataModel data : monthlyData) {
                    dataset.addValue(data.getValue().doubleValue(), "Doanh thu", "Tháng " + data.getLabel());
                }
                title = "Doanh thu theo tháng - " + selectedYear;
                break;
                
            case "Theo tuần":
                List<ChartDataModel> weeklyData = invoiceController.getWeeklyRevenueForCurrentMonth();
                for (ChartDataModel data : weeklyData) {
                    dataset.addValue(data.getValue().doubleValue(), "Doanh thu", data.getLabel());
                }
                title = "Doanh thu theo tuần - Tháng hiện tại";
                break;
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            title,
            "Thời gian",
            "Doanh thu (VNĐ)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        revenueChartPanel.removeAll();
        revenueChartPanel.add(new ChartPanel(chart));
        revenueChartPanel.revalidate();
        revenueChartPanel.repaint();
    }

    private void createCharts() {
        // Revenue chart
        DefaultCategoryDataset revenueDataset = new DefaultCategoryDataset();
        List<ChartDataModel> yearlyData = invoiceController.getYearlyRevenue();
        for (ChartDataModel data : yearlyData) {
            revenueDataset.addValue(data.getValue().doubleValue(), "Doanh thu", data.getLabel());
        }
        
        JFreeChart revenueChart = ChartFactory.createLineChart(
            "Doanh thu theo năm",
            "Năm",
            "Doanh thu (VNĐ)",
            revenueDataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        revenueChartPanel = new ChartPanel(revenueChart);
        revenueChartPanel.setPreferredSize(new Dimension(400, 300));
        
        // Top products chart
        DefaultCategoryDataset productsDataset = new DefaultCategoryDataset();
        List<Map<String, Object>> topProducts = productController.getTopSellingProducts();
        for (Map<String, Object> product : topProducts) {
            productsDataset.addValue((Integer)product.get("quantity"), 
                "Số lượng", product.get("product_name").toString());
        }
        
        JFreeChart productsChart = ChartFactory.createBarChart(
            "Sản phẩm bán chạy",
            "Sản phẩm",
            "Số lượng",
            productsDataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        topProductsPanel = new ChartPanel(productsChart);
        topProductsPanel.setPreferredSize(new Dimension(400, 300));
        
        // Top customers by revenue (bar chart)
        DefaultCategoryDataset customersDataset = new DefaultCategoryDataset();
        List<Map<String, Object>> topCustomers = invoiceController.getTopCustomersByRevenue(5);
        for (Map<String, Object> customer : topCustomers) {
            customersDataset.addValue(((BigDecimal)customer.get("total_revenue")).doubleValue(),
                "Doanh thu", customer.get("users_id").toString());
        }
        JFreeChart customersChart = ChartFactory.createBarChart(
            "Top khách hàng theo doanh thu",
            "Khách hàng (ID)",
            "Doanh thu (VNĐ)",
            customersDataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        topCustomersPanel = new ChartPanel(customersChart);
        topCustomersPanel.setPreferredSize(new Dimension(400, 300));

        // Top invoices (table)
        List<Map<String, Object>> topInvoices = invoiceController.getTopInvoices(5);
        String[] columns = {"Mã hóa đơn", "Khách hàng (ID)", "Tổng tiền", "Ngày tạo"};
        Object[][] data = new Object[topInvoices.size()][4];
        for (int i = 0; i < topInvoices.size(); i++) {
            Map<String, Object> invoice = topInvoices.get(i);
            data[i][0] = invoice.get("invoice_id");
            data[i][1] = invoice.get("users_id");
            data[i][2] = invoice.get("total_amount");
            data[i][3] = invoice.get("created_at");
        }
        JTable invoiceTable = new JTable(data, columns);
        JScrollPane invoiceScroll = new JScrollPane(invoiceTable);
        invoiceScroll.setPreferredSize(new Dimension(400, 150));
        topInvoicesPanel = new JPanel(new BorderLayout());
        topInvoicesPanel.add(new JLabel("Top hóa đơn giá trị lớn nhất", SwingConstants.CENTER), BorderLayout.NORTH);
        topInvoicesPanel.add(invoiceScroll, BorderLayout.CENTER);
        topInvoicesPanel.setPreferredSize(new Dimension(400, 200));

        // Invoice status ratio (pie chart)
        org.jfree.data.general.DefaultPieDataset pieDataset = new org.jfree.data.general.DefaultPieDataset();
        List<ChartDataModel> statusData = invoiceController.getInvoiceStatusRatio();
        for (ChartDataModel dataModel : statusData) {
            pieDataset.setValue(dataModel.getLabel(), dataModel.getValue());
        }
        JFreeChart statusChart = ChartFactory.createPieChart(
            "Tỷ lệ hóa đơn đã thanh toán/Chưa thanh toán",
            pieDataset,
            true,
            true,
            false
        );
        invoiceStatusPanel = new ChartPanel(statusChart);
        invoiceStatusPanel.setPreferredSize(new Dimension(400, 300));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spAdmin = new javax.swing.JScrollPane();
        panelAdmin = new javax.swing.JPanel();
        panelStatistic = new javax.swing.JPanel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        spAdmin.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        spAdmin.setToolTipText("");
        spAdmin.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        spAdmin.setViewportView(panelAdmin);

        panelStatistic.setLayout(new javax.swing.BoxLayout(panelStatistic, javax.swing.BoxLayout.X_AXIS));

        javax.swing.GroupLayout panelAdminLayout = new javax.swing.GroupLayout(panelAdmin);
        panelAdmin.setLayout(panelAdminLayout);
        panelAdminLayout.setHorizontalGroup(
            panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelStatistic, javax.swing.GroupLayout.DEFAULT_SIZE, 835, Short.MAX_VALUE)
        );
        panelAdminLayout.setVerticalGroup(
            panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAdminLayout.createSequentialGroup()
                .addComponent(panelStatistic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(441, Short.MAX_VALUE))
        );

        spAdmin.setViewportView(panelAdmin);

        add(spAdmin);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panelAdmin;
    private javax.swing.JPanel panelStatistic;
    private javax.swing.JScrollPane spAdmin;
    // End of variables declaration//GEN-END:variables

}
