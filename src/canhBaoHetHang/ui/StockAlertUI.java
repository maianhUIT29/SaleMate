package ui;

import dao.StockAlertDAO;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Lớp StockAlertUI hiển thị cảnh báo hết hàng trong bảng
public class StockAlertUI extends JFrame {

    private JTable table;  // Bảng hiển thị dữ liệu sản phẩm

    // Constructor: thiết lập giao diện người dùng
    public StockAlertUI() {
        setTitle("Cảnh báo hết hàng");  // Tiêu đề cửa sổ
        setSize(400, 300);  // Kích thước cửa sổ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Đóng ứng dụng khi cửa sổ đóng

        // Tạo bảng với các cột Mã SP, Tên sản phẩm, Số lượng
        String[] columns = {"Mã SP", "Tên sản phẩm", "Số lượng"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        // Lấy danh sách sản phẩm sắp hết hàng và thêm vào bảng
        StockAlertDAO dao = new StockAlertDAO();
        List<Product> products = dao.getLowStockProducts();
        for (Product product : products) {
            model.addRow(new Object[]{product.getId(), product.getName(), product.getQuantity()});
        }

        // Thêm bảng vào cửa sổ và hiển thị
        add(new JScrollPane(table), BorderLayout.CENTER);
        setVisible(true);
    }

    // Hàm main để chạy ứng dụng
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StockAlertUI::new);
    }
}
