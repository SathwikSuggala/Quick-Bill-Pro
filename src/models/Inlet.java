package models;

public class Inlet {
    private int id;
    private String name;
    private String contactInfo;
    private int productCount;

    public Inlet(int id, String name, String contactInfo, int productCount) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.productCount = productCount;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }
    public int getProductCount() { return productCount; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setProductCount(int productCount) { this.productCount = productCount; }
} 