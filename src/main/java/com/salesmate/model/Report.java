package com.salesmate.model;

import java.util.Date;
import jakarta.persistence.*;

/*
Report Schema in oracle
    report_id INT PRIMARY KEY,
    report_type VARCHAR2(50) CHECK (report_type IN ('Sales', 'Stock')),
    report_date DATE,
    content VARCHAR2(100),
    users_id INT NOT NULL,
    created_at DATE DEFAULT SYSDATE,
    FOREIGN KEY (users_id) REFERENCES USERS(users_id)
);
*/

@Entity
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private int reportId;

    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType;

    @Temporal(TemporalType.DATE)
    @Column(name = "report_date")
    private Date reportDate;

    @Column(name = "content", length = 100)
    private String content;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date createdAt;

    // Getters and Setters
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
