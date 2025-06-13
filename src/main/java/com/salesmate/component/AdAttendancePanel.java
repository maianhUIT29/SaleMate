package com.salesmate.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.salesmate.controller.AttendanceController;
import com.salesmate.model.Attendance;
import com.salesmate.model.AttendanceAdjustment;
import com.salesmate.model.LeaveRequest;
import com.toedter.calendar.JDateChooser;

public class AdAttendancePanel extends JPanel {
    private final AttendanceController attendanceController;
    private final DefaultTableModel tableModel;
    private final JTable attendanceTable;
    private final JTextField searchField;
    private final JDateChooser dateChooser;
    private final JComboBox<String> shiftFilter;
    private final JComboBox<String> statusFilter;
    private final JComboBox<String> adjustFilter;  // lọc Điều chỉnh
    private final JComboBox<String> leaveFilter;   // lọc Nghỉ phép
    private final JButton btnPrevPage;
    private final JButton btnNextPage;
    private final JLabel lblPageInfo;

    private int currentPage = 1;
    private int rowsPerPage = 10;
    private List<Attendance> allData = new ArrayList<>();

    // Modern color scheme
    private static final Color PRIMARY_COLOR      = new Color(25, 118, 210);    // Material Blue
    private static final Color SECONDARY_COLOR    = new Color(66, 165, 245);    // Lighter Blue
    private static final Color ACCENT_COLOR       = new Color(255, 82, 82);     // Modern Red
    private static final Color SUCCESS_COLOR      = new Color(76, 175, 80);     // Green
    private static final Color WARNING_COLOR      = new Color(255, 152, 0);     // Orange
    private static final Color BACKGROUND_COLOR   = new Color(250, 250, 250);   // Almost White
    private static final Color TABLE_HEADER_BG    = new Color(236, 239, 241);   // Light Gray Blue
    private static final Color TABLE_STRIPE       = new Color(245, 245, 245);   // Stripe row color
    private static final Color TEXT_COLOR         = new Color(33, 33, 33);      // Almost Black
    private static final Color LIGHT_TEXT         = new Color(255, 255, 255);   // White
    private static final Color BORDER_COLOR       = new Color(224, 224, 224);   // Light Gray
    private static final Color HOVER_COLOR        = new Color(236, 239, 241);   // Light Gray Blue
    
    // UI Constants
    private static final int BUTTON_RADIUS        = 8;     // Border radius for buttons
    private static final int PANEL_RADIUS         = 12;    // Border radius for panels
    private static final int DEFAULT_PADDING      = 15;    // Standard padding
    private static final int ROW_HEIGHT           = 40;    // Table row height
    private static final String FONT_FAMILY       = "Segoe UI"; // Modern font

