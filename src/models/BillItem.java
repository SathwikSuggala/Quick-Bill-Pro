package models;

public class BillItem {
    private int billItemId;
    private int billId;
    private int productId;
    private String productName;
    private int quantity;
    private double price;
    private double cgst;
    private double sgst;
    
    public BillItem(int billItemId, int billId, int productId, String productName, 
                   int quantity, double price, double cgst, double sgst) {
        this.billItemId = billItemId;
        this.billId = billId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.cgst = cgst;
        this.sgst = sgst;
    }
    
    // Getters
    public int getBillItemId() { return billItemId; }
    public int getBillId() { return billId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getCgst() { return cgst; }
    public double getSgst() { return sgst; }
    public double getTotal() { 
        double subtotal = quantity * price;
        double cgstAmount = subtotal * (cgst / 100.0);
        double sgstAmount = subtotal * (sgst / 100.0);
        return subtotal + cgstAmount + sgstAmount;
    }
    public double getCgstAmount() {
        return (quantity * price) * (cgst / 100.0);
    }
    public double getSgstAmount() {
        return (quantity * price) * (sgst / 100.0);
    }
    
    // Setters
    public void setBillItemId(int billItemId) { this.billItemId = billItemId; }
    public void setBillId(int billId) { this.billId = billId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setCgst(double cgst) { this.cgst = cgst; }
    public void setSgst(double sgst) { this.sgst = sgst; }
} 