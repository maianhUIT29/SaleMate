package com.salesmate.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "salary_detail")
public class SalaryDetail {
    @Id
    @Column(name = "salary_detail_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int salaryDetailId;

    @Column(name = "salary_id", nullable = false)
    private int salaryId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "calculation_base", precision = 10, scale = 2)
    private BigDecimal calculationBase;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "component_name", nullable = false, length = 50)
    private String componentName;

    @Column(name = "component_type", length = 20)
    private String componentType;

    @Column(name = "calculation_type", length = 20)
    private String calculationType;

    @Column(name = "value", precision = 10, scale = 2)
    private BigDecimal value;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_taxable")
    private int isTaxable = 1;

    // Getters and setters
    public int getSalaryDetailId() {
        return salaryDetailId;
    }

    public void setSalaryDetailId(int salaryDetailId) {
        this.salaryDetailId = salaryDetailId;
    }

    public int getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(int salaryId) {
        this.salaryId = salaryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCalculationBase() {
        return calculationBase;
    }

    public void setCalculationBase(BigDecimal calculationBase) {
        this.calculationBase = calculationBase;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(String calculationType) {
        this.calculationType = calculationType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsTaxable() {
        return isTaxable;
    }

    public void setIsTaxable(int isTaxable) {
        this.isTaxable = isTaxable;
    }
}
