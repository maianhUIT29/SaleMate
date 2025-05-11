package com.salesmate.component;

import com.salesmate.controller.InvoiceController;
import com.salesmate.controller.DetailController;
import com.salesmate.controller.UserController;
import com.salesmate.model.Detail;
import com.salesmate.model.Invoice;
import com.salesmate.model.User;
import com.salesmate.utils.ExportDialog;
import com.salesmate.utils.ExcelExporter;
import com.salesmate.utils.ExcelImporter;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.event.TableModelEvent;

public class AdInvoicePanel extends JPanel {
    private final InvoiceController controller       = new InvoiceController();
    private final DetailController detailController = new DetailController();
    private final UserController    userController  = new UserController(); 
    private final DefaultTableModel tableModel;
    private final JTable invoiceTable;
    private final JTextField searchField;
    private final JSpinner pageSpinner;
    private final JLabel totalPagesLabel;

    private Date filterFrom, filterTo;
    private String filterStatus = "All";
    private String currentSearch = "";
    private int currentPage = 1;
    private final int pageSize = 20;
    private int totalPages = 1;

    // Color scheme
    private static final Color PRIMARY_COLOR    = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR  = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR     = new Color(231, 76, 60);
    private static final Color EXPORT_COLOR     = new Color(155, 89, 182);
    private static final Color IMPORT_COLOR     = new Color(243, 156, 18);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR       = new Color(44, 62, 80);
    private static final Color LIGHT_TEXT       = new Color(255, 255, 255);
    private static final Color BORDER_COLOR     = new Color(189, 195, 199);

    public AdInvoicePanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JLabel title = new JLabel("Quản lý hóa đơn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY_COLOR);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND_COLOR);
        header.add(title, BorderLayout.WEST);

        // Toolbar buttons
        JButton editBtn    = createStyledButton("Sửa", SECONDARY_COLOR);
        JButton deleteBtn  = createStyledButton("Xóa", ACCENT_COLOR);
        JButton exportBtn  = createStyledButton("Xuất Excel", EXPORT_COLOR);
        JButton importBtn  = createStyledButton("Nhập Excel", IMPORT_COLOR);
        JButton refreshBtn = createStyledButton("Làm mới", new Color(52, 73, 94));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(BACKGROUND_COLOR);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(exportBtn);
        btnPanel.add(importBtn);
        btnPanel.add(refreshBtn);

        // Search field
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JLabel searchLabel = new JLabel("Tìm kiếm theo ID:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        // Date and status filters
        JDateChooser fromDate = new JDateChooser();
        fromDate.setDateFormatString("yyyy-MM-dd");
        JDateChooser toDate = new JDateChooser();
        toDate.setDateFormatString("yyyy-MM-dd");
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "Paid", "Unpaid"});
        JButton applyFilter = createStyledButton("Áp dụng", SECONDARY_COLOR);
        applyFilter.addActionListener(e -> {
            filterFrom = fromDate.getDate();
            filterTo   = toDate.getDate();
            filterStatus    = (String) statusFilter.getSelectedItem();
            currentSearch   = searchField.getText();
            currentPage     = 1;

            loadInvoices();
        });
        JPanel filter2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filter2.setBackground(BACKGROUND_COLOR);
        filter2.add(new JLabel("Từ ngày:"));    filter2.add(fromDate);
        filter2.add(new JLabel("Đến ngày:"));  filter2.add(toDate);
        filter2.add(new JLabel("Trạng thái:"));filter2.add(statusFilter);
        filter2.add(applyFilter);

