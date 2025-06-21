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
    private final DoubleProperty total;
    private final DoubleProperty hsn;
    private final DoubleProperty taxableAmount;
    private final DoubleProperty cgstAmount;
    private final DoubleProperty sgstAmount;
    private final DoubleProperty totalTax;

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
        this.total = new SimpleDoubleProperty(totalAmount);

        double taxableValue = quantity * unitPrice;
        this.taxableAmount = new SimpleDoubleProperty(taxableValue);
        this.cgstAmount = new SimpleDoubleProperty(taxableValue * (cgst / 100.0));
        this.sgstAmount = new SimpleDoubleProperty(taxableValue * (sgst / 100.0));
        
        this.hsn = new SimpleDoubleProperty(0);
        this.totalTax = new SimpleDoubleProperty(0);
    }

    public ReportItem() {
        this.hsn = new SimpleDoubleProperty(0);
        this.taxableAmount = new SimpleDoubleProperty(0);
        this.cgstAmount = new SimpleDoubleProperty(0);
        this.sgstAmount = new SimpleDoubleProperty(0);
        this.totalTax = new SimpleDoubleProperty(0);
        this.saleDate = new SimpleStringProperty("");
        this.billId = new SimpleIntegerProperty(0);
        this.outletName = new SimpleStringProperty("");
        this.productName = new SimpleStringProperty("");
        this.quantity = new SimpleIntegerProperty(0);
        this.unitPrice = new SimpleDoubleProperty(0);
        this.cgst = new SimpleDoubleProperty(0);
        this.sgst = new SimpleDoubleProperty(0);
        this.total = new SimpleDoubleProperty(0);
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
    public DoubleProperty totalProperty() { return total; }
    public DoubleProperty hsnProperty() { return hsn; }
    public DoubleProperty taxableAmountProperty() { return taxableAmount; }
    public DoubleProperty cgstAmountProperty() { return cgstAmount; }
    public DoubleProperty sgstAmountProperty() { return sgstAmount; }
    public DoubleProperty totalTaxProperty() { return totalTax; }

    // Regular Getters (if needed elsewhere)
    public String getSaleDate() { return saleDate.get(); }
    public int getBillId() { return billId.get(); }
    public String getOutletName() { return outletName.get(); }
    public String getProductName() { return productName.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getUnitPrice() { return unitPrice.get(); }
    public double getCgst() { return cgst.get(); }
    public double getSgst() { return sgst.get(); }
    public double getTotal() { return total.get(); }
    public double getPrice() {
        return unitPrice.get();
    }
    public double getTaxableAmount() {
        return taxableAmount.get();
    }
    public double getCgstAmount(){
        return cgstAmount.get();
    }
    public double getSgstAmount(){
        return sgstAmount.get();
    }
    public double getHsn() { return hsn.get(); }
    public double getTotalTax() { return totalTax.get(); }

    public void setHsn(double hsn) { this.hsn.set(hsn); }
    public void setTaxableAmount(double value) { this.taxableAmount.set(value); }
    public void setCgstAmount(double value) { this.cgstAmount.set(value); }
    public void setSgstAmount(double value) { this.sgstAmount.set(value); }
    public void setTotalTax(double value) { this.totalTax.set(value); }
} 