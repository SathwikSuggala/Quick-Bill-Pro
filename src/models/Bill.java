package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Date;

public class Bill {
    private int billId;
    private int outletId;
    private double totalCGST;
    private double totalSGST;
    private double totalAmount;
    private Date billDate;
    private String paymentType;
    private String remarks;
    private ObservableList<BillItem> billItems;
    
    public Bill(int billId, int outletId, double totalCGST, double totalSGST, double totalAmount, 
                Date billDate, String paymentType, String remarks) {
        this.billId = billId;
        this.outletId = outletId;
        this.totalCGST = totalCGST;
        this.totalSGST = totalSGST;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
        this.paymentType = paymentType;
        this.remarks = remarks;
        this.billItems = FXCollections.observableArrayList();
    }
    
    // Getters
    public int getBillId() { return billId; }
    public int getOutletId() { return outletId; }
    public double getTotalCGST() { return totalCGST; }
    public double getTotalSGST() { return totalSGST; }
    public double getTotalAmount() { return totalAmount; }
    public Date getBillDate() { return billDate; }
    public String getPaymentType() { return paymentType; }
    public String getRemarks() { return remarks; }
    public ObservableList<BillItem> getBillItems() { return billItems; }
    
    // Setters
    public void setBillId(int billId) { this.billId = billId; }
    public void setOutletId(int outletId) { this.outletId = outletId; }
    public void setTotalCGST(double totalCGST) { this.totalCGST = totalCGST; }
    public void setTotalSGST(double totalSGST) { this.totalSGST = totalSGST; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setBillDate(Date billDate) { this.billDate = billDate; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public void setBillItems(ObservableList<BillItem> billItems) { this.billItems = billItems; }
} 