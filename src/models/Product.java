package models;

public class Product {
    private int id;
    private String name;
    private String description;
    private double unitPrice;
    private int quantity;
    private String inletName;
    private double cgst;
    private double sgst;
    private double hsn;

    public Product(int id, String name, String description, double unitPrice, int quantity, String inletName, double cgst, double sgst, double hsn) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.inletName = inletName;
        this.cgst = cgst;
        this.sgst = sgst;
        this.hsn = hsn;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public String getInletName() { return inletName; }
    public double getCgst() { return cgst; }
    public double getSgst() { return sgst; }
    public double getHsn() { return hsn; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setInletName(String inletName) { this.inletName = inletName; }
    public void setCgst(double cgst) { this.cgst = cgst; }
    public void setSgst(double sgst) { this.sgst = sgst; }
    public void setHsn(double hsn) { this.hsn = hsn; }
} 