        // Combine toolbar and filters
        JPanel topTools = new JPanel(new BorderLayout(10, 0));
        topTools.setBackground(BACKGROUND_COLOR);
        topTools.add(btnPanel, BorderLayout.WEST);
        JPanel rightTools = new JPanel(new BorderLayout());
        rightTools.setBackground(BACKGROUND_COLOR);
        rightTools.add(searchPanel, BorderLayout.NORTH);
        rightTools.add(filter2, BorderLayout.SOUTH);
        topTools.add(rightTools, BorderLayout.EAST);

        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(BACKGROUND_COLOR);
        north.add(header, BorderLayout.NORTH);
        north.add(topTools, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        // Initialize pagination spinner here
        pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        totalPagesLabel = new JLabel(" / 1");
        totalPagesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalPagesLabel.setForeground(TEXT_COLOR);

        // Table setup
        String[] cols = {"Mã hóa đơn", "Người lập hóa đơn", "Ngày", "Tổng tiền", "Trạng thái", "Chi tiết"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return col == 5;
            }
        };
        invoiceTable = new JTable(tableModel);
        invoiceTable.setRowHeight(40);
        invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.setRowSorter(new TableRowSorter<>(tableModel));
        styleHeader(invoiceTable);
        styleRows(invoiceTable);

        // Detail button column
        invoiceTable.getColumnModel().getColumn(5)
            .setCellRenderer(new ButtonRenderer());
        invoiceTable.getColumnModel().getColumn(5)
            .setCellEditor(new ButtonEditor(new JCheckBox()));

        add(new JScrollPane(invoiceTable), BorderLayout.CENTER);

        // Pagination controls
        JButton prev = createStyledButton("« Trước", SECONDARY_COLOR);
        JButton next = createStyledButton("Sau »", SECONDARY_COLOR);
        prev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                pageSpinner.setValue(currentPage);
                loadInvoices();
            }
        });
        next.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                pageSpinner.setValue(currentPage);
                loadInvoices();
            }
        });
        pageSpinner.addChangeListener(e -> {
            currentPage = (int) pageSpinner.getValue();
            loadInvoices();
        });

        JPanel paging = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        paging.setBackground(BACKGROUND_COLOR);
        paging.setBorder(new EmptyBorder(15, 0, 0, 0));
        paging.add(prev);
        paging.add(new JLabel("Trang:"));
        paging.add(pageSpinner);
        paging.add(totalPagesLabel);
        paging.add(next);
        add(paging, BorderLayout.SOUTH);

        // Other listeners
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteInvoice());
        refreshBtn.addActionListener(e -> loadInvoices());
        exportBtn.addActionListener(e -> exportToExcel());
        importBtn.addActionListener(e -> importFromExcel());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter.doClick(); }
            public void removeUpdate(DocumentEvent e) { applyFilter.doClick(); }
            public void changedUpdate(DocumentEvent e) { applyFilter.doClick(); }
        });

        // Initial load
        loadInvoices();
    }

    private void loadInvoices() {
        tableModel.setRowCount(0);
        List<Invoice> all = controller.getAllInvoices();
        List<Invoice> filtered = all.stream().filter(inv -> {
            boolean m1 = String.valueOf(inv.getInvoiceId()).contains(currentSearch)
                      || inv.getPaymentStatus().toLowerCase().contains(currentSearch.toLowerCase());
            boolean m2 = filterStatus.equals("All") || inv.getPaymentStatus().equals(filterStatus);
            boolean m3 = true;
            if (filterFrom != null) m3 = !inv.getCreatedAt().before(filterFrom);
            if (m3 && filterTo != null) m3 = !inv.getCreatedAt().after(filterTo);
            return m1 && m2 && m3;
        }).collect(Collectors.toList());

        int total = filtered.size();
        totalPages = Math.max((int)Math.ceil((double)total / pageSize), 1);
        currentPage = Math.min(Math.max(currentPage, 1), totalPages);
        pageSpinner.setModel(new SpinnerNumberModel(currentPage, 1, totalPages, 1));
        totalPagesLabel.setText(" / " + totalPages);

       int start = (currentPage - 1) * pageSize;
int end   = Math.min(start + pageSize, total);

for (Invoice inv : filtered.subList(start, end)) {
    // Lấy tên người lập tương ứng cho từng hóa đơn
    User u = userController.getUserById(inv.getUsersId());
    String username = (u != null ? u.getUsername() : "—");

    tableModel.addRow(new Object[]{
        inv.getInvoiceId(),
        username,
        inv.getCreatedAt(),
        inv.getTotal(),
        inv.getPaymentStatus(),
        "Xem chi tiết"
    });
}


        ((TableRowSorter<?>)invoiceTable.getRowSorter())
            .setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
    }
