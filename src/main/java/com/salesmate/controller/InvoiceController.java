package com.salesmate.controller;

import com.salesmate.dao.InvoiceDAO;
import com.salesmate.model.Invoice;

public class InvoiceController {
    private final InvoiceDAO invoiceDAO;

    public InvoiceController() {
        this.invoiceDAO = new InvoiceDAO();
    }

    public void saveInvoice(Invoice invoice) {
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
    
}
