package models;

import java.time.LocalDate;

public class Purchase {
    private int purchaseId;
    private int inletId;
    private String inletName;
    private int productId;
    private String productName;
    private int quantity;
    private double costPrice;
    private LocalDate purchaseDate;

    public Purchase(int purchaseId, int inletId, String inletName, int productId, 
                   String productName, int quantity, double costPrice, LocalDate purchaseDate) {
        this.purchaseId = purchaseId;
        this.inletId = inletId;
        this.inletName = inletName;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.costPrice = costPrice;
        this.purchaseDate = purchaseDate;
    }

    // Getters
    public int getPurchaseId() { return purchaseId; }
    public int getInletId() { return inletId; }
    public String getInletName() { return inletName; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getCostPrice() { return costPrice; }
    public LocalDate getPurchaseDate() { return purchaseDate; }

    // Setters
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }
    public void setInletId(int inletId) { this.inletId = inletId; }
    public void setInletName(String inletName) { this.inletName = inletName; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
} 