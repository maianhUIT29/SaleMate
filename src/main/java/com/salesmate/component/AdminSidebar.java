package com.salesmate.component;

import com.salesmate.view.AdminView;
import com.salesmate.utils.ColorPalette;

import javax.swing.*;
import java.awt.*;

public class AdminSidebar extends JPanel {

    private AdminView parentView;

    private JToggleButton tbtnDashBoard;
    private JToggleButton tbtnUser;
    private JToggleButton tbtnProduct;
    private JToggleButton tbtnInvoice;
    private JToggleButton tbtnRevenue;
    private JToggleButton tbtnAttendance;
    private JToggleButton tbtnSalary;

    public AdminSidebar() {
        initSidebar();
    }

    public void setParentView(AdminView parentView) {
        this.parentView = parentView;
    }

    private void initSidebar() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorPalette.WARNING);

        add(Box.createVerticalStrut(20));

        tbtnDashBoard = createSidebarButton("DASHBOARD", "/img/icons/ic_dashboard.png");
        tbtnDashBoard.addActionListener(evt -> {
            if (parentView != null) parentView.switchCard("cardDashBoard");
        });
        add(tbtnDashBoard);
        add(Box.createVerticalStrut(10));

        tbtnUser = createSidebarButton("NHÂN VIÊN", "/img/icons/ic_user.png");
        tbtnUser.addActionListener(evt -> {
            if (parentView != null) parentView.switchCard("cardUserPanel");
        });
        add(tbtnUser);
        add(Box.createVerticalStrut(10));

        tbtnProduct = createSidebarButton("SẢN PHẨM", "/img/icons/ic_product.png");
        tbtnProduct.addActionListener(evt -> {
            if (parentView != null) parentView.switchCard("cardProductPanel");
        });
        add(tbtnProduct);
        add(Box.createVerticalStrut(10));

        tbtnInvoice = createSidebarButton("HÓA ĐƠN", "/img/icons/ic_invoice.png");
        tbtnInvoice.addActionListener(evt -> {
            if (parentView != null) parentView.switchCard("cardInvoicePanel");
        });
        add(tbtnInvoice);
        add(Box.createVerticalStrut(10));

        tbtnRevenue = createSidebarButton("NGƯỜI DÙNG", "/img/icons/ic_qlyuser.png");
        tbtnRevenue.addActionListener(evt -> {
            if (parentView != null) parentView.switchCard("cardRevenuePanel");
        });
        add(tbtnRevenue);
        add(Box.createVerticalStrut(10));

        tbtnAttendance = createSidebarButton("CHẤM CÔNG", "/img/icons/ic_attendance.png");
        tbtnAttendance.addActionListener(evt -> {
            if (parentView != null) parentView.switchCard("cardAttendancePanel"); 
        });
        add(tbtnAttendance);
        add(Box.createVerticalStrut(10));

        tbtnSalary = createSidebarButton("BẢNG LƯƠNG", "/img/icons/ic_salary.png");
        tbtnSalary.addActionListener(evt -> {
            if (parentView != null) parentView.switchCard("cardSalaryPanel");
        });
        add(tbtnSalary);

        add(Box.createVerticalGlue());

        ButtonGroup group = new ButtonGroup();
        group.add(tbtnDashBoard);
        group.add(tbtnUser);
        group.add(tbtnProduct);
        group.add(tbtnInvoice);
        group.add(tbtnRevenue);
        group.add(tbtnAttendance);
        group.add(tbtnSalary);
    }

    private JToggleButton createSidebarButton(String text, String iconPath) {
        JToggleButton btn = new JToggleButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Scale icon xuống 32x32
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(iconPath));
        Image scaledImg = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        btn.setIcon(new ImageIcon(scaledImg));

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setPreferredSize(new Dimension(180, 50));
        btn.setMinimumSize(new Dimension(100, 50));
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(
            0, 0, ColorPalette.WARNING,
            0, getHeight(), new Color(255, 204, 0)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
