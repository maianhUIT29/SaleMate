package com.salesmate.controller;

import java.util.List;

import com.salesmate.dao.DetailDAO;
import com.salesmate.model.Detail;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DetailController {
    private final DetailDAO detailDAO;

    public DetailController() {
        this.detailDAO = new DetailDAO();
    }

    public boolean addDetail(Detail detail) {
        return detailDAO.addDetail(detail);
    }

    public List<Detail> getAllDetails() {
        return detailDAO.getAllDetails();
    }

    public Detail getDetailById(int detailId) {
        return detailDAO.getDetailById(detailId);
    }

    public boolean updateDetail(Detail detail) {
        return detailDAO.updateDetail(detail);
    }

    public boolean deleteDetail(int detailId) {
        return detailDAO.deleteDetail(detailId);
    }

 

    public List<Detail> getDetailsByInvoiceId(int invoiceId) {
        return detailDAO.getDetailsByInvoiceId(invoiceId);
    }
    
}