    public AdAttendancePanel() {
        this.attendanceController = new AttendanceController();

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ======= PHẦN TIÊU ĐỀ =======
        JLabel titleLabel = new JLabel("Quản Lý Chấm Công");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // ======= KHỞI TẠO CÁC THÀNH PHẨN CHO BỘ LỌC (sẽ nằm trong Dialog) =======
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setPreferredSize(new Dimension(130, 30));

        shiftFilter = new JComboBox<>(new String[]{"Tất cả", "1", "2", "3"});
        shiftFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        statusFilter = new JComboBox<>(new String[]{"Tất cả", "Present", "Absent", "Late"});
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        adjustFilter = new JComboBox<>(new String[]{"Tất cả", "Có", "Không"});
        adjustFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        leaveFilter = new JComboBox<>(new String[]{"Tất cả", "Có", "Không"});
        leaveFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // ======= Ô tìm kiếm =======
        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        // ======= CÁC NÚT Ở HÀNG TRÊN =======
        JButton btnFilterDialog     = createStyledButton("Bộ Lọc", PRIMARY_COLOR);
        JButton btnRefresh          = createStyledButton("Làm Mới", ACCENT_COLOR);
        JButton btnViewAdjustments  = createStyledButton("Xem Điều Chỉnh", SECONDARY_COLOR);
        JButton btnViewLeave        = createStyledButton("Xem Nghỉ Phép", SECONDARY_COLOR);
        JButton btnEdit             = createStyledButton("Sửa", ACCENT_COLOR);

        // ======= TỔ CHỨC LAYOUT CHO PHẦN FILTER PANEL =======
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBackground(BACKGROUND_COLOR);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.add(btnFilterDialog);
        leftPanel.add(btnRefresh);
        leftPanel.add(btnViewAdjustments);
        leftPanel.add(btnViewLeave);
        leftPanel.add(btnEdit);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.add(new JLabel("Tìm kiếm:"));
        rightPanel.add(searchField);

        filterPanel.add(leftPanel, BorderLayout.WEST);
        filterPanel.add(rightPanel, BorderLayout.EAST);

        // ======= ĐƯA HEADER + FILTER PANEL VÀO GIAO DIỆN CHÍNH =======
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // ======= TẠO BẢNG DỮ LIỆU (có 2 cột cuối “Điều chỉnh” và “Nghỉ phép”) =======
        String[] columns = {
            "ID", "Employee ID", "Ngày", "Shift ID", "Check-In", "Check-Out",
            "Status", "Late (phút)", "Early Leave (phút)", "Working Hours", "Note",
            "Điều chỉnh", "Nghỉ phép"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(36);
        attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        attendanceTable.setRowSorter(new TableRowSorter<>(tableModel));
        attendanceTable.setShowGrid(true);
        attendanceTable.setGridColor(new Color(220, 220, 220));
        attendanceTable.setSelectionBackground(new Color(232, 234, 246));
        attendanceTable.setSelectionForeground(TEXT_COLOR);

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        add(scrollPane, BorderLayout.CENTER);

        // ======= PHẦN PHÂN TRANG =======
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        paginationPanel.setBackground(BACKGROUND_COLOR);
        btnPrevPage = createStyledButton("< Trang trước", SECONDARY_COLOR);
        btnNextPage = createStyledButton("Trang sau >", SECONDARY_COLOR);
        lblPageInfo = new JLabel("Trang 1");
        lblPageInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPageInfo.setForeground(TEXT_COLOR);
        paginationPanel.add(btnPrevPage);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNextPage);
        add(paginationPanel, BorderLayout.SOUTH);

        // ======= ACTION LISTENERS CHO CÁC NÚT =======

        // (1) Khi bấm “Bộ Lọc”: mở dialog chứa dateChooser + shiftFilter + statusFilter + adjustFilter + leaveFilter
        btnFilterDialog.addActionListener(e -> {
            JDialog filterDlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                             "Bộ Lọc", true);
            filterDlg.setLayout(new GridBagLayout());
            filterDlg.getContentPane().setBackground(BACKGROUND_COLOR);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.WEST;

            // 1) Label + dateChooser
            gbc.gridx = 0;  gbc.gridy = 0;
            filterDlg.add(new JLabel("Ngày:"), gbc);
            gbc.gridx = 1;
            filterDlg.add(dateChooser, gbc);

            // 2) Label + shiftFilter
            gbc.gridx = 0;  gbc.gridy = 1;
            filterDlg.add(new JLabel("Ca làm:"), gbc);
            gbc.gridx = 1;
            filterDlg.add(shiftFilter, gbc);

            // 3) Label + statusFilter
            gbc.gridx = 0;  gbc.gridy = 2;
            filterDlg.add(new JLabel("Trạng thái:"), gbc);
            gbc.gridx = 1;
            filterDlg.add(statusFilter, gbc);

            // 4) Label + adjustFilter
            gbc.gridx = 0;  gbc.gridy = 3;
            filterDlg.add(new JLabel("Điều chỉnh:"), gbc);
            gbc.gridx = 1;
            filterDlg.add(adjustFilter, gbc);

            // 5) Label + leaveFilter
            gbc.gridx = 0;  gbc.gridy = 4;
            filterDlg.add(new JLabel("Nghỉ phép:"), gbc);
            gbc.gridx = 1;
            filterDlg.add(leaveFilter, gbc);

            // Nút Áp dụng / Hủy
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            btnPanel.setBackground(BACKGROUND_COLOR);
            JButton okBtn     = createStyledButton("Áp dụng", PRIMARY_COLOR);
            JButton cancelBtn = createStyledButton("Hủy", ACCENT_COLOR);
            btnPanel.add(okBtn);
            btnPanel.add(cancelBtn);

            gbc.gridx = 0;    gbc.gridy = 5;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.EAST;
            filterDlg.add(btnPanel, gbc);

            okBtn.addActionListener(ev -> {
                currentPage = 1;
                loadFilteredData();
                filterDlg.dispose();
            });
            cancelBtn.addActionListener(ev -> filterDlg.dispose());

            filterDlg.pack();
            filterDlg.setLocationRelativeTo(this);
            filterDlg.setVisible(true);
        });

