package models;

public class Outlet {
    private int id;
    private String name;
    private String address;
    private String contactInfo;
    private String email;
    private String gstin;

    public Outlet(int id, String name, String address, String contactInfo, String email, String gstin) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contactInfo = contactInfo;
        this.email = email;
        this.gstin = gstin;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getContactInfo() { return contactInfo; }
    public String getEmail() { return email; }
    public String getGstin() { return gstin; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setEmail(String email) { this.email = email; }
    public void setGstin(String gstin) { this.gstin = gstin; }
} 