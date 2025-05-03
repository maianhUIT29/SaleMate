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
Employee Schema in oracle
    employee_id INT PRIMARY KEY,                
    first_name VARCHAR2(50) NOT NULL,           
    last_name VARCHAR2(50) NOT NULL,            
    birth_date DATE,                            
    hire_date DATE DEFAULT SYSDATE,             
    phone VARCHAR2(15),                         
    address VARCHAR2(255),                      
    emergency_contact VARCHAR2(100),            
    emergency_phone VARCHAR2(15),               
    role VARCHAR2(20) CHECK (role IN ('Warehouse', 'Sales'))
*/

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private int employeeId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    private Date birthDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "hire_date", columnDefinition = "DATE DEFAULT SYSDATE")
    private Date hireDate;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 15)
    private String emergencyPhone;

    @Column(name = "role", length = 20)
    private String role;

    // Constructors
    public Employee() {
    }

    public Employee(int employeeId, String firstName, String lastName, Date birthDate, Date hireDate, String phone,
            String address, String emergencyContact, String emergencyPhone, String role) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.phone = phone;
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.emergencyPhone = emergencyPhone;
        this.role = role;
    }

    // Getters and Setters
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
