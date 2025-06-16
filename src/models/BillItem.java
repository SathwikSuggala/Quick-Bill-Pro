package models;

public class BillItem {
    private int billItemId;
    private int billId;
    private int productId;
    private String productName;
    private int quantity;
    private double price;
    
    public BillItem(int billItemId, int billId, int productId, String productName, int quantity, double price) {
        this.billItemId = billItemId;
        this.billId = billId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }
    
    // Getters
    public int getBillItemId() { return billItemId; }
    public int getBillId() { return billId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return quantity * price; }
    
    // Setters
    public void setBillItemId(int billItemId) { this.billItemId = billItemId; }
    public void setBillId(int billId) { this.billId = billId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
} 