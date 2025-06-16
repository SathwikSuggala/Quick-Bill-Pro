package models;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class OutletCredit {
    private int creditId;
    private int outletId;
    private String outletName;
    private int billId;
    private double creditAmount;
    private Date creditDate;
    private Date dueDate;
    private String status;
    private double paidAmount;
    private double remainingAmount;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public OutletCredit(int creditId, int outletId, String outletName, int billId, 
                       double creditAmount, Date creditDate, Date dueDate, 
                       String status, double paidAmount) {
        this.creditId = creditId;
        this.outletId = outletId;
        this.outletName = outletName;
        this.billId = billId;
        this.creditAmount = creditAmount;
        this.creditDate = creditDate;
        this.dueDate = dueDate;
        this.status = status;
        this.paidAmount = paidAmount;
        this.remainingAmount = creditAmount - paidAmount;
    }
    
    // Getters
    public int getCreditId() { return creditId; }
    public int getOutletId() { return outletId; }
    public String getOutletName() { return outletName; }
    public int getBillId() { return billId; }
    public double getCreditAmount() { return creditAmount; }
    public String getCreditDate() { return creditDate != null ? dateFormat.format(creditDate) : ""; }
    public String getDueDate() { return dueDate != null ? dateFormat.format(dueDate) : ""; }
    public String getStatus() { return status; }
    public double getPaidAmount() { return paidAmount; }
    public double getRemainingAmount() { return remainingAmount; }
    
    // Setters
    public void setCreditId(int creditId) { this.creditId = creditId; }
    public void setOutletId(int outletId) { this.outletId = outletId; }
    public void setOutletName(String outletName) { this.outletName = outletName; }
    public void setBillId(int billId) { this.billId = billId; }
    public void setCreditAmount(double creditAmount) { 
        this.creditAmount = creditAmount;
        this.remainingAmount = creditAmount - paidAmount;
    }
    public void setCreditDate(Date creditDate) { this.creditDate = creditDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public void setStatus(String status) { this.status = status; }
    public void setPaidAmount(double paidAmount) { 
        this.paidAmount = paidAmount;
        this.remainingAmount = creditAmount - paidAmount;
    }
} 