package com.salesmate.component;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import com.salesmate.controller.InvoiceController;
import com.salesmate.controller.ProductController;
import com.salesmate.controller.UserController;
import com.salesmate.model.ChartDataModel;

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
    private JPanel lowStockPanel;
    private JPanel paymentMethodPanel;
    private JPanel userRolePanel;
    
    // Filter components
    private JComboBox<String> timeRangeCombo;
    private JComboBox<Integer> yearCombo;
    private JComboBox<Integer> monthCombo;
    private JPanel filterPanel;
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(231, 76, 60);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color LIGHT_TEXT = new Color(255, 255, 255);
    private final Color CARD_COLOR = new Color(255, 255, 255);
    private final Color BORDER_COLOR = new Color(189, 195, 199);

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
        // Set background color for the main panel
        setBackground(BACKGROUND_COLOR);
        panelAdmin.setBackground(BACKGROUND_COLOR);
        panelStatistic.setBackground(BACKGROUND_COLOR);
        
        // Apply modern chart theme
        ChartTheme theme = StandardChartTheme.createJFreeTheme();
        ChartFactory.setChartTheme(theme);
        
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
        panelStatistic.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add title
        JLabel dashboardTitle = new JLabel("Bảng điều khiển");
        dashboardTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        dashboardTitle.setForeground(PRIMARY_COLOR);
        dashboardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panelStatistic.add(dashboardTitle);
        
        // Add statistics panels in a row with proper spacing
        JPanel statsRow = new JPanel();
        statsRow.setLayout(new GridLayout(1, 4, 15, 0));
        statsRow.setBackground(BACKGROUND_COLOR);
        statsRow.add(employeePanel);
        statsRow.add(productPanel);
        statsRow.add(invoicePanel);
        statsRow.add(revenuePanel);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panelStatistic.add(statsRow);
        
        // Add spacing
        panelStatistic.add(Box.createVerticalStrut(20));
        
        // Add revenue widgets
        JPanel revenueWidgetsRow = new JPanel();
        revenueWidgetsRow.setLayout(new GridLayout(1, 2, 15, 0));
        revenueWidgetsRow.setBackground(BACKGROUND_COLOR);
        revenueWidgetsRow.add(createDailyRevenueWidget());
        revenueWidgetsRow.add(createMonthlyRevenueWidget());
        revenueWidgetsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        revenueWidgetsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panelStatistic.add(revenueWidgetsRow);
        
        // Add spacing
        panelStatistic.add(Box.createVerticalStrut(20));
        
        // Add title for charts section
        JLabel chartsTitle = new JLabel("Phân tích & Thống kê");
        chartsTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        chartsTitle.setForeground(PRIMARY_COLOR);
        chartsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        chartsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelStatistic.add(chartsTitle);
        
        // Add filter panel and revenue chart in one container
        JPanel revenueChartContainer = new JPanel(new BorderLayout(0, 10));
        revenueChartContainer.setBackground(BACKGROUND_COLOR);
        revenueChartContainer.add(filterPanel, BorderLayout.NORTH);
        revenueChartContainer.add(createPanelWithBorder(revenueChartPanel, "Biểu đồ doanh thu"), BorderLayout.CENTER);
        revenueChartContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelStatistic.add(revenueChartContainer);
        
        // Add spacing
        panelStatistic.add(Box.createVerticalStrut(15));
        
        // Add top products chart
        JPanel topProductsContainer = new JPanel(new BorderLayout());
        topProductsContainer.setBackground(BACKGROUND_COLOR);
        topProductsContainer.add(createPanelWithBorder(topProductsPanel, "Sản phẩm bán chạy"), BorderLayout.CENTER);
        topProductsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelStatistic.add(topProductsContainer);
        
        // Add spacing
        panelStatistic.add(Box.createVerticalStrut(15));
        
        // Add top customers chart
        JPanel topCustomersContainer = new JPanel(new BorderLayout());
        topCustomersContainer.setBackground(BACKGROUND_COLOR);
        topCustomersContainer.add(createPanelWithBorder(topCustomersPanel, "Top nhân viên"), BorderLayout.CENTER);
        topCustomersContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelStatistic.add(topCustomersContainer);
        
        // Add spacing
        panelStatistic.add(Box.createVerticalStrut(15));
        
        // Add payment method and user role charts in a row
        JPanel paymentAndRoleRow = new JPanel(new GridLayout(1, 2, 15, 0));
        paymentAndRoleRow.setBackground(BACKGROUND_COLOR);
        paymentAndRoleRow.add(createPanelWithBorder(paymentMethodPanel, "Phương thức thanh toán"));
        paymentAndRoleRow.add(createPanelWithBorder(userRolePanel, "Loại nhân viên"));
        paymentAndRoleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentAndRoleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        panelStatistic.add(paymentAndRoleRow);
        
        // Add spacing
        panelStatistic.add(Box.createVerticalStrut(20));
        
        // Add title for tables section
        JLabel tablesTitle = new JLabel("Báo cáo chi tiết");
        tablesTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tablesTitle.setForeground(PRIMARY_COLOR);
        tablesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        tablesTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelStatistic.add(tablesTitle);
        
        // Add top invoices table with better styling
        createTopInvoicesPanel();
        topInvoicesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topInvoicesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        panelStatistic.add(topInvoicesPanel);
        
        // Add spacing
        panelStatistic.add(Box.createVerticalStrut(20));
        
        // Add low stock prediction table with better styling
        createLowStockPanel();
        lowStockPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lowStockPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        panelStatistic.add(lowStockPanel);
    }

    private JPanel createPanelWithBorder(JPanel contentPanel, String title) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(CARD_COLOR);
        container.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        container.add(titleLabel, BorderLayout.NORTH);
        container.add(contentPanel, BorderLayout.CENTER);
        
        return container;
    }

    private void createStatisticsPanels() {
        // Employee count panel
        employeePanel = createStatPanel("Nhân viên", 
            String.valueOf(userController.countUser()),
            "Tổng số nhân viên", 
            PRIMARY_COLOR);

        // Product count panel
        productPanel = createStatPanel("Sản phẩm", 
            String.valueOf(productController.countProduct()), 
            "Tổng số sản phẩm",
            SECONDARY_COLOR);

        // Invoice count panel
        invoicePanel = createStatPanel("Hóa đơn", 
            String.valueOf(invoiceController.countInvoices()), 
            "Tổng số hóa đơn",
            ACCENT_COLOR);

        // Total revenue panel
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String totalRevenue = formatter.format(invoiceController.getTotalRevenue());
        revenuePanel = createStatPanel("Doanh thu", 
            totalRevenue, 
            "Tổng doanh thu tất cả hóa đơn",
            new Color(46, 204, 113)); // Green color for revenue
    }

    private JPanel createStatPanel(String title, String value, String description, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Add a color indicator on the left side
        JPanel colorStrip = new JPanel();
        colorStrip.setBackground(accentColor);
        colorStrip.setPreferredSize(new Dimension(5, 0));
        panel.add(colorStrip, BorderLayout.WEST);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.setBorder(new EmptyBorder(0, 10, 0, 0)); // Add left padding for better appearance
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(accentColor);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT_COLOR);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_COLOR);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(descLabel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void createRevenueWidgets() {
        // Daily revenue widget gets created when needed
        // Monthly revenue widget gets created when needed
    }

    private JPanel createDailyRevenueWidget() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String dailyRevenue = formatter.format(invoiceController.getTodayRevenue());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        
        // Add a color indicator on the left side
        JPanel colorStrip = new JPanel();
        colorStrip.setBackground(SECONDARY_COLOR);
        colorStrip.setPreferredSize(new Dimension(5, 0));
        panel.add(colorStrip, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel("Doanh thu hôm nay");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(SECONDARY_COLOR);
        
        JLabel valueLabel = new JLabel(dailyRevenue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT_COLOR);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(valueLabel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createMonthlyRevenueWidget() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String monthlyRevenue = formatter.format(invoiceController.getCurrentMonthRevenue());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        
        // Add a color indicator on the left side
        JPanel colorStrip = new JPanel();
        colorStrip.setBackground(PRIMARY_COLOR);
        colorStrip.setPreferredSize(new Dimension(5, 0));
        panel.add(colorStrip, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel("Doanh thu tháng này");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JLabel valueLabel = new JLabel(monthlyRevenue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT_COLOR);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(valueLabel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void createFilterPanel() {
        filterPanel = new JPanel();
        filterPanel.setBackground(CARD_COLOR);
        filterPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
        
        // Time range filter
        String[] timeRanges = {"Theo tháng", "Theo năm", "Theo tuần"};
        timeRangeCombo = new JComboBox<>(timeRanges);
        timeRangeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        timeRangeCombo.setPreferredSize(new Dimension(120, 30));
        timeRangeCombo.setBackground(Color.WHITE);
        timeRangeCombo.setForeground(TEXT_COLOR);
        timeRangeCombo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, 
                                                         boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(PRIMARY_COLOR);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(TEXT_COLOR);
                }
                return c;
            }
        });
        timeRangeCombo.addActionListener(e -> {
            updateCharts();
        });
        
        // Year filter
        yearCombo = new JComboBox<>();
        yearCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        yearCombo.setPreferredSize(new Dimension(100, 30));
        yearCombo.setBackground(Color.WHITE);
        yearCombo.setForeground(TEXT_COLOR);
        yearCombo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, 
                                                         boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(PRIMARY_COLOR);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(TEXT_COLOR);
                }
                return c;
            }
        });
        updateYearCombo();
        yearCombo.addActionListener(e -> updateCharts());
        
        JLabel filterTitle = new JLabel("Lọc dữ liệu:");
        filterTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterTitle.setForeground(PRIMARY_COLOR);
        
        JLabel timeLabel = new JLabel("Xem theo:");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JLabel yearLabel = new JLabel("Năm:");
        yearLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        filterPanel.add(filterTitle);
        filterPanel.add(timeLabel);
        filterPanel.add(timeRangeCombo);
        filterPanel.add(yearLabel);
        filterPanel.add(yearCombo);
        
        JButton applyButton = createStyledButton("Áp dụng", PRIMARY_COLOR, Color.WHITE);
        applyButton.addActionListener(e -> updateCharts());
        filterPanel.add(applyButton);
    }

    // Helper method to create consistently styled buttons that properly display colors
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g.setColor(bgColor.brighter());
                } else {
                    g.setColor(bgColor);
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false); // Important for custom painting
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return button;
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
        
        if (selectedYear == null) return;
        
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
                List<ChartDataModel> weeklyData = invoiceController.getWeeklyRevenueByYear(selectedYear);
                for (ChartDataModel data : weeklyData) {
                    dataset.addValue(data.getValue().doubleValue(), "Doanh thu", data.getLabel());
                }
                title = "Doanh thu theo tuần - " + selectedYear;
                break;
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            title,
            "Thời gian",
            "Doanh thu (Triệu VNĐ)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Customize chart appearance
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        chart.getTitle().setPaint(PRIMARY_COLOR);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        
        // Format Y-axis to show values in millions
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                return new StringBuffer(Math.round(number / 1000000) + "");
            }
            
            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                return format((double) number, toAppendTo, pos);
            }
            
            @Override
            public Number parse(String source, java.text.ParsePosition parsePosition) {
                return null;
            }
        });
        
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, PRIMARY_COLOR);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesFilled(0, true);
        
        plot.setRenderer(renderer);
        
        revenueChartPanel.removeAll();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        revenueChartPanel.add(chartPanel);
        revenueChartPanel.revalidate();
        revenueChartPanel.repaint();
    }

    private void createCharts() {
        // Revenue chart
        revenueChartPanel = new JPanel(new BorderLayout());
        revenueChartPanel.setBackground(Color.WHITE);
        
        DefaultCategoryDataset revenueDataset = new DefaultCategoryDataset();
        List<ChartDataModel> yearlyData = invoiceController.getYearlyRevenue();
        for (ChartDataModel data : yearlyData) {
            revenueDataset.addValue(data.getValue().doubleValue(), "Doanh thu", data.getLabel());
        }
        
        JFreeChart revenueChart = ChartFactory.createLineChart(
            "Doanh thu theo năm",
            "Năm",
            "Doanh thu (Triệu VNĐ)",
            revenueDataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Style the revenue chart
        revenueChart.setBackgroundPaint(Color.WHITE);
        revenueChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        revenueChart.getTitle().setPaint(PRIMARY_COLOR);
        
        CategoryPlot revenuePlot = revenueChart.getCategoryPlot();
        revenuePlot.setBackgroundPaint(Color.WHITE);
        revenuePlot.setDomainGridlinePaint(new Color(220, 220, 220));
        revenuePlot.setRangeGridlinePaint(new Color(220, 220, 220));
        
        // Format Y-axis to show values in millions
        NumberAxis rangeAxis = (NumberAxis) revenuePlot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                return new StringBuffer(Math.round(number / 1000000) + "");
            }
            
            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                return format((double) number, toAppendTo, pos);
            }
            
            @Override
            public Number parse(String source, java.text.ParsePosition parsePosition) {
                return null;
            }
        });
        
        LineAndShapeRenderer revenueRenderer = new LineAndShapeRenderer();
        revenueRenderer.setSeriesPaint(0, PRIMARY_COLOR);
        revenueRenderer.setSeriesStroke(0, new BasicStroke(2.0f));
        revenueRenderer.setSeriesShapesVisible(0, true);
        revenueRenderer.setSeriesShapesFilled(0, true);
        
        revenuePlot.setRenderer(revenueRenderer);
        
        ChartPanel revenueCP = new ChartPanel(revenueChart);
        revenueCP.setPreferredSize(new Dimension(400, 300));
        revenueChartPanel.add(revenueCP);
        
        // Top products chart
        topProductsPanel = new JPanel(new BorderLayout());
        topProductsPanel.setBackground(Color.WHITE);
        
        DefaultCategoryDataset productsDataset = new DefaultCategoryDataset();
        List<Map<String, Object>> topProducts = productController.getTopSellingProducts();
        for (Map<String, Object> product : topProducts) {
            productsDataset.addValue((Integer)product.get("quantity"), 
                "Số lượng", truncateString(product.get("product_name").toString(), 15));
        }
        
        JFreeChart productsChart = ChartFactory.createBarChart(
            "Sản phẩm bán chạy",
            "Sản phẩm",
            "Số lượng",
            productsDataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        // Style the products chart
        productsChart.setBackgroundPaint(Color.WHITE);
        productsChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        productsChart.getTitle().setPaint(PRIMARY_COLOR);
        
        CategoryPlot productsPlot = productsChart.getCategoryPlot();
        productsPlot.setBackgroundPaint(Color.WHITE);
        productsPlot.setDomainGridlinePaint(new Color(220, 220, 220));
        productsPlot.setRangeGridlinePaint(new Color(220, 220, 220));
        
        BarRenderer productRenderer = (BarRenderer) productsPlot.getRenderer();
        productRenderer.setSeriesPaint(0, SECONDARY_COLOR);
        productRenderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        productRenderer.setShadowVisible(false);
        
        ChartPanel productsCP = new ChartPanel(productsChart);
        productsCP.setPreferredSize(new Dimension(400, 300));
        topProductsPanel.add(productsCP);
        
        // Top employees by revenue (bar chart)
        topCustomersPanel = new JPanel(new BorderLayout());
        topCustomersPanel.setBackground(Color.WHITE);
        
        DefaultCategoryDataset employeesDataset = new DefaultCategoryDataset();
        List<Map<String, Object>> topEmployees = invoiceController.getTopEmployeesByRevenue(5);
        for (Map<String, Object> emp : topEmployees) {
            double revenueInMillions = ((BigDecimal)emp.get("total_revenue")).doubleValue();
            String label = emp.get("username") + " (" + emp.get("users_id") + ")";
            employeesDataset.addValue(revenueInMillions, "Doanh thu", label);
        }
        
        JFreeChart employeesChart = ChartFactory.createBarChart(
            "Top nhân viên theo doanh thu",
            "Nhân viên (Tên & ID)",
            "Doanh thu (Triệu VNĐ)",
            employeesDataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        
        // Style the employees chart
        employeesChart.setBackgroundPaint(Color.WHITE);
        employeesChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        employeesChart.getTitle().setPaint(PRIMARY_COLOR);
        
        CategoryPlot employeesPlot = employeesChart.getCategoryPlot();
        employeesPlot.setBackgroundPaint(Color.WHITE);
        employeesPlot.setDomainGridlinePaint(new Color(220, 220, 220));
        employeesPlot.setRangeGridlinePaint(new Color(220, 220, 220));
        
        // Format Y-axis to show values in millions like the revenue chart
        NumberAxis empRangeAxis = (NumberAxis) employeesPlot.getRangeAxis();
        empRangeAxis.setNumberFormatOverride(new java.text.NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                return new StringBuffer(Math.round(number / 1000000) + "");
            }
            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, java.text.FieldPosition pos) {
                return format((double) number, toAppendTo, pos);
            }
            @Override
            public Number parse(String source, java.text.ParsePosition parsePosition) {
                return null;
            }
        });
        
        BarRenderer empRenderer = (BarRenderer) employeesPlot.getRenderer();
        empRenderer.setSeriesPaint(0, new Color(41, 128, 185));
        empRenderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        empRenderer.setShadowVisible(false);
        empRenderer.setItemLabelGenerator(new org.jfree.chart.labels.StandardCategoryItemLabelGenerator());
        empRenderer.setItemLabelsVisible(true);
        empRenderer.setItemLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        empRenderer.setItemLabelPaint(Color.WHITE);
        empRenderer.setPositiveItemLabelPosition(new org.jfree.chart.labels.ItemLabelPosition(
            org.jfree.chart.labels.ItemLabelAnchor.CENTER,
            org.jfree.ui.TextAnchor.CENTER
        ));
        
        ChartPanel employeesCP = new ChartPanel(employeesChart);
        employeesCP.setPreferredSize(new Dimension(400, 300));
        topCustomersPanel.add(employeesCP);

        // 1. Payment method pie chart
        paymentMethodPanel = new JPanel(new BorderLayout());
        paymentMethodPanel.setBackground(Color.WHITE);
        org.jfree.data.general.DefaultPieDataset paymentDataset = new org.jfree.data.general.DefaultPieDataset();
        List<ChartDataModel> paymentData = invoiceController.getInvoiceCountByPaymentMethod();
        for (ChartDataModel data : paymentData) {
            paymentDataset.setValue(data.getLabel(), data.getValue());
        }
        JFreeChart paymentChart = ChartFactory.createPieChart(
            "Phương thức thanh toán",
            paymentDataset,
            true,
            true,
            false
        );
        paymentChart.setBackgroundPaint(Color.WHITE);
        paymentChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        paymentChart.getTitle().setPaint(SECONDARY_COLOR);
        PiePlot paymentPlot = (PiePlot) paymentChart.getPlot();
        paymentPlot.setBackgroundPaint(Color.WHITE);
        paymentPlot.setOutlineVisible(false);
        paymentPlot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        paymentPlot.setLabelBackgroundPaint(Color.WHITE);
        ChartPanel paymentCP = new ChartPanel(paymentChart);
        paymentCP.setPreferredSize(new Dimension(400, 300));
        paymentMethodPanel.add(paymentCP);

        // 2. User role pie chart
        userRolePanel = new JPanel(new BorderLayout());
        userRolePanel.setBackground(Color.WHITE);
        org.jfree.data.general.DefaultPieDataset roleDataset = new org.jfree.data.general.DefaultPieDataset();
        List<ChartDataModel> roleData = userController.getUserCountByRole();
        for (ChartDataModel data : roleData) {
            roleDataset.setValue(data.getLabel(), data.getValue());
        }
        JFreeChart roleChart = ChartFactory.createPieChart(
            "Loại nhân viên",
            roleDataset,
            true,
            true,
            false
        );
        roleChart.setBackgroundPaint(Color.WHITE);
        roleChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleChart.getTitle().setPaint(new Color(46, 204, 113));
        PiePlot rolePlot = (PiePlot) roleChart.getPlot();
        rolePlot.setBackgroundPaint(Color.WHITE);
        rolePlot.setOutlineVisible(false);
        rolePlot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        rolePlot.setLabelBackgroundPaint(Color.WHITE);
        ChartPanel roleCP = new ChartPanel(roleChart);
        roleCP.setPreferredSize(new Dimension(400, 300));
        userRolePanel.add(roleCP);
    }
    
    private void createTopInvoicesPanel() {
        // Create top invoices panel with table
        topInvoicesPanel = new JPanel(new BorderLayout());
        topInvoicesPanel.setBackground(CARD_COLOR);
        topInvoicesPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Top hóa đơn giá trị lớn nhất");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Create table with data
        List<Map<String, Object>> topInvoices = invoiceController.getTopInvoices(5);
        String[] columns = {"Mã hóa đơn", "Người lập hóa đơn", "Tổng tiền", "Ngày tạo"};
        Object[][] data = new Object[topInvoices.size()][4];
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
        
        for (int i = 0; i < topInvoices.size(); i++) {
            Map<String, Object> invoice = topInvoices.get(i);
            data[i][0] = invoice.get("invoice_id");
            data[i][1] = invoice.get("users_id");
            
            BigDecimal amount = (BigDecimal) invoice.get("total_amount");
            data[i][2] = formatter.format(amount);
            
            java.sql.Date date = (java.sql.Date) invoice.get("created_at");
            data[i][3] = dateFormat.format(date);
        }
        
        JTable invoiceTable = new JTable(data, columns);
        styleTable(invoiceTable);
        
        JScrollPane invoiceScroll = new JScrollPane(invoiceTable);
        invoiceScroll.setBorder(BorderFactory.createEmptyBorder());
        
        topInvoicesPanel.add(titleLabel, BorderLayout.NORTH);
        topInvoicesPanel.add(invoiceScroll, BorderLayout.CENTER);
    }
    
    private void createLowStockPanel() {
        // Create low stock prediction panel with table
        lowStockPanel = new JPanel(new BorderLayout());
        lowStockPanel.setBackground(CARD_COLOR);
        lowStockPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Sản phẩm sắp hết hàng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Create table with data
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
        styleTable(lowStockTable);
        
        JScrollPane lowStockScroll = new JScrollPane(lowStockTable);
        lowStockScroll.setBorder(BorderFactory.createEmptyBorder());
        
        lowStockPanel.add(titleLabel, BorderLayout.NORTH);
        lowStockPanel.add(lowStockScroll, BorderLayout.CENTER);
    }
    
    private void styleTable(JTable table) {
        // Style header - fix header visibility issues
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getWidth(), 35)); // Increase header height
        
        // Fix header renderer to ensure text is visible
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setBackground(PRIMARY_COLOR);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(0, 5, 0, 5)
                ));
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });
        
        // Style table
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(220, 220, 220));
        table.setShowVerticalLines(true); // Show vertical lines for better readability
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(5, 0)); // Add horizontal spacing
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(232, 234, 246));
        table.setSelectionForeground(TEXT_COLOR);
        table.setFocusable(false);
        
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, false, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                
                // Left align for text (especially product names), center align for numbers
                int horizontalAlignment = SwingConstants.LEFT;
                if (column == 0 || column == 2 || column == 3) { // ID, quantities and numeric columns
                    horizontalAlignment = SwingConstants.CENTER;
                }
                ((DefaultTableCellRenderer)c).setHorizontalAlignment(horizontalAlignment);
                
                // Add padding
                setBorder(new EmptyBorder(0, 8, 0, 8));
                
                return c;
            }
        });
    }
    
    private String truncateString(String text, int length) {
        if (text.length() <= length) {
            return text;
        }
        return text.substring(0, length) + "...";
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
