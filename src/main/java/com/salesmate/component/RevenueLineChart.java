package com.salesmate.component;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.salesmate.controller.InvoiceController;


public class RevenueLineChart extends JPanel {
    private DefaultCategoryDataset dataset;
    private ChartPanel chartPanel;
    private InvoiceController invoiceController;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy");

    public RevenueLineChart() {
        initComponents();
        invoiceController = new InvoiceController();
    }

    private void initComponents() {
        // Initialize any other components if needed
    }
}
