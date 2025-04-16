package com.salesmate.controller;

import java.util.List;

import com.salesmate.dao.InvoiceDAO;
import com.salesmate.model.Invoice;

public class InvoiceController {
    private InvoiceDAO invoiceDAO;

    public InvoiceController() {
        // Khởi tạo InvoiceDAO
        this.invoiceDAO = new InvoiceDAO();
    }

    // Tạo hóa đơn, trả về ID của hóa đơn vừa tạo
    public int saveInvoice(Invoice invoice) {
        try {
            return invoiceDAO.saveInvoice(invoice);
        } catch (Exception e) {
            e.printStackTrace();
            // Có thể log hoặc throw lại ngoại lệ tùy mục đích
            // throw new RuntimeException("Failed to save invoice", e);
            return -1; 
        }
    }

    public boolean addInvoice(Invoice invoice) {
        try {
            return invoiceDAO.saveInvoice(invoice) > 0;
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
}
