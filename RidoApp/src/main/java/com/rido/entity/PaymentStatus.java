package com.rido.entity;

//package com.sample.razor.pay.model;

public class PaymentStatus {
    
    private String paymentId;
    private boolean paid;
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public boolean isPaid() {
        return paid;
    }
    
    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}

