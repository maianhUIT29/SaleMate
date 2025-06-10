package com.salesmate.utils;

public class PaymentResult {
    private boolean success;
    private String message;
    private String orderId;
    private String transactionId;
    private long amount;
    private String responseCode;
    private String transactionStatus;
    private String payDate;
    private String bankCode;
    private String cardType;
    
    // Constructor
    public PaymentResult() {
        this.success = false;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public long getAmount() {
        return amount;
    }
    
    public void setAmount(long amount) {
        this.amount = amount;
    }
    
    public String getResponseCode() {
        return responseCode;
    }
    
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
    
    public String getTransactionStatus() {
        return transactionStatus;
    }
    
    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
    
    public String getPayDate() {
        return payDate;
    }
    
    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }
    
    public String getBankCode() {
        return bankCode;
    }
    
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
    
    public String getCardType() {
        return cardType;
    }
    
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
    
    @Override
    public String toString() {
        return "PaymentResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", orderId='" + orderId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", responseCode='" + responseCode + '\'' +
                ", transactionStatus='" + transactionStatus + '\'' +
                ", payDate='" + payDate + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", cardType='" + cardType + '\'' +
                '}';
    }
}
