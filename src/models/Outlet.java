package models;

public class Outlet {
    private int id;
    private String name;
    private String address;
    private String contactInfo;

    public Outlet(int id, String name, String address, String contactInfo) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contactInfo = contactInfo;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getContactInfo() { return contactInfo; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
} 