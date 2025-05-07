package com.salesmate.model;

import java.math.BigDecimal;
import java.util.Date;

public class ChartDataModel {
    private String label;
    private Date date;
    private BigDecimal value;
    private String category;

    public ChartDataModel(String label, BigDecimal value) {
        this.label = label;
        this.value = value;
    }

    public ChartDataModel(Date date, BigDecimal value) {
        this.date = date;
        this.value = value;
    }

    public ChartDataModel(String label, BigDecimal value, String category) {
        this.label = label;
        this.value = value;
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
} 