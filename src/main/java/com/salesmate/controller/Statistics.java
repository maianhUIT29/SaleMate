package com.salesmate.controller;
public class Statistics {
    private int employeeCount;
    private int productCount;
    private int invoiceCount;

    public Statistics(int employeeCount, int productCount, int invoiceCount) {
        this.employeeCount = employeeCount;
        this.productCount = productCount;
        this.invoiceCount = invoiceCount;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public int getProductCount() {
        return productCount;
    }

    public int getInvoiceCount() {
        return invoiceCount;
    }
}