package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class ReportItem {
    private final StringProperty saleDate;
    private final IntegerProperty billId;
    private final StringProperty outletName;
    private final StringProperty productName;
    private final IntegerProperty quantity;
    private final DoubleProperty unitPrice;
    private final DoubleProperty cgst;
    private final DoubleProperty sgst;
    private final DoubleProperty totalAmount;

    public ReportItem(LocalDate saleDate, int billId, String outletName, String productName,
                      int quantity, double unitPrice, double cgst, double sgst, double totalAmount) {
        this.saleDate = new SimpleStringProperty(saleDate != null ? saleDate.toString() : "");
        this.billId = new SimpleIntegerProperty(billId);
        this.outletName = new SimpleStringProperty(outletName);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.unitPrice = new SimpleDoubleProperty(unitPrice);
        this.cgst = new SimpleDoubleProperty(cgst);
        this.sgst = new SimpleDoubleProperty(sgst);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
    }

    // Property Getters (for TableView)
    public StringProperty saleDateProperty() { return saleDate; }
    public IntegerProperty billIdProperty() { return billId; }
    public StringProperty outletNameProperty() { return outletName; }
    public StringProperty productNameProperty() { return productName; }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty unitPriceProperty() { return unitPrice; }
    public DoubleProperty cgstProperty() { return cgst; }
    public DoubleProperty sgstProperty() { return sgst; }
    public DoubleProperty totalAmountProperty() { return totalAmount; }

    // Regular Getters (if needed elsewhere)
    public String getSaleDate() { return saleDate.get(); }
    public int getBillId() { return billId.get(); }
    public String getOutletName() { return outletName.get(); }
    public String getProductName() { return productName.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getUnitPrice() { return unitPrice.get(); }
    public double getCgst() { return cgst.get(); }
    public double getSgst() { return sgst.get(); }
    public double getTotalAmount() { return totalAmount.get(); }
    public double getPrice() {
        return unitPrice.get();
    }
    public double getTaxableAmount() {
        return unitPrice.get()*quantity.get();
    }
    public double getCgstAmount(){
        return getTaxableAmount()*(getCgst()/100);
    }
    public double getSgstAmount(){
        return getTaxableAmount()*(getSgst()/100);
    }
    public double getTotal(){
        return getTotalAmount();
    }

} 