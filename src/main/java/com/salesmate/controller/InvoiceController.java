package com.salesmate.controller;

import java.math.BigDecimal;

import com.salesmate.dao.InvoiceDAO;
import com.salesmate.dao.UserDAO;
import com.salesmate.model.Invoice;
import java.util.List;

public class InvoiceController {
    private final InvoiceDAO invoiceDAO;
    private final UserDAO userDAO;

    public InvoiceController() {
        this.invoiceDAO = new InvoiceDAO();
        this.userDAO = new UserDAO();
    }

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

    public Invoice getInvoiceById(int id) {
        return invoiceDAO.getInvoiceById(id);
    }

    public void deleteInvoice(int id) {
        invoiceDAO.deleteInvoice(id);
    }

    // Tạo hoá đơn mới
    public void createInvoice(Invoice invoice) {
        invoiceDAO.saveInvoice(invoice);
    }

    // Lấy tất cả hoá đơn của một người dùng
    public List<Invoice> getInvoicesByUserId(int userId) {
        return invoiceDAO.getInvoicesByUserId(userId);
    }
    
}
