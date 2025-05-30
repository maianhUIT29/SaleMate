package com.salesmate.component;

import com.salesmate.controller.EmployeeController;
import com.salesmate.model.Employee;
import com.salesmate.utils.ExcelExporter;
import com.salesmate.utils.ExcelImporter;
import com.salesmate.utils.ExportDialog;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class AdUserPanel extends JPanel {
    private final EmployeeController employeeController;
    private final DefaultTableModel tableModel;
    private final JTable userTable;
    private final JTextField searchField;
    private final JSpinner pageSpinner;
    private final JLabel totalPagesLabel;
    private int currentPage = 1;
    private final int pageSize = 20;
    private int totalPages = 1;
    private String currentSearch = "";

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);
    private static final Color EXPORT_COLOR = new Color(155, 89, 182);
    private static final Color IMPORT_COLOR = new Color(243, 156, 18);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color LIGHT_TEXT = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(189, 195, 199);

    public AdUserPanel() {
        this.employeeController = new EmployeeController();

        // Header
        JLabel titleLabel = new JLabel("Quản lý nhân viên");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Buttons and search
        JButton addButton    = createStyledButton("Thêm nhân viên", PRIMARY_COLOR);
        JButton editButton   = createStyledButton("Sửa", SECONDARY_COLOR);
        JButton deleteButton = createStyledButton("Xóa", ACCENT_COLOR);
        JButton refreshButton= createStyledButton("Làm mới", new Color(52, 73, 94));
        JButton exportButton = createStyledButton("Xuất Excel", EXPORT_COLOR);
        JButton importButton = createStyledButton("Nhập Excel", IMPORT_COLOR);
        JButton salaryButton = createStyledButton("Xem lương", PRIMARY_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(salaryButton);

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);

        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);

        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.setBackground(BACKGROUND_COLOR);
        northContainer.add(headerPanel, BorderLayout.NORTH);
        northContainer.add(topPanel, BorderLayout.SOUTH);

        // Table setup
        String[] columns = {"ID","Họ","Tên","Ngày sinh","Ngày vào làm",
                            "SĐT","Địa chỉ","Người liên hệ","SĐT liên hệ","Chức vụ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        userTable = new JTable(tableModel);
        userTable.setRowHeight(40);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowSorter(new TableRowSorter<>(tableModel));
        userTable.setShowGrid(true);
        userTable.setGridColor(new Color(220,220,220));
        userTable.setSelectionBackground(new Color(232,234,246));
        userTable.setSelectionForeground(TEXT_COLOR);
        styleHeader(userTable);
        styleRows(userTable);
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // Pagination
        JButton prevButton = createStyledButton("« Trước", SECONDARY_COLOR);
        JButton nextButton = createStyledButton("Sau »", SECONDARY_COLOR);
        pageSpinner = new JSpinner(new SpinnerNumberModel(1,1,1,1));
        totalPagesLabel = new JLabel(" / 1"); totalPagesLabel.setFont(new Font("Segoe UI",Font.BOLD,14)); totalPagesLabel.setForeground(TEXT_COLOR);
        JLabel pageInfoLabel = new JLabel("Trang:"); pageInfoLabel.setFont(new Font("Segoe UI",Font.BOLD,14)); pageInfoLabel.setForeground(TEXT_COLOR);
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
        paginationPanel.setBackground(BACKGROUND_COLOR);
        paginationPanel.setBorder(new EmptyBorder(15,0,0,0));
        paginationPanel.add(prevButton);
        paginationPanel.add(pageInfoLabel);
        paginationPanel.add(pageSpinner);
        paginationPanel.add(totalPagesLabel);
        paginationPanel.add(nextButton);

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15,15,15,15));
        add(northContainer,BorderLayout.NORTH);
        add(scrollPane,BorderLayout.CENTER);
        add(paginationPanel,BorderLayout.SOUTH);

        // Listeners
        addButton.addActionListener(e->showAddDialog());
        editButton.addActionListener(e->showEditDialog());
        deleteButton.addActionListener(e->deleteEmployee());
        refreshButton.addActionListener(e->loadEmployees());
        exportButton.addActionListener(e->exportToExcel());
        importButton.addActionListener(e->importFromExcel());
        prevButton.addActionListener(e->{if(currentPage>1){currentPage--;pageSpinner.setValue(currentPage);loadEmployees();}});
        nextButton.addActionListener(e->{if(currentPage<totalPages){currentPage++;pageSpinner.setValue(currentPage);loadEmployees();}});
        pageSpinner.addChangeListener(e->{currentPage=(Integer)pageSpinner.getValue();loadEmployees();});
        searchField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){filter();}
            public void removeUpdate(DocumentEvent e){filter();}
            public void changedUpdate(DocumentEvent e){filter();}
            private void filter(){currentSearch=searchField.getText();currentPage=1;pageSpinner.setValue(1);loadEmployees();}
        });
        salaryButton.addActionListener(e -> showSalaryViewDialog());

        // Initial load
        loadEmployees();
    }

    private void loadEmployees() {
        tableModel.setRowCount(0);
        List<Employee> list = employeeController.getEmployee(currentPage, pageSize, currentSearch);
        int total = employeeController.countEmployee(currentSearch);
        totalPages = Math.max((int) Math.ceil((double) total / pageSize), 1);
        currentPage = Math.min(Math.max(currentPage, 1), totalPages);
        pageSpinner.setModel(new SpinnerNumberModel(currentPage, 1, totalPages, 1));
        totalPagesLabel.setText(" / " + totalPages);

        for (Employee e : list) {
            tableModel.addRow(new Object[]{
                e.getEmployeeId(),
                e.getFirstName(),
                e.getLastName(),
                e.getBirthDate(),
                e.getHireDate(),
                e.getPhone(),
                e.getAddress(),
                e.getEmergencyContact(),
                e.getEmergencyPhone(),
                e.getRole()
            });
        }

      
    }
    

   private void showAddDialog() {
    // Tạo dialog modal
    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm nhân viên", true);
    dialog.setLayout(new BorderLayout());

    // Form panel với GridBagLayout
    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill   = GridBagConstraints.HORIZONTAL;

    // 1. Họ
    JTextField fn = new JTextField(20);
    gbc.gridx = 0; gbc.gridy = 0;
    form.add(new JLabel("Họ:"), gbc);
    gbc.gridx = 1;
    form.add(fn, gbc);

    // 2. Tên
    JTextField ln = new JTextField(20);
    gbc.gridx = 0; gbc.gridy = 1;
    form.add(new JLabel("Tên:"), gbc);
    gbc.gridx = 1;
    form.add(ln, gbc);

    // 3. Ngày sinh — dùng JDateChooser từ JCalendar
    JDateChooser bd = new JDateChooser();
    bd.setDateFormatString("yyyy-MM-dd");
    gbc.gridx = 0; gbc.gridy = 2;
    form.add(new JLabel("Ngày sinh:"), gbc);
    gbc.gridx = 1;
    form.add(bd, gbc);

    // 4. Ngày vào làm — dùng JDateChooser
    JDateChooser hd = new JDateChooser();
    hd.setDateFormatString("yyyy-MM-dd");
    gbc.gridx = 0; gbc.gridy = 3;
    form.add(new JLabel("Ngày vào làm:"), gbc);
    gbc.gridx = 1;
    form.add(hd, gbc);

    // 5. SĐT
    JTextField phone = new JTextField(20);
    gbc.gridx = 0; gbc.gridy = 4;
    form.add(new JLabel("SĐT:"), gbc);
    gbc.gridx = 1;
    form.add(phone, gbc);

    // 6. Địa chỉ
    JTextField addr = new JTextField(20);
    gbc.gridx = 0; gbc.gridy = 5;
    form.add(new JLabel("Địa chỉ:"), gbc);
    gbc.gridx = 1;
    form.add(addr, gbc);

    // 7. Người liên hệ khẩn cấp
    JTextField ec = new JTextField(20);
    gbc.gridx = 0; gbc.gridy = 6;
    form.add(new JLabel("Người liên hệ:"), gbc);
    gbc.gridx = 1;
    form.add(ec, gbc);

    // 8. SĐT liên hệ khẩn cấp
    JTextField ep = new JTextField(20);
    gbc.gridx = 0; gbc.gridy = 7;
    form.add(new JLabel("SĐT liên hệ:"), gbc);
    gbc.gridx = 1;
    form.add(ep, gbc);

    // 9. Chức vụ
    JComboBox<String> role = new JComboBox<>(new String[]{ "Warehouse", "Sales" });
    gbc.gridx = 0; gbc.gridy = 8;
    form.add(new JLabel("Chức vụ:"), gbc);
    gbc.gridx = 1;
    form.add(role, gbc);

    // Buttons “Lưu” / “Hủy”
    JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    JButton save   = new JButton("Lưu");
    JButton cancel = new JButton("Hủy");

    // Lưu dữ liệu khi nhấn “Lưu”
    save.addActionListener(e -> {
        try {
            Employee emp = new Employee();
            emp.setFirstName(fn.getText());
            emp.setLastName(ln.getText());
            emp.setBirthDate(bd.getDate());
            emp.setHireDate(hd.getDate());
            emp.setPhone(phone.getText());
            emp.setAddress(addr.getText());
            emp.setEmergencyContact(ec.getText());
            emp.setEmergencyPhone(ep.getText());
            emp.setRole((String) role.getSelectedItem());

            if (employeeController.addEmployee(emp)) {
                JOptionPane.showMessageDialog(dialog, "Thêm thành công");
                dialog.dispose();
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi thêm", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Kiểm tra dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    });

    // Hủy bỏ dialog khi nhấn “Hủy”
    cancel.addActionListener(e -> dialog.dispose());

    btnP.add(save);
    btnP.add(cancel);

    // Hoàn thiện và hiển thị dialog
    dialog.add(form, BorderLayout.CENTER);
    dialog.add(btnP, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}

private void showEditDialog() {
    // Kiểm tra chọn dòng
    int row = userTable.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên");
        return;
    }

    // Lấy ID và nạp dữ liệu từ Controller
    int id = (int) tableModel.getValueAt(row, 0);
    Employee emp = employeeController.getEmployeeById(id);
    if (emp == null) {
        JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Tạo dialog modal
    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa nhân viên", true);
    dialog.setLayout(new BorderLayout());

    // Form panel
    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill   = GridBagConstraints.HORIZONTAL;

    // 1. Họ (prefill)
    JTextField fn = new JTextField(emp.getFirstName(), 20);
    gbc.gridx = 0; gbc.gridy = 0;
    form.add(new JLabel("Họ:"), gbc);
    gbc.gridx = 1;
    form.add(fn, gbc);

    // 2. Tên (prefill)
    JTextField ln = new JTextField(emp.getLastName(), 20);
    gbc.gridx = 0; gbc.gridy = 1;
    form.add(new JLabel("Tên:"), gbc);
    gbc.gridx = 1;
    form.add(ln, gbc);

    // 3. Ngày sinh — JDateChooser (prefill)
    JDateChooser bd = new JDateChooser(emp.getBirthDate());
    bd.setDateFormatString("yyyy-MM-dd");
    gbc.gridx = 0; gbc.gridy = 2;
    form.add(new JLabel("Ngày sinh:"), gbc);
    gbc.gridx = 1;
    form.add(bd, gbc);

    // 4. Ngày vào làm — JDateChooser (prefill)
    JDateChooser hd = new JDateChooser(emp.getHireDate());
    hd.setDateFormatString("yyyy-MM-dd");
    gbc.gridx = 0; gbc.gridy = 3;
    form.add(new JLabel("Ngày vào làm:"), gbc);
    gbc.gridx = 1;
    form.add(hd, gbc);

    // 5. SĐT (prefill)
    JTextField phone = new JTextField(emp.getPhone(), 20);
    gbc.gridx = 0; gbc.gridy = 4;
    form.add(new JLabel("SĐT:"), gbc);
    gbc.gridx = 1;
    form.add(phone, gbc);

    // 6. Địa chỉ (prefill)
    JTextField addr = new JTextField(emp.getAddress(), 20);
    gbc.gridx = 0; gbc.gridy = 5;
    form.add(new JLabel("Địa chỉ:"), gbc);
    gbc.gridx = 1;
    form.add(addr, gbc);

    // 7. Người liên hệ (prefill)
    JTextField ec = new JTextField(emp.getEmergencyContact(), 20);
    gbc.gridx = 0; gbc.gridy = 6;
    form.add(new JLabel("Người liên hệ:"), gbc);
    gbc.gridx = 1;
    form.add(ec, gbc);

    // 8. SĐT liên hệ (prefill)
    JTextField ep = new JTextField(emp.getEmergencyPhone(), 20);
    gbc.gridx = 0; gbc.gridy = 7;
    form.add(new JLabel("SĐT liên hệ:"), gbc);
    gbc.gridx = 1;
    form.add(ep, gbc);

    // 9. Chức vụ (prefill)
    JComboBox<String> role = new JComboBox<>(new String[]{ "Warehouse", "Sales" });
    role.setSelectedItem(emp.getRole());
    gbc.gridx = 0; gbc.gridy = 8;
    form.add(new JLabel("Chức vụ:"), gbc);
    gbc.gridx = 1;
    form.add(role, gbc);

    // Buttons “Lưu” / “Hủy”
    JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    JButton save   = new JButton("Lưu");
    JButton cancel = new JButton("Hủy");

    save.addActionListener(e -> {
        try {
            // Cập nhật giá trị từ form
            emp.setFirstName(fn.getText());
            emp.setLastName(ln.getText());
            emp.setBirthDate(bd.getDate());
            emp.setHireDate(hd.getDate());
            emp.setPhone(phone.getText());
            emp.setAddress(addr.getText());
            emp.setEmergencyContact(ec.getText());
            emp.setEmergencyPhone(ep.getText());
            emp.setRole((String) role.getSelectedItem());

            // Gọi controller để lưu
            if (employeeController.updateEmployee(emp)) {
                JOptionPane.showMessageDialog(dialog, "Cập nhật thành công");
                dialog.dispose();
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi cập nhật", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Kiểm tra dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    });
    cancel.addActionListener(e -> dialog.dispose());

    btnP.add(save);
    btnP.add(cancel);

    // Hoàn thiện và hiển thị
    dialog.add(form, BorderLayout.CENTER);
    dialog.add(btnP, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}


    private void deleteEmployee() {
        int row = userTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Chọn nhân viên");return;}
        int id=(int)tableModel.getValueAt(row,0);
        int confirm = JOptionPane.showConfirmDialog(this,"Xác nhận xóa?","Xóa",JOptionPane.YES_NO_OPTION);
        if(confirm==JOptionPane.YES_OPTION){
            if(employeeController.deleteEmployee(id)){ JOptionPane.showMessageDialog(this,"Xóa thành công"); loadEmployees();}
            else JOptionPane.showMessageDialog(this,"Lỗi xóa","Lỗi",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToExcel() {
        ExportDialog dlg = new ExportDialog((Frame)SwingUtilities.getWindowAncestor(this),userTable);
        dlg.setVisible(true);
        if(dlg.isExportConfirmed()){
            File f = dlg.showSaveDialog(); if(f!=null){
                try{ if(dlg.isXLSX()) ExcelExporter.exportToExcel(userTable,f,dlg.includeHeaders(),dlg.getSelectedColumns());
                    else ExcelExporter.exportToCSV(userTable,f,dlg.includeHeaders(),dlg.getSelectedColumns());
                    if(dlg.openAfterExport()) ExcelExporter.openFile(f);
                }catch(IOException ex){ JOptionPane.showMessageDialog(this,"Lỗi xuất: "+ex.getMessage(),"Lỗi",JOptionPane.ERROR_MESSAGE);} }
        }
    }

    private void importFromExcel() {
        JFileChooser chooser = new JFileChooser(); chooser.setFileFilter(new FileNameExtensionFilter("Excel Files","xlsx"));
        if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){ File f=chooser.getSelectedFile();
            try{ if(!ExcelImporter.validateExcelFile(f)){ JOptionPane.showMessageDialog(this,"Excel không hợp lệ","Lỗi",JOptionPane.ERROR_MESSAGE);return;} 
                String[] hdr=ExcelImporter.getColumnHeaders(f);
                JDialog mapDlg=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Ánh xạ cột",true);
                JPanel mapP=new JPanel(new GridBagLayout()); GridBagConstraints gbc=new GridBagConstraints();gbc.insets=new Insets(5,5,5,5);gbc.fill=GridBagConstraints.HORIZONTAL;
                String[] fields={"Họ","Tên","Ngày sinh","Ngày vào làm","SĐT","Địa chỉ","Người liên hệ","SĐT liên hệ","Chức vụ"};
                JComboBox<String>[] cmb=new JComboBox[fields.length];
                for(int i=0;i<fields.length;i++){gbc.gridx=0;gbc.gridy=i;mapP.add(new JLabel(fields[i]+":"),gbc);gbc.gridx=1;cmb[i]=new JComboBox<>(hdr);mapP.add(cmb[i],gbc);}  
                JButton imp=new JButton("Nhập"),cn=new JButton("Hủy");
                imp.addActionListener(e->{mapDlg.dispose();performImport(f,cmb);}); cn.addActionListener(e->mapDlg.dispose());
                JPanel bp=new JPanel();bp.add(imp);bp.add(cn);
                mapDlg.setLayout(new BorderLayout());mapDlg.add(mapP,BorderLayout.CENTER);mapDlg.add(bp,BorderLayout.SOUTH);mapDlg.pack();mapDlg.setLocationRelativeTo(this);mapDlg.setVisible(true);
            }catch(IOException ex){ JOptionPane.showMessageDialog(this,"Lỗi import: "+ex.getMessage(),"Lỗi",JOptionPane.ERROR_MESSAGE);} }
    }

    private void performImport(File file, JComboBox<String>[] combos) {
        try{ List<Object[]> data = ExcelImporter.importFromExcel(file);int s=0,f=0;StringBuilder log=new StringBuilder();
            for(int i=0;i<data.size();i++){ Object[] row=data.get(i); try{ Employee e=new Employee();
                    e.setFirstName(row[combos[0].getSelectedIndex()].toString());
                    e.setLastName(row[combos[1].getSelectedIndex()].toString());
                    e.setBirthDate(new java.sql.Date(((java.util.Date)row[combos[2].getSelectedIndex()]).getTime()));
                    e.setHireDate(new java.sql.Date(((java.util.Date)row[combos[3].getSelectedIndex()]).getTime()));
                    e.setPhone(row[combos[4].getSelectedIndex()].toString());
                    e.setAddress(row[combos[5].getSelectedIndex()].toString());
                    e.setEmergencyContact(row[combos[6].getSelectedIndex()].toString());
                    e.setEmergencyPhone(row[combos[7].getSelectedIndex()].toString());
                    e.setRole(row[combos[8].getSelectedIndex()].toString());
                    if(employeeController.addEmployee(e)) s++; else {f++; log.append("Dòng "+(i+2)+": Lỗi lưu\n");}
                }catch(Exception ex){f++; log.append("Dòng "+(i+2)+": "+ex.getMessage()+"\n");}} 
            String msg="Nhập thành công "+s+" nhân viên."+ (f>0?"\nKhông thể nhập "+f+"\n"+log.toString():"");
            JOptionPane.showMessageDialog(this,msg);
            loadEmployees();
        }catch(IOException ex){ JOptionPane.showMessageDialog(this,"Lỗi import: "+ex.getMessage(),"Lỗi",JOptionPane.ERROR_MESSAGE);} }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn=new JButton(text){@Override protected void paintComponent(Graphics g){ if(getModel().isPressed())g.setColor(bg.darker());else if(getModel().isRollover())g.setColor(bg.brighter());else g.setColor(bg);g.fillRect(0,0,getWidth(),getHeight());super.paintComponent(g);} };
        btn.setForeground(LIGHT_TEXT);btn.setFont(new Font("Segoe UI",Font.BOLD,13));btn.setFocusPainted(false);btn.setBorderPainted(false);btn.setOpaque(false);btn.setBorder(BorderFactory.createEmptyBorder(8,15,8,15));btn.setCursor(new Cursor(Cursor.HAND_CURSOR));return btn;
    }

    private void styleHeader(JTable table){ JTableHeader h = table.getTableHeader(); h.setPreferredSize(new Dimension(h.getWidth(),45)); h.setDefaultRenderer(new DefaultTableCellRenderer(){@Override public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){ JLabel lbl=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c); lbl.setBackground(new Color(25,79,115)); lbl.setForeground(Color.WHITE); lbl.setFont(new Font("Segoe UI",Font.BOLD,15)); lbl.setHorizontalAlignment(JLabel.CENTER); lbl.setOpaque(true); lbl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,2,1,BORDER_COLOR),BorderFactory.createEmptyBorder(5,5,5,5))); return lbl;}});
    }

    private void styleRows(JTable table){ table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){@Override public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){ super.getTableCellRendererComponent(t,v,s,f,r,c); if(!s) setBackground(r%2==0?Color.WHITE:new Color(245,247,250)); setHorizontalAlignment((c==0||c==3||c==4)?JLabel.CENTER:JLabel.LEFT); setBorder(BorderFactory.createEmptyBorder(0,8,0,8)); return this;}});
    }

    private void showSalaryViewDialog() {
        // Kiểm tra chọn dòng
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để xem lương");
            return;
        }

        // Lấy ID nhân viên
        int id = (int) tableModel.getValueAt(row, 0);
        Employee emp = employeeController.getEmployeeById(id);
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo và hiển thị SalaryViewDialog
        JDialog salaryDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Xem lương nhân viên", true);
        salaryDialog.setLayout(new BorderLayout());
        salaryDialog.setSize(400, 300);
        salaryDialog.setLocationRelativeTo(this);

        JLabel salaryLabel = new JLabel("Lương của " + emp.getFirstName() + " " + emp.getLastName() + ": " + emp.getSalary() + " VND");
        salaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        salaryLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> salaryDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);

        salaryDialog.add(salaryLabel, BorderLayout.CENTER);
        salaryDialog.add(buttonPanel, BorderLayout.SOUTH);
        salaryDialog.setVisible(true);
    }
}
