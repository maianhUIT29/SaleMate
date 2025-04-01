package com.salesmate.controller;

import com.salesmate.dao.SaleDAO;
import com.salesmate.model.Sale;
import java.sql.*;
import java.util.List;

public class SaleController {
    private SaleDAO saleDAO;

    public SaleController(Connection connection) {
        this.saleDAO = new SaleDAO(connection);
    }

    public List<Sale> getTopSellingProducts(Date startDate, Date endDate) {
        try {
            return saleDAO.getTopSellingProducts(startDate, endDate);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
