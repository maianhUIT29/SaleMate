package com.salesmate.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.salesmate.dao.DetailDAO;
import com.salesmate.dao.InvoiceDAO;
import com.salesmate.dao.PaymentDAO;
import com.salesmate.dao.UserDAO;
import com.salesmate.model.ChartDataModel;
import com.salesmate.model.Invoice;

public class InvoiceController {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final UserDAO userDAO = new UserDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final DetailDAO detailDAO   = new DetailDAO();  // ← thêm dòng này

    // Change return type to void since we update the invoice object directly
    public void saveInvoice(Invoice invoice) {
        if (userDAO.getUserById(invoice.getUsersId()) == null) {
            throw new IllegalArgumentException("Invalid users_id: User does not exist");
        }
        if (invoice.getTotal() == null || invoice.getTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount must be non-negative");
        }
        invoiceDAO.saveInvoice(invoice);
    }

    public boolean addInvoice(Invoice invoice) {
        try {
            return invoiceDAO.createInvoice(invoice);
        } catch (Exception e) {
            logError(e);
            return false;
        }
    }

    public Invoice getInvoiceById(int id) {
        return handleException(() -> invoiceDAO.getInvoiceById(id));
    }

    public List<Invoice> getAllInvoices() {
        return handleException(invoiceDAO::getAllInvoices);
    }

    public boolean updateInvoice(Invoice invoice) {
        return handleException(() -> invoiceDAO.updateInvoice(invoice));
    }

 public boolean deleteInvoice(int id) {
    return handleException(() -> {
        // 1) Xoá tất cả detail trước
        detailDAO.deleteByInvoiceId(id);
        // 2) Xoá chính hóa đơn
        return invoiceDAO.deleteInvoice(id);
    }, false);
}


    public List<Invoice> getInvoicesByUserId(int userId) {
        return handleException(() -> invoiceDAO.getInvoicesByUserId(userId));
    }

    public List<Invoice> getInvoicesLast7Days() {
        return handleException(invoiceDAO::getInvoicesLast7Days);
    }

    public int countInvoices() {
        return handleException(invoiceDAO::countInvoices, 0);
    }

    public BigDecimal getCurrentMonthRevenue() {
        return handleException(invoiceDAO::getRevenueForCurrentMonth, BigDecimal.ZERO);
    }

    public List<ChartDataModel> getDailyRevenue() {
        return handleException(invoiceDAO::getDailyRevenue);
    }

    public List<Invoice> getInvoicesLastNDays(int days) {
        return handleException(() -> invoiceDAO.getInvoicesLastNDays(days));
    }

    public BigDecimal getTodayRevenue() {
        return handleException(invoiceDAO::getTodayRevenue, BigDecimal.ZERO);
    }

    public List<ChartDataModel> getYearlyRevenue() {
        return handleException(invoiceDAO::getYearlyRevenue);
    }

    public List<ChartDataModel> getMonthlyRevenueByYear(int year) {
        return handleException(() -> invoiceDAO.getMonthlyRevenueByYear(year));
    }

    public List<ChartDataModel> getWeeklyRevenueForCurrentMonth() {
        return handleException(invoiceDAO::getWeeklyRevenueForCurrentMonth);
    }

    public List<Integer> getAvailableYears() {
        return handleException(invoiceDAO::getAvailableYears);
    }

    public List<Map<String, Object>> getTopCustomersByRevenue(int topN) {
        return handleException(() -> invoiceDAO.getTopCustomersByRevenue(topN));
    }

    public List<Map<String, Object>> getTopInvoices(int topN) {
        return handleException(() -> invoiceDAO.getTopInvoices(topN));
    }

    public List<ChartDataModel> getInvoiceStatusRatio() {
        return handleException(invoiceDAO::getInvoiceStatusRatio);
    }

    public List<ChartDataModel> getInvoiceCountByPaymentMethod() {
        return handleException(paymentDAO::getInvoiceCountByPaymentMethod);
    }

    public List<ChartDataModel> getInvoiceCountByPaymentMethodForYear(int year) {
        return handleException(() -> paymentDAO.getInvoiceCountByPaymentMethodForYear(year));
    }

    public List<ChartDataModel> getInvoiceCountByPaymentMethodForMonth(int year, int month) {
        return handleException(() -> paymentDAO.getInvoiceCountByPaymentMethodForMonth(year, month));
    }

    public List<ChartDataModel> getInvoiceCountByPaymentMethodForWeek(int year, int month) {
        return handleException(() -> paymentDAO.getInvoiceCountByPaymentMethodForWeek(year, month));
    }

    public List<Map<String, Object>> getTopEmployeesByRevenue(int topN) {
        return handleException(() -> invoiceDAO.getTopEmployeesByRevenue(topN));
    }

    public BigDecimal getTotalRevenue() {
        return handleException(invoiceDAO::getTotalRevenue, BigDecimal.ZERO);
    }

    public List<ChartDataModel> getWeeklyRevenueByYear(int year) {
        return handleException(() -> invoiceDAO.getWeeklyRevenueByYear(year));
    }

    private void logError(Exception e) {
        e.printStackTrace(); // Replace with proper logging in production
    }

    private <T> T handleException(SupplierWithException<T> supplier) {
        return handleException(supplier, null);
    }

    private <T> T handleException(SupplierWithException<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            logError(e);
            return defaultValue;
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }
    
}