        // (2) Khi bấm “Làm Mới”: reset search + bộ lọc
        btnRefresh.addActionListener(e -> {
            searchField.setText("");
            dateChooser.setDate(null);
            shiftFilter.setSelectedIndex(0);
            statusFilter.setSelectedIndex(0);
            adjustFilter.setSelectedIndex(0);
            leaveFilter.setSelectedIndex(0);
            currentPage = 1;
            loadFilteredData();
        });

        // (3) Khi bấm “Xem Điều Chỉnh”: mở dialog hiển thị list điều chỉnh
        btnViewAdjustments.addActionListener(e -> {
            int selectedRow = attendanceTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một bản ghi chấm công để xem điều chỉnh.");
                return;
            }
            int modelRow    = attendanceTable.convertRowIndexToModel(selectedRow);
            int attendanceId = (int) tableModel.getValueAt(modelRow, 0);
            showAdjustmentDialog(attendanceId);
        });

        // (4) Khi bấm “Xem Nghỉ Phép”: mở dialog hiển thị list nghỉ phép
        btnViewLeave.addActionListener(e -> {
            int selectedRow = attendanceTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một bản ghi chấm công để xem nghỉ phép.");
                return;
            }
            int modelRow    = attendanceTable.convertRowIndexToModel(selectedRow);
            int employeeId  = (int) tableModel.getValueAt(modelRow, 1);
            List<LeaveRequest> leaveList =
                attendanceController.getLeaveRequestsByEmployeeId(employeeId);
            showLeaveRequestDialog(leaveList);
        });

        // (5) Khi bấm “Sửa”: mở dialog sửa dựa trên object từ allData
        btnEdit.addActionListener(e -> {
            int selectedRow = attendanceTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một bản ghi chấm công để sửa.");
                return;
            }
            int modelRow     = attendanceTable.convertRowIndexToModel(selectedRow);
            int dataIndex    = (currentPage - 1) * rowsPerPage + modelRow;
            Attendance a     = allData.get(dataIndex);

            JDialog editDlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                          "Sửa Chấm Công #" + a.getAttendanceId(), true);
            editDlg.setLayout(new BorderLayout());
            editDlg.setSize(400, 240);
            editDlg.setLocationRelativeTo(this);

            JPanel content = new JPanel(new GridBagLayout());
            content.setBorder(new EmptyBorder(10, 10, 10, 10));
            content.setBackground(BACKGROUND_COLOR);
            GridBagConstraints c2 = new GridBagConstraints();
            c2.insets = new Insets(5, 5, 5, 5);
            c2.anchor = GridBagConstraints.WEST;

            // Check-In
            c2.gridx = 0; c2.gridy = 0;
            content.add(new JLabel("Check-In (YYYY-MM-DD HH:MM:SS):"), c2);
            c2.gridx = 1;
            JTextField txtCheckIn = new JTextField(
                a.getCheckInTime() != null ? a.getCheckInTime().toString() : "", 15);
            content.add(txtCheckIn, c2);

            // Check-Out
            c2.gridx = 0; c2.gridy = 1;
            content.add(new JLabel("Check-Out (YYYY-MM-DD HH:MM:SS):"), c2);
            c2.gridx = 1;
            JTextField txtCheckOut = new JTextField(
                a.getCheckOutTime() != null ? a.getCheckOutTime().toString() : "", 15);
            content.add(txtCheckOut, c2);

            // Status
            c2.gridx = 0; c2.gridy = 2;
            content.add(new JLabel("Status:"), c2);
            c2.gridx = 1;
            JComboBox<String> cboStatus = new JComboBox<>(new String[]{"Present", "Absent", "Late"});
            cboStatus.setSelectedItem(a.getStatus());
            content.add(cboStatus, c2);

            // Nút Lưu / Hủy
            JPanel btnPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel2.setBackground(BACKGROUND_COLOR);
            JButton saveBtn   = createStyledButton("Lưu", PRIMARY_COLOR);
            JButton cancelBtn = createStyledButton("Hủy", ACCENT_COLOR);
            btnPanel2.add(saveBtn);
            btnPanel2.add(cancelBtn);

            saveBtn.addActionListener(ev -> {
                try {
                    String newCheckIn  = txtCheckIn.getText().trim();
                    String newCheckOut = txtCheckOut.getText().trim();
                    String newStatus   = Objects.requireNonNull(cboStatus.getSelectedItem()).toString();

                    if (!newCheckIn.isEmpty()) {
                        a.setCheckInTime(java.sql.Timestamp.valueOf(newCheckIn));
                    } else {
                        a.setCheckInTime(null);
                    }
                    if (!newCheckOut.isEmpty()) {
                        a.setCheckOutTime(java.sql.Timestamp.valueOf(newCheckOut));
                    } else {
                        a.setCheckOutTime(null);
                    }
                    a.setStatus(newStatus);

                    boolean ok = attendanceController.updateAttendance(a);
                    if (ok) {
                        JOptionPane.showMessageDialog(editDlg, "Cập nhật thành công.");
                        loadFilteredData();
                    } else {
                        JOptionPane.showMessageDialog(editDlg,
                            "Cập nhật thất bại. Vui lòng thử lại.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    editDlg.dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(editDlg,
                        "Định dạng thời gian không hợp lệ (YYYY-MM-DD HH:MM:SS).",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            cancelBtn.addActionListener(ev -> editDlg.dispose());

            editDlg.add(content, BorderLayout.CENTER);
            editDlg.add(btnPanel2, BorderLayout.SOUTH);
            editDlg.setVisible(true);
        });

        // (6) DocumentListener cho searchField: tự động loadFilteredData khi gõ/xóa
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadFilteredData(); }
            public void removeUpdate(DocumentEvent e) { loadFilteredData(); }
            public void changedUpdate(DocumentEvent e) { loadFilteredData(); }
        });

        // (7) Nút Prev / Next cho phân trang
        btnPrevPage.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTable();
            }
        });
        btnNextPage.addActionListener(e -> {
            if (currentPage * rowsPerPage < allData.size()) {
                currentPage++;
                updateTable();
            }
        });

        // Load dữ liệu ban đầu
        loadFilteredData();
    }

    /**
     * loadFilteredData: Lọc theo searchField + dateChooser + shiftFilter + statusFilter + adjustFilter + leaveFilter
     * Chỉ query 1 lần để lấy allAttendance, 1 lần để lấy tất cả adjustedIds, 1 lần để lấy tất cả leaveEmpIds.
     */
    private void loadFilteredData() {
        String keyword    = searchField.getText().trim().toLowerCase();
        Date date         = dateChooser.getDate();
        String shiftId    = Objects.requireNonNull(shiftFilter.getSelectedItem()).toString();
        String status     = Objects.requireNonNull(statusFilter.getSelectedItem()).toString();
        String adjustSel  = Objects.requireNonNull(adjustFilter.getSelectedItem()).toString();
        String leaveSel   = Objects.requireNonNull(leaveFilter.getSelectedItem()).toString();

        // 1) Load toàn bộ attendance
        List<Attendance> originalList = attendanceController.getAllAttendance();
        // 2) Load tất cả attendance_id có điều chỉnh (1 query)
        Set<Integer> adjustedIds = attendanceController.getAllAdjustedAttendanceIds();
        // 3) Load tất cả employee_id có nghỉ phép (1 query)
        Set<Integer> leaveEmpIds = attendanceController.getAllEmployeesWithLeave();

        allData.clear();
        for (Attendance a : originalList) {
            // 1) Kiểm tra từ khóa trong Employee ID hoặc Note
            boolean matchesKeyword = keyword.isEmpty()
                || String.valueOf(a.getEmployeeId()).contains(keyword)
                || (a.getNote() != null && a.getNote().toLowerCase().contains(keyword));

            // 2) Kiểm tra ngày
            boolean matchesDate = (date == null)
                || (a.getAttendanceDate() != null
                    && a.getAttendanceDate().equals(new java.sql.Date(date.getTime())));

            // 3) Kiểm tra shift
            boolean matchesShift = shiftId.equals("Tất cả")
                || String.valueOf(a.getShiftId()).equals(shiftId);

            // 4) Kiểm tra status
            boolean matchesStatus = status.equals("Tất cả")
                || (a.getStatus() != null && a.getStatus().equalsIgnoreCase(status));

            // 5) Kiểm tra điều kiện “Điều chỉnh” bằng cách dùng adjustedIds
            boolean hasAdjust = adjustedIds.contains(a.getAttendanceId());
            boolean matchesAdjust = adjustSel.equals("Tất cả")
                || (adjustSel.equals("Có") && hasAdjust)
                || (adjustSel.equals("Không") && !hasAdjust);

            // 6) Kiểm tra điều kiện “Nghỉ phép” bằng cách dùng leaveEmpIds
            boolean hasLeave = leaveEmpIds.contains(a.getEmployeeId());
            boolean matchesLeave = leaveSel.equals("Tất cả")
                || (leaveSel.equals("Có") && hasLeave)
                || (leaveSel.equals("Không") && !hasLeave);

            // Nếu thỏa tất cả điều kiện, thêm vào allData
            if (matchesKeyword && matchesDate && matchesShift
                && matchesStatus && matchesAdjust && matchesLeave) {
                allData.add(a);
            }
        }

        updateTable();
    }

    /**
     * updateTable: Hiển thị “rowsPerPage” bản ghi, cùng 2 cột cuối “Điều chỉnh” và “Nghỉ phép” (dùng checked sets).
     */
    private void updateTable() {
        tableModel.setRowCount(0);

        // Lấy lại sets một lần nữa để tính cột hiển thị
        Set<Integer> adjustedIds = attendanceController.getAllAdjustedAttendanceIds();
        Set<Integer> leaveEmpIds = attendanceController.getAllEmployeesWithLeave();

        int start = (currentPage - 1) * rowsPerPage;
        int end   = Math.min(start + rowsPerPage, allData.size());

        for (int i = start; i < end; i++) {
            Attendance a = allData.get(i);

            // Cột “Điều chỉnh”
            String hasAdjust = adjustedIds.contains(a.getAttendanceId()) ? "Có" : "Không";

            // Cột “Nghỉ phép”
            String hasLeave = leaveEmpIds.contains(a.getEmployeeId()) ? "Có" : "Không";

            tableModel.addRow(new Object[]{
                a.getAttendanceId(),
                a.getEmployeeId(),
                a.getAttendanceDate(),
                a.getShiftId(),
                a.getCheckInTime(),
                a.getCheckOutTime(),
                a.getStatus(),
                a.getLateMinutes(),
                a.getEarlyLeaveMinutes(),
                a.getTotalWorkingHours(),
                a.getNote(),
                hasAdjust,
                hasLeave
            });
        }

        int totalPage = (allData.size() + rowsPerPage - 1) / rowsPerPage;
        lblPageInfo.setText("Trang " + currentPage + " / " + totalPage);
    }

    /**
     * Tạo JButton với style nền bo góc, chữ trắng, hover + pressed effect.
     */
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getModel().isPressed() ? bg.darker()
                       : getModel().isRollover() ? bg.brighter()
                       : bg);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        btn.setForeground(LIGHT_TEXT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Hiển thị dialog cho danh sách nghỉ phép (LeaveRequest).
     */
    private void showLeaveRequestDialog(List<LeaveRequest> leaveList) {
        if (leaveList == null || leaveList.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Nhân viên này chưa có đơn nghỉ phép nào.",
                "Nghỉ Phép",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        JTextArea area = new JTextArea();
        area.setEditable(false);
        StringBuilder sb = new StringBuilder();
        for (LeaveRequest lr : leaveList) {
            sb.append("Từ: ").append(lr.getStartDate())
              .append(" - Đến: ").append(lr.getEndDate())
              .append(" (Lý do: ").append(lr.getReason()).append(")\n");
        }
        area.setText(sb.toString());

        JOptionPane.showMessageDialog(
            this,
            new JScrollPane(area),
            "Nghỉ Phép",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Hiển thị dialog cho danh sách điều chỉnh (AttendanceAdjustment).
     */
    private void showAdjustmentDialog(int attendanceId) {
        List<AttendanceAdjustment> list =
            attendanceController.getAdjustmentsByAttendanceId(attendanceId);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có điều chỉnh nào cho bản ghi này.");
            return;
        }

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                  "Điều chỉnh chấm công #" + attendanceId, true);
        dlg.setLayout(new BorderLayout());
        dlg.setSize(800, 400);
        dlg.setLocationRelativeTo(this);

        String[] columns = {
            "ID", "Từ", "Đến", "Lý do", "Người yêu cầu", "Ngày",
            "Người duyệt", "Ngày duyệt", "Trạng thái"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (AttendanceAdjustment a : list) {
            model.addRow(new Object[]{
                a.getAdjustmentId(),
                a.getOldStatus(),
                a.getNewStatus(),
                a.getReason(),
                a.getRequestedBy(),
                a.getRequestDate(),
                a.getApprovedBy() == 0 ? "Chưa duyệt" : a.getApprovedBy(),
                a.getApprovalDate() == null ? "-" : a.getApprovalDate(),
                a.getStatus()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(table);
        dlg.add(scroll, BorderLayout.CENTER);

        JButton close = createStyledButton("Đóng", ACCENT_COLOR);
        close.addActionListener(e -> dlg.dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BACKGROUND_COLOR);
        bottom.add(close);
        dlg.add(bottom, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }
}
