package com.salesmate.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.salesmate.dao.InvoiceDAO;
import com.salesmate.dao.UserDAO;
import com.salesmate.model.Invoice;
import com.salesmate.model.RevenueLineChartModel;

public class InvoiceController {

    private InvoiceDAO invoiceDAO;
    private final UserDAO userDAO;

    public InvoiceController() {
        this.invoiceDAO = new InvoiceDAO();
        this.userDAO = new UserDAO();
    }

    // Tạo hóa đơn, trả về ID của hóa đơn vừa tạo
    public void saveInvoice(Invoice invoice) {
        // Validate that the user exists before saving
        if (userDAO.getUserById(invoice.getUsersId()) == null) {
            throw new IllegalArgumentException("Invalid users_id: User does not exist");
        }

        // Validate other required fields
        if (invoice.getTotal() == null || invoice.getTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount must be non-negative");
        }

        invoiceDAO.saveInvoice(invoice);
    }

    public boolean addInvoice(Invoice invoice) {
        try {
            // Changed saveInvoice to createInvoice
            return invoiceDAO.createInvoice(invoice);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy hóa đơn theo ID
    public Invoice getInvoiceById(int id) {
        try {
            return invoiceDAO.getInvoiceById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy tất cả hóa đơn
    public List<Invoice> getAllInvoices() {
        try {
            return invoiceDAO.getAllInvoices();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Cập nhật hóa đơn
    public boolean updateInvoice(Invoice invoice) {
        try {
            return invoiceDAO.updateInvoice(invoice);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa hóa đơn theo ID
    public boolean deleteInvoice(int id) {
        try {
            return invoiceDAO.deleteInvoice(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả hóa đơn của một người dùng
    public List<Invoice> getInvoicesByUserId(int userId) {
        try {
            return invoiceDAO.getInvoicesByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy tất cả hóa đơn trong 7 ngày gần đây
    public List<Invoice> getInvoicesLast7Days() {
        try {
            return invoiceDAO.getInvoicesLast7Days();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Đếm số lượng hóa đơn
    public int countInvoices() {
        try {
            return invoiceDAO.countInvoices();
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Return 0 if there's an error
        }
    }

    // Tính doanh thu tháng hiện tại

    /**
     * Trả về tổng doanh thu tháng hiện tại.
     */
    public BigDecimal getCurrentMonthRevenue() {
        return invoiceDAO.getRevenueForCurrentMonth();
    }

    /**
     * Trả về danh sách doanh thu theo ngày trong tháng hiện tại
     * để vẽ biểu đồ line chart.
     */
    public List<RevenueLineChartModel> getDailyRevenue() {
        return invoiceDAO.getDailyRevenue();
    }

    /* Gets invoices from the last N days */
    public List<Invoice> getInvoicesLastNDays(int days) {
        try {
            return invoiceDAO.getInvoicesLastNDays(days);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}
