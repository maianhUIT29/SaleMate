package com.salesmate.model;

import java.awt.Color;
public class RevenueLineChartModel{

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public  RevenueLineChartModel(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public  RevenueLineChartModel() {
    }
    private String name;
    private double value;
}