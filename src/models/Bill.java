package models;

import java.util.Date;

public class Bill {
    private int billId;
    private int outletId;
    private double totalAmount;
    private Date billDate;
    private String paymentType;
    private String remarks;
    
    public Bill(int billId, int outletId, double totalAmount, Date billDate, String paymentType, String remarks) {
        this.billId = billId;
        this.outletId = outletId;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
        this.paymentType = paymentType;
        this.remarks = remarks;
    }
    
    // Getters
    public int getBillId() { return billId; }
    public int getOutletId() { return outletId; }
    public double getTotalAmount() { return totalAmount; }
    public Date getBillDate() { return billDate; }
    public String getPaymentType() { return paymentType; }
    public String getRemarks() { return remarks; }
    
    // Setters
    public void setBillId(int billId) { this.billId = billId; }
    public void setOutletId(int outletId) { this.outletId = outletId; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setBillDate(Date billDate) { this.billDate = billDate; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
} 