package com.salesmate.view;

import com.salesmate.dao.StatisticsDAO;
import com.salesmate.controller.Statistics;

import javax.swing.*;
import java.awt.*;

public class StatisticsPanel extends JPanel {
    private JLabel lblEmployees, lblProducts, lblInvoices; // Các label để hiển thị số lượng nhân viên, sản phẩm và hóa đơn
    private StatisticsDAO dao = new StatisticsDAO(); // DAO để lấy dữ liệu từ CSDL

    public StatisticsPanel() {
        setLayout(new BorderLayout());

        // Panel hiển thị kết quả thống kê
        JPanel content = new JPanel(new GridLayout(2, 3, 10, 5));
        lblEmployees = new JLabel();
        lblProducts = new JLabel();
        lblInvoices = new JLabel();

        content.add(new JLabel("Nhân viên:"));
        content.add(new JLabel("Sản phẩm:"));
        content.add(new JLabel("Hóa đơn:"));
        content.add(lblEmployees);  // Số lượng nhân viên
        content.add(lblProducts);   // Số lượng sản phẩm
        content.add(lblInvoices);   // Số lượng hóa đơn

        // Gắn panel vào giao diện chính
        add(content, BorderLayout.CENTER);

        refresh();  // Gọi refresh khi lần đầu tiên hiển thị
    }

    // Phương thức cập nhật dữ liệu vào UI
    private void refresh() {
        // Lấy thống kê số lượng từ DAO
        Statistics s = dao.getStatistics(null, false); // Không lọc theo vai trò hay trạng thái thanh toán
        lblEmployees.setText(String.valueOf(s.getEmployeeCount()));  // Hiển thị số lượng nhân viên
        lblProducts.setText(String.valueOf(s.getProductCount()));    // Hiển thị số lượng sản phẩm
        lblInvoices.setText(String.valueOf(s.getInvoiceCount()));    // Hiển thị số lượng hóa đơn
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Thống kê số lượng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLocationRelativeTo(null);
            frame.add(new StatisticsPanel());  // Thêm StatisticsPanel vào JFrame
            frame.setVisible(true);  // Hiển thị cửa sổ
        });
    }
}
