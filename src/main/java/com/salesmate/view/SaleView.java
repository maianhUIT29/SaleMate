package com.salesmate.view;

import com.salesmate.configs.DBConnection;
import com.salesmate.controller.SaleController;
import com.salesmate.model.Sale;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class SaleView extends JFrame {
    private SaleController saleController;
    private JTable salesTable;

    public SaleView(SaleController saleController) {
        this.saleController = saleController;
        setTitle("Sản phẩm bán chạy nhất");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        salesTable = new JTable();
        add(new JScrollPane(salesTable), BorderLayout.CENTER);

        // Lấy dữ liệu và cập nhật bảng
        updateSalesData();
    }

    private void updateSalesData() {
        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();
        // Xác định ngày bắt đầu và kết thúc của tháng hiện tại
        Date startDate = Date.valueOf(currentDate.withDayOfMonth(1));
        Date endDate = Date.valueOf(currentDate.withDayOfMonth(currentDate.lengthOfMonth()));

        // Lấy danh sách sản phẩm bán chạy
        List<Sale> sales = saleController.getTopSellingProducts(startDate, endDate);
        if (sales != null && !sales.isEmpty()) {
            String[] columnNames = {"Tên sản phẩm", "Tổng số lượng bán"};
            Object[][] data = new Object[sales.size()][2];
            for (int i = 0; i < sales.size(); i++) {
                Sale sale = sales.get(i);
                data[i][0] = sale.getProductName();
                data[i][1] = sale.getTotalQuantitySold();
            }
            salesTable.setModel(new DefaultTableModel(data, columnNames));
        } else {
            // Hiển thị thông báo nếu không có dữ liệu
            JOptionPane.showMessageDialog(this, "Không có dữ liệu sản phẩm bán chạy.");
        }
    }

    public static void main(String[] args) {
        // Kết nối đến cơ sở dữ liệu
        Connection connection = DBConnection.getConnection();

        // Khởi tạo SaleController
        SaleController saleController = new SaleController(connection);

        // Hiển thị giao diện SaleView
        SaleView saleView = new SaleView(saleController);
        saleView.setVisible(true);
    }
}