private void showEditDialog() {
    // 1. Lấy dòng được chọn
    int viewRow = invoiceTable.getSelectedRow();
    if (viewRow < 0) {
        JOptionPane.showMessageDialog(this, "Chọn hóa đơn để sửa");
        return;
    }
    int modelRow = invoiceTable.convertRowIndexToModel(viewRow);
    int invoiceId = (int) tableModel.getValueAt(modelRow, 0);

    // 2. Lấy đối tượng Invoice và danh sách Detail từ DB
    Invoice invoice = controller.getInvoiceById(invoiceId);
    List<Detail> originalDetails = detailController.getDetailsByInvoiceId(invoiceId);

    // 3. Tạo model cho bảng chi tiết
    String[] cols = {"detailId","Mã SP","Số lượng","Đơn giá","Thành tiền"};
    DefaultTableModel detailModel = new DefaultTableModel(cols, 0) {
        @Override public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    JTable detailTable = new JTable(detailModel);
    // Ẩn cột detailId
    detailTable.removeColumn(detailTable.getColumnModel().getColumn(0));
    for (Detail d : originalDetails) {
        detailModel.addRow(new Object[]{
            d.getDetailId(), d.getProductId(), d.getQuantity(),
            d.getPrice(), d.getTotal()
        });
    }

    // 4. Danh sách tạm để chứa thay đổi
    List<Detail> toAdd = new ArrayList<>();
    List<Detail> toUpdate = new ArrayList<>();
    List<Integer> toDelete = new ArrayList<>();

    // 5. Tạo dialog chính
    JDialog dialog = new JDialog(
        (Frame) SwingUtilities.getWindowAncestor(this),
        "Sửa hóa đơn #" + invoiceId, true
    );
    dialog.setLayout(new BorderLayout(10,10));
    dialog.getContentPane().setBackground(BACKGROUND_COLOR);

    // 6. Panel nút Thêm/Sửa/Xóa
    JButton addBtn = createStyledButton("Thêm", SECONDARY_COLOR);
    JButton editBtn = createStyledButton("Sửa", SECONDARY_COLOR);
    JButton deleteBtn = createStyledButton("Xóa", ACCENT_COLOR);
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    btnPanel.setBackground(BACKGROUND_COLOR);
    btnPanel.add(addBtn);
    btnPanel.add(editBtn);
    btnPanel.add(deleteBtn);

    // 7. ScrollPane cho bảng detail
    JScrollPane scroll = new JScrollPane(detailTable);

    // 8. Panel tổng tiền + Lưu/Hủy
    JLabel totalLabel = new JLabel("Tổng tiền: " + invoice.getTotal());
    JButton saveBtn = createStyledButton("Lưu", SECONDARY_COLOR);
    JButton cancelBtn = createStyledButton("Hủy", ACCENT_COLOR);
    JPanel bottom = new JPanel(new BorderLayout());
    bottom.setBackground(BACKGROUND_COLOR);
    bottom.add(totalLabel, BorderLayout.WEST);
    JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
    rightBtns.setBackground(BACKGROUND_COLOR);
    rightBtns.add(saveBtn);
    rightBtns.add(cancelBtn);
    bottom.add(rightBtns, BorderLayout.EAST);

    dialog.add(btnPanel, BorderLayout.NORTH);
    dialog.add(scroll, BorderLayout.CENTER);
    dialog.add(bottom, BorderLayout.SOUTH);

    // 9. Hàm tính lại tổng tiền và cập nhật label
    Runnable recalcTotal = () -> {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < detailModel.getRowCount(); i++) {
            sum = sum.add((BigDecimal) detailModel.getValueAt(i, 4));
        }
        invoice.setTotal(sum);
        totalLabel.setText("Tổng tiền: " + sum);
    };

    // === XỬ LÝ NÚT THÊM ===
    addBtn.addActionListener(e -> {
        JDialog dlg = new JDialog(dialog, "Thêm SP vào HĐ #" + invoiceId, true);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Các trường nhập liệu
        gbc.gridx = 0; gbc.gridy = 0;
        dlg.add(new JLabel("Mã SP:"), gbc);
        gbc.gridx = 1;
        JTextField pidField = new JTextField();
        dlg.add(pidField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dlg.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        JSpinner qtySpin = new JSpinner(new SpinnerNumberModel(1,1,9999,1));
        dlg.add(qtySpin, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dlg.add(new JLabel("Đơn giá:"), gbc);
        gbc.gridx = 1;
        JTextField priceField = new JTextField();
        dlg.add(priceField, gbc);

        // Nút Lưu/Hủy
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        pBtns.setBackground(BACKGROUND_COLOR);
        JButton ok = createStyledButton("Lưu", SECONDARY_COLOR);
        JButton cancel = createStyledButton("Hủy", ACCENT_COLOR);
        pBtns.add(ok); pBtns.add(cancel);
        dlg.add(pBtns, gbc);

        ok.addActionListener(ae -> {
            try {
                int pid = Integer.parseInt(pidField.getText().trim());
                int qty = (int) qtySpin.getValue();
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                BigDecimal tot = price.multiply(BigDecimal.valueOf(qty));
                // Tạo Detail mới
                Detail d = new Detail(0, invoiceId, pid, qty, price, tot);
                toAdd.add(d);
                detailModel.addRow(new Object[]{0, pid, qty, price, tot});
                recalcTotal.run();
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Nhập dữ liệu sai!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancel.addActionListener(ae -> dlg.dispose());

        dlg.pack();
        dlg.setLocationRelativeTo(dialog);
        dlg.setVisible(true);
    });

    // === XỬ LÝ NÚT SỬA ===
    editBtn.addActionListener(e -> {
        int r = detailTable.getSelectedRow();
        if (r < 0) return;
        int detailId = (int) detailModel.getValueAt(r, 0);
        int oldPid   = (int) detailModel.getValueAt(r, 1);
        int oldQty   = (int) detailModel.getValueAt(r, 2);
        BigDecimal oldPrice = (BigDecimal) detailModel.getValueAt(r, 3);

        JDialog dlg = new JDialog(dialog, "Sửa SP trong HĐ", true);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã SP
        gbc.gridx = 0; gbc.gridy = 0;
        dlg.add(new JLabel("Mã SP:"), gbc);
        gbc.gridx = 1;
        JTextField pidField = new JTextField(String.valueOf(oldPid));
        dlg.add(pidField, gbc);

        // Số lượng
        gbc.gridx = 0; gbc.gridy = 1;
        dlg.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        JSpinner qtySpin = new JSpinner(new SpinnerNumberModel(oldQty,1,9999,1));
        dlg.add(qtySpin, gbc);

        // Đơn giá
        gbc.gridx = 0; gbc.gridy = 2;
        dlg.add(new JLabel("Đơn giá:"), gbc);
        gbc.gridx = 1;
        JTextField priceField = new JTextField(oldPrice.toString());
        dlg.add(priceField, gbc);

        // Nút Lưu/Hủy
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        pBtns.setBackground(BACKGROUND_COLOR);
        JButton ok = createStyledButton("Lưu", SECONDARY_COLOR);
        JButton cancel = createStyledButton("Hủy", ACCENT_COLOR);
        pBtns.add(ok); pBtns.add(cancel);
        dlg.add(pBtns, gbc);

        ok.addActionListener(ae -> {
            try {
                int pid = Integer.parseInt(pidField.getText().trim());
                int qty = (int) qtySpin.getValue();
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                BigDecimal tot = price.multiply(BigDecimal.valueOf(qty));
                Detail d = new Detail(detailId, invoiceId, pid, qty, price, tot);
                if (detailId == 0) {
                    // chỉ cập nhật trong toAdd
                    toAdd.removeIf(x-> x.getDetailId()==0 && x.getProductId()==oldPid);
                    toAdd.add(d);
                } else {
                    toUpdate.removeIf(x-> x.getDetailId()==detailId);
                    toUpdate.add(d);
                }
                detailModel.setValueAt(pid,   r,1);
                detailModel.setValueAt(qty,   r,2);
                detailModel.setValueAt(price, r,3);
                detailModel.setValueAt(tot,   r,4);
                recalcTotal.run();
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Dữ liệu không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancel.addActionListener(ae -> dlg.dispose());

        dlg.pack();
        dlg.setLocationRelativeTo(dialog);
        dlg.setVisible(true);
    });

    // === XỬ LÝ NÚT XÓA ===
    deleteBtn.addActionListener(e -> {
        int r = detailTable.getSelectedRow();
        if (r < 0) return;
        int detailId = (int) detailModel.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(dialog, "Xóa sản phẩm này?", "Xác nhận", JOptionPane.YES_NO_OPTION)
            != JOptionPane.YES_OPTION) return;
        if (detailId == 0) {
            toAdd.removeIf(d-> d.getDetailId()==0 &&
                               (int)detailModel.getValueAt(r,1)==d.getProductId());
        } else {
            toDelete.add(detailId);
            toUpdate.removeIf(d-> d.getDetailId()==detailId);
        }
        detailModel.removeRow(r);
        recalcTotal.run();
    });

    // === XỬ LÝ NÚT LƯU ===
    saveBtn.addActionListener(e -> {
        // Nếu xóa hết thì cả hoá đơn cũng xóa luôn
        if (detailModel.getRowCount() == 0) {
            controller.deleteInvoice(invoiceId);
            JOptionPane.showMessageDialog(dialog,
                "Hóa đơn đã bị xóa hoàn toàn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadInvoices();
            dialog.dispose();
            return;
        }
        // 1) Xóa chi tiết
        for (int id : toDelete) {
            boolean ok = detailController.deleteDetail(id);
            System.out.println("Deleted detail " + id + ": " + ok);
        }
        // 2) Cập nhật chi tiết
        for (Detail d : toUpdate) {
            boolean ok = detailController.updateDetail(d);
            System.out.println("Updated detail " + d.getDetailId() + ": " + ok);
        }
        // 3) Thêm chi tiết
        for (Detail d : toAdd) {
            boolean ok = detailController.addDetail(d);
            System.out.println("Added detail: " + ok);
        }
        // 4) Cập nhật tổng của hoá đơn
        boolean okInv = controller.updateInvoice(invoice);
        System.out.println("Updated invoice total: " + okInv);

        JOptionPane.showMessageDialog(dialog,
            "Lưu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        loadInvoices();
        dialog.dispose();
    });

    cancelBtn.addActionListener(e -> dialog.dispose());

    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}


private void deleteInvoice() {
    // 1) Lấy dòng được chọn trong view
    int viewRow = invoiceTable.getSelectedRow();
    if (viewRow < 0) {
        JOptionPane.showMessageDialog(this, "Chọn hóa đơn để xóa");
        return;
    }

    // 2) Chuyển thành chỉ số model (vì có sort/filter)
    int modelRow = invoiceTable.convertRowIndexToModel(viewRow);
    int invoiceId = (int) tableModel.getValueAt(modelRow, 0);

    // 3) Hỏi xác nhận
    int choice = JOptionPane.showConfirmDialog(
        this,
        "Xóa hóa đơn #" + invoiceId + " và tất cả chi tiết của nó?",
        "Xác nhận",
        JOptionPane.YES_NO_OPTION
    );
    if (choice != JOptionPane.YES_OPTION) {
        return;
    }

    // 4) Gọi controller (đã tự động xóa chi tiết rồi)
    boolean success = controller.deleteInvoice(invoiceId);

    // 5) Thông báo và làm mới
    if (success) {
        JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!");
        loadInvoices();
    } else {
        JOptionPane.showMessageDialog(
            this,
            "Xóa hóa đơn thất bại. Vui lòng thử lại.",
            "Lỗi",
            JOptionPane.ERROR_MESSAGE
        );
    }
}





    private void exportToExcel() {
        ExportDialog dlg = new ExportDialog((Frame)SwingUtilities.getWindowAncestor(this), invoiceTable);
        dlg.setVisible(true);
        if (!dlg.isExportConfirmed()) return;
        File f = dlg.showSaveDialog();
        if (f == null) return;
        try {
            if (dlg.isXLSX())
                ExcelExporter.exportToExcel(invoiceTable, f, dlg.includeHeaders(), dlg.getSelectedColumns());
            else
                ExcelExporter.exportToCSV(invoiceTable, f, dlg.includeHeaders(), dlg.getSelectedColumns());
            if (dlg.openAfterExport()) ExcelExporter.openFile(f);
            JOptionPane.showMessageDialog(this, "Xuất thành công");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importFromExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();
        try {
            if (!ExcelImporter.validateExcelFile(f)) {
                JOptionPane.showMessageDialog(this, "File không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // TODO: implement import mapping if needed
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi import: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDetailDialog(int invoiceId) {
        List<Detail> details = detailController.getDetailsByInvoiceId(invoiceId);
        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
                                  "Chi tiết hóa đơn #" + invoiceId, true);
        String[] cols = {"Mã sản phẩm", "Số lượng", "Giá", "Thành tiền"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        JTable t = new JTable(m);
        for (Detail d : details) {
            m.addRow(new Object[]{ d.getProductId(), d.getQuantity(), d.getPrice(), d.getTotal() });
        }
        dlg.add(new JScrollPane(t));
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // Renderer & Editor for detail button
    class ButtonRenderer extends JButton implements TableCellRenderer {
        ButtonRenderer() { setOpaque(true); setText("Xem chi tiết"); }
        @Override public Component getTableCellRendererComponent(JTable tbl, Object val,
                boolean isSelected, boolean hasFocus, int row, int col) {
            return this;
        }
    }
    class ButtonEditor extends DefaultCellEditor {
        private int currentRow;
        private final JButton btn = new JButton("Xem chi tiết");
        ButtonEditor(JCheckBox chk) {
            super(chk);
            btn.setOpaque(true);
            btn.addActionListener(e -> fireEditingStopped());
        }
        @Override public Component getTableCellEditorComponent(JTable tbl, Object val,
                boolean isSelected, int row, int col) {
            currentRow = row;
            return btn;
        }
        @Override public Object getCellEditorValue() {
            int invoiceId = (int) tableModel.getValueAt(currentRow, 0);
            showDetailDialog(invoiceId);
            return "Xem chi tiết";
        }
    }

    // Helper methods for styling
    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) g.setColor(bg.darker());
                else if (getModel().isRollover()) g.setColor(bg.brighter());
                else g.setColor(bg);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        b.setForeground(LIGHT_TEXT);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return b;
    }

    private void styleHeader(JTable tbl) {
        JTableHeader h = tbl.getTableHeader();
        h.setPreferredSize(new Dimension(h.getWidth(), 45));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setBackground(new Color(25, 79, 115));
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                return lbl;
            }
        });
    }

    private void styleRows(JTable tbl) {
        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean s, boolean f, int row, int col) {
                super.getTableCellRendererComponent(t, v, s, f, row, col);
                if (!s) setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                setHorizontalAlignment(col == 0 || col == 2 ? JLabel.CENTER : JLabel.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }
}
