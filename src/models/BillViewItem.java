package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class BillViewItem {
    private final IntegerProperty billId;
    private final StringProperty billDate;
    private final StringProperty outletName;
    private final StringProperty outletAddress;
    private final DoubleProperty totalAmount;
    private final DoubleProperty totalCGST;
    private final DoubleProperty totalSGST;

    public BillViewItem(int billId, LocalDate billDate, String outletName, String outletAddress,
                       double totalAmount, double totalCGST, double totalSGST) {
        this.billId = new SimpleIntegerProperty(billId);
        this.billDate = new SimpleStringProperty(billDate != null ? billDate.toString() : "");
        this.outletName = new SimpleStringProperty(outletName);
        this.outletAddress = new SimpleStringProperty(outletAddress);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.totalCGST = new SimpleDoubleProperty(totalCGST);
        this.totalSGST = new SimpleDoubleProperty(totalSGST);
    }

    // Property Getters
    public IntegerProperty billIdProperty() { return billId; }
    public StringProperty billDateProperty() { return billDate; }
    public StringProperty outletNameProperty() { return outletName; }
    public StringProperty outletAddressProperty() { return outletAddress; }
    public DoubleProperty totalAmountProperty() { return totalAmount; }
    public DoubleProperty totalCGSTProperty() { return totalCGST; }
    public DoubleProperty totalSGSTProperty() { return totalSGST; }

    // Regular Getters
    public int getBillId() { return billId.get(); }
    public String getBillDate() { return billDate.get(); }
    public String getOutletName() { return outletName.get(); }
    public String getOutletAddress() { return outletAddress.get(); }
    public double getTotalAmount() { return totalAmount.get(); }
    public double getTotalCGST() { return totalCGST.get(); }
    public double getTotalSGST() { return totalSGST.get(); }
} 