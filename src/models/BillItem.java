package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BillItem {
    private final IntegerProperty billItemId;
    private final IntegerProperty billId;
    private final IntegerProperty productId;
    private final StringProperty productName;
    private final IntegerProperty quantity;
    private final DoubleProperty price;
    private final DoubleProperty cgst;
    private final DoubleProperty sgst;
    private final DoubleProperty total;
    private final DoubleProperty cgstAmount;
    private final DoubleProperty sgstAmount;
    private final DoubleProperty taxableAmount;
    private final DoubleProperty hsn;
    
    public BillItem(int billItemId, int billId, int productId, String productName, 
                   int quantity, double price, double cgst, double sgst, double hsn) {
        this.billItemId = new SimpleIntegerProperty(billItemId);
        this.billId = new SimpleIntegerProperty(billId);
        this.productId = new SimpleIntegerProperty(productId);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.cgst = new SimpleDoubleProperty(cgst);
        this.sgst = new SimpleDoubleProperty(sgst);
        this.hsn = new SimpleDoubleProperty(hsn);

        // Bind properties for automatic calculation
        this.taxableAmount = new SimpleDoubleProperty();
        this.taxableAmount.bind(this.quantity.multiply(this.price));

        this.cgstAmount = new SimpleDoubleProperty();
        this.cgstAmount.bind(this.taxableAmount.multiply(this.cgst).divide(100.0));

        this.sgstAmount = new SimpleDoubleProperty();
        this.sgstAmount.bind(this.taxableAmount.multiply(this.sgst).divide(100.0));

        this.total = new SimpleDoubleProperty();
        this.total.bind(this.taxableAmount.add(this.cgstAmount).add(this.sgstAmount));
    }
    
    // Getters for properties
    public IntegerProperty billItemIdProperty() { return billItemId; }
    public IntegerProperty billIdProperty() { return billId; }
    public IntegerProperty productIdProperty() { return productId; }
    public StringProperty productNameProperty() { return productName; }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty priceProperty() { return price; }
    public DoubleProperty cgstProperty() { return cgst; }
    public DoubleProperty sgstProperty() { return sgst; }
    public DoubleProperty totalProperty() { return total; }
    public DoubleProperty cgstAmountProperty() { return cgstAmount; }
    public DoubleProperty sgstAmountProperty() { return sgstAmount; }
    public DoubleProperty taxableAmountProperty() { return taxableAmount; }
    public DoubleProperty hsnProperty() { return hsn; }

    // Existing Getters (can remain for other uses)
    public int getBillItemId() { return billItemId.get(); }
    public int getBillId() { return billId.get(); }
    public int getProductId() { return productId.get(); }
    public String getProductName() { return productName.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getPrice() { return price.get(); }
    public double getCgst() { return cgst.get(); }
    public double getSgst() { return sgst.get(); }
    public double getTotal() { return total.get(); }
    public double getCgstAmount() { return cgstAmount.get(); }
    public double getSgstAmount() { return sgstAmount.get(); }
    public double getTaxableAmount() { return taxableAmount.get(); }
    public double getHsn() { return hsn.get(); }
    
    // Setters (if needed, currently not used by BillSoftCopyController)
    public void setBillItemId(int billItemId) { this.billItemId.set(billItemId); }
    public void setBillId(int billId) { this.billId.set(billId); }
    public void setProductId(int productId) { this.productId.set(productId); }
    public void setProductName(String productName) { this.productName.set(productName); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public void setPrice(double price) { this.price.set(price); }
    public void setCgst(double cgst) { this.cgst.set(cgst); }
    public void setSgst(double sgst) { this.sgst.set(sgst); }
    public void setHsn(double hsn) { this.hsn.set(hsn); }
} 