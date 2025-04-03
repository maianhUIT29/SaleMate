package com.salesmate.controller;

import com.salesmate.dao.InvoiceDAO;
import com.salesmate.model.Invoice;
import java.util.List;

public class InvoiceController {
    private InvoiceDAO invoiceDAO;
    
    public InvoiceController() {
        invoiceDAO = new InvoiceDAO();
    }
    
    public boolean addInvoice(Invoice invoice) {
        try {
            return invoiceDAO.createInvoice(invoice);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateInvoice(Invoice invoice) {
        try {
            return invoiceDAO.updateInvoice(invoice);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteInvoice(int invoiceId) {
        try {
            return invoiceDAO.deleteInvoice(invoiceId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Invoice getInvoiceById(int invoiceId) {
        try {
            return invoiceDAO.getInvoiceById(invoiceId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Invoice> getAllInvoices() {
        try {
            return invoiceDAO.getAllInvoices();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 