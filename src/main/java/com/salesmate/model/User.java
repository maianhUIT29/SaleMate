package com.salesmate.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/*
User Schema in oracle
    users_id INT PRIMARY KEY,
    username VARCHAR2(50) NOT NULL,
    role VARCHAR2(20) CHECK (role IN ('Manager', 'Warehouse', 'Sales')) NOT NULL,
    created_at DATE DEFAULT SYSDATE,
    avatar VARCHAR2(255),
    email VARCHAR2(255),
    password VARCHAR2(255),
    status VARCHAR2(20),
    employee_id INT,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    CONSTRAINT users_role_check CHECK (role IN ('Manager', 'Warehouse', 'Sales')),
);
*/

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private int usersId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date createdAt;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "employee_id")
    private int employeeId;

    // Constructors
    public User() {
    }

    public User(int usersId, String username, String role, Date createdAt, String avatar, String email, String status) {
        this.usersId = usersId;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
        this.avatar = avatar;
        this.email = email;
        this.status = status;
    }

    // Constructor with all fields
    public User(int usersId, String username, String role, Date createdAt, String avatar, String email, String status, String password, int employeeId) {
        this.usersId = usersId;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
        this.avatar = avatar;
        this.email = email;
        this.status = status;
        this.password = password;
        this.employeeId = employeeId;
    }

    // Getters and Setters
    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }
}