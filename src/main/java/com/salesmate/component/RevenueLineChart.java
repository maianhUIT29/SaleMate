package com.salesmate.component;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import com.salesmate.model.RevenueLineChartModel;
import com.salesmate.controller.InvoiceController;

/**
 * JPanel hiển thị doanh thu theo từng ngày trong tháng hiện tại.
 */
public class RevenueLineChart extends JPanel {
    private DefaultCategoryDataset dataset;
    private ChartPanel chartPanel;
    private InvoiceController controller;

    public RevenueLineChart() {
        controller = new InvoiceController();
        initChart();
        loadData();
    }

    private void initChart() {
        dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createLineChart(
            "Doanh thu theo ngày trong tháng", // Tiêu đề biểu đồ
            "Ngày",                            // Nhãn trục X
            "Doanh thu (VNĐ)",                 // Nhãn trục Y
            dataset                             // Dữ liệu
        );
        chartPanel = new ChartPanel(chart);
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
    }

    /**
     * Lấy dữ liệu doanh thu theo ngày và nạp vào biểu đồ.
     */
    private void loadData() {
        // Giả sử controller.getDailyRevenue() trả về List<RevenueLineChartModel>
        // với name = "1", "2", ... và value = tổng doanh thu mỗi ngày.
        List<RevenueLineChartModel> raw = controller.getDailyRevenue();
        Map<String, Double> map = raw.stream()
            .collect(Collectors.toMap(RevenueLineChartModel::getName, RevenueLineChartModel::getValue));

        dataset.clear();
        YearMonth now = YearMonth.now();
        int days = now.lengthOfMonth();
        for (int day = 1; day <= days; day++) {
            String dayStr = String.valueOf(day);
            double value = map.getOrDefault(dayStr, 0.0);
            dataset.addValue(value, "Doanh thu", dayStr);
        }
    }

    /**
     * Cập nhật lại dữ liệu và vẽ lại biểu đồ.
     */
    public void refresh() {
        loadData();
        chartPanel.getChart().fireChartChanged();
    }
}
