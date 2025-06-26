package DataBase;

import javafx.collections.FXCollections;
import models.Bill;
import models.BillItem;
import models.OutletCredit;
import models.Product;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillsDataBase {
    
    public static int createBill(int outletId, double totalCGST, double totalSGST, double totalAmount, 
                                String paymentType, String remarks, Date billDate) throws SQLException {
        String query = "INSERT INTO bills (outlet_id, total_CGST, total_SGST, total_amount, payment_type, remarks, bill_date) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, outletId);
            stmt.setDouble(2, totalCGST);
            stmt.setDouble(3, totalSGST);
            stmt.setDouble(4, totalAmount);
            stmt.setString(5, paymentType);
            stmt.setString(6, remarks);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            stmt.setString(7, dateFormat.format(billDate));
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Failed to get generated bill ID");
            }
        }
    }
    
    public static void addBillItem(int billId, int productId, int quantity, double price, 
                                 double cgst, double sgst) throws SQLException {
        String query = "INSERT INTO bill_items (bill_id, product_id, quantity, price, CGST, SGST) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, billId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            stmt.setDouble(5, cgst);
            stmt.setDouble(6, sgst);
            
            stmt.executeUpdate();
            
            // Update stock
            updateStock(productId, -quantity);
        }
    }
    
    private static void updateStock(int productId, int quantityChange) throws SQLException {
        String query = "UPDATE stock SET quantity = quantity + ? WHERE product_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, quantityChange);
            stmt.setInt(2, productId);
            
            stmt.executeUpdate();
        }
    }
    
    public static List<Bill> getBillsByOutlet(int outletId) throws SQLException {
        String query = "SELECT * FROM bills WHERE outlet_id = ? ORDER BY bill_date DESC";
        List<Bill> bills = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, outletId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String dateString = rs.getString("bill_date");
                    Date billDate = parseDate(dateString);

                    Bill bill = new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("outlet_id"),
                        rs.getDouble("total_CGST"),
                        rs.getDouble("total_SGST"),
                        rs.getDouble("total_amount"),
                        billDate,
                        rs.getString("payment_type"),
                        rs.getString("remarks")
                    );
                    bills.add(bill);
                }
            }
        }
        return bills;
    }
    
    public static List<BillItem> getBillItems(int billId) throws SQLException {
        String query = "SELECT bi.*, p.name as product_name, p.HSN as hsn FROM bill_items bi " +
                      "JOIN products p ON bi.product_id = p.product_id " +
                      "WHERE bi.bill_id = ?";
        List<BillItem> items = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, billId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BillItem item = new BillItem(
                        rs.getInt("bill_item_id"),
                        rs.getInt("bill_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("CGST"),
                        rs.getDouble("SGST"),
                        rs.getDouble("hsn")
                    );
                    items.add(item);
                }
            }
        }
        return items;
    }
    
    public static List<Product> getAvailableProducts() throws SQLException {
        String query = "SELECT p.*, s.quantity, i.name as inlet_name FROM products p " +
                      "JOIN stock s ON p.product_id = s.product_id " +
                      "JOIN inlets i ON p.inlet_id = i.inlet_id " +
                      "WHERE s.quantity > 0";
        List<Product> products = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("unit_price"),
                    rs.getInt("quantity"),
                    rs.getString("inlet_name"),
                    rs.getDouble("CGST"),
                    rs.getDouble("SGST"),
                    rs.getDouble("HSN")
                );
                products.add(product);
            }
        }
        return products;
    }
    
    public static void createOutletCredit(int outletId, int billId, double creditAmount, Date dueDate) throws SQLException {
        String query = "INSERT INTO outlet_credits (outlet_id, bill_id, credit_amount, due_date) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, outletId);
            stmt.setInt(2, billId);
            stmt.setDouble(3, creditAmount);
            stmt.setDate(4, new java.sql.Date(dueDate.getTime()));
            
            stmt.executeUpdate();
        }
    }
    
    public static List<OutletCredit> getAllCredits() throws SQLException {
        System.out.println("Executing getAllCredits query...");
        String query = "SELECT oc.*, o.name as outlet_name, " +
                      "COALESCE(SUM(cp.payment_amount), 0) as paid_amount, " +
                      "strftime('%Y-%m-%d', oc.credit_date) as formatted_credit_date, " +
                      "strftime('%Y-%m-%d', oc.due_date) as formatted_due_date " +
                      "FROM outlet_credits oc " +
                      "JOIN outlets o ON oc.outlet_id = o.outlet_id " +
                      "LEFT JOIN credit_payments cp ON oc.credit_id = cp.credit_id " +
                      "GROUP BY oc.credit_id " +
                      "ORDER BY oc.credit_date DESC";
        
        List<OutletCredit> credits = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("Query executed successfully, processing results...");
            while (rs.next()) {
                try {
                    double paidAmount = rs.getDouble("paid_amount");
                    double creditAmount = rs.getDouble("credit_amount");
                    String dueDateStr = rs.getString("formatted_due_date");
                    String creditDateStr = rs.getString("formatted_credit_date");
                    
                    System.out.println("Processing credit - ID: " + rs.getInt("credit_id") + 
                                     ", Due Date: " + dueDateStr);
                    
                    java.sql.Date dueDate = dueDateStr != null ? java.sql.Date.valueOf(dueDateStr) : null;
                    java.sql.Date creditDate = creditDateStr != null ? java.sql.Date.valueOf(creditDateStr) : null;
                    
                    String status = determineCreditStatus(paidAmount, creditAmount, dueDate);
                    
                    OutletCredit credit = new OutletCredit(
                        rs.getInt("credit_id"),
                        rs.getInt("outlet_id"),
                        rs.getString("outlet_name"),
                        rs.getInt("bill_id"),
                        creditAmount,
                        creditDate,
                        dueDate,
                        status,
                        paidAmount
                    );
                    credits.add(credit);
                } catch (Exception e) {
                    System.err.println("Error processing credit row: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("Total credits processed: " + credits.size());
        }
        return credits;
    }
    
    public static List<OutletCredit> getCreditsByOutlet(int outletId) throws SQLException {
        System.out.println("Executing getCreditsByOutlet query for outlet ID: " + outletId);
        String query = "SELECT oc.*, o.name as outlet_name, " +
                      "COALESCE(SUM(cp.payment_amount), 0) as paid_amount, " +
                      "strftime('%Y-%m-%d', oc.credit_date) as formatted_credit_date, " +
                      "strftime('%Y-%m-%d', oc.due_date) as formatted_due_date " +
                      "FROM outlet_credits oc " +
                      "JOIN outlets o ON oc.outlet_id = o.outlet_id " +
                      "LEFT JOIN credit_payments cp ON oc.credit_id = cp.credit_id " +
                      "WHERE oc.outlet_id = ? " +
                      "GROUP BY oc.credit_id " +
                      "ORDER BY oc.credit_date DESC";
        
        List<OutletCredit> credits = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, outletId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("Query executed successfully, processing results...");
                while (rs.next()) {
                    try {
                        double paidAmount = rs.getDouble("paid_amount");
                        double creditAmount = rs.getDouble("credit_amount");
                        String dueDateStr = rs.getString("formatted_due_date");
                        String creditDateStr = rs.getString("formatted_credit_date");
                        
                        System.out.println("Processing credit - ID: " + rs.getInt("credit_id") + 
                                         ", Due Date: " + dueDateStr);
                        
                        java.sql.Date dueDate = dueDateStr != null ? java.sql.Date.valueOf(dueDateStr) : null;
                        java.sql.Date creditDate = creditDateStr != null ? java.sql.Date.valueOf(creditDateStr) : null;
                        
                        String status = determineCreditStatus(paidAmount, creditAmount, dueDate);
                        
                        OutletCredit credit = new OutletCredit(
                            rs.getInt("credit_id"),
                            rs.getInt("outlet_id"),
                            rs.getString("outlet_name"),
                            rs.getInt("bill_id"),
                            creditAmount,
                            creditDate,
                            dueDate,
                            status,
                            paidAmount
                        );
                        credits.add(credit);
                    } catch (Exception e) {
                        System.err.println("Error processing credit row: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                System.out.println("Total credits processed: " + credits.size());
            }
        }
        return credits;
    }
    
    public static void addCreditPayment(int creditId, double amount, String paymentMode, String remarks) throws SQLException {
        String query = "INSERT INTO credit_payments (credit_id, payment_amount, payment_mode) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, creditId);
            stmt.setDouble(2, amount);
            stmt.setString(3, paymentMode);
            
            stmt.executeUpdate();
            
            // Update credit status
            updateCreditStatus(creditId);
        }
    }
    
    private static void updateCreditStatus(int creditId) throws SQLException {
        String query = "UPDATE outlet_credits " +
                      "SET status = ( " +
                      "    SELECT CASE " +
                      "        WHEN COALESCE(SUM(cp.payment_amount), 0) >= credit_amount THEN 'PAID' " +
                      "        WHEN COALESCE(SUM(cp.payment_amount), 0) > 0 THEN 'PARTIALLY_PAID' " +
                      "        WHEN due_date < DATE('now') THEN 'OVERDUE' " +
                      "        ELSE 'PENDING' " +
                      "    END " +
                      "    FROM credit_payments cp " +
                      "    WHERE cp.credit_id = outlet_credits.credit_id " +
                      ") " +
                      "WHERE credit_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, creditId);
            stmt.executeUpdate();
        }
    }
    
    private static String determineCreditStatus(double paidAmount, double creditAmount, java.sql.Date dueDate) {
        if (paidAmount >= creditAmount) {
            return "PAID";
        } else if (paidAmount > 0) {
            return "PARTIALLY_PAID";
        } else if (dueDate != null && dueDate.before(new java.sql.Date(System.currentTimeMillis()))) {
            return "OVERDUE";
        } else {
            return "PENDING";
        }
    }
    
    public static Bill getBillById(int billId) throws SQLException {
        String query = "SELECT b.*, o.name as outlet_name, o.address as outlet_address FROM bills b " +
                      "JOIN outlets o ON b.outlet_id = o.outlet_id " +
                      "WHERE b.bill_id = ?";
        Bill bill = null;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, billId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dateString = rs.getString("bill_date");
                    Date billDate = parseDate(dateString);

                    bill = new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("outlet_id"),
                        rs.getDouble("total_CGST"),
                        rs.getDouble("total_SGST"),
                        rs.getDouble("total_amount"),
                        billDate,
                        rs.getString("payment_type"),
                        rs.getString("remarks")
                    );
                    bill.setBillItems(FXCollections.observableArrayList(getBillItems(billId)));
                }
            }
        }
        return bill;
    }

    public static String getOutletNameForBill(int billId) throws SQLException {
        String query = "SELECT o.name FROM bills b JOIN outlets o ON b.outlet_id = o.outlet_id WHERE b.bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, billId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return null;
    }

    public static String getOutletAddressForBill(int billId) throws SQLException {
        String query = "SELECT o.address FROM bills b JOIN outlets o ON b.outlet_id = o.outlet_id WHERE b.bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, billId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("address");
                }
            }
        }
        return null;
    }

    private static Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // Try parsing as YYYY-MM-DD
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            try {
                // If that fails, try parsing as a Unix timestamp (long string)
                return new Date(Long.parseLong(dateString));
            } catch (NumberFormatException ex) {
                System.err.println("Error parsing date string: " + dateString + ", Reason: " + ex.getMessage());
                return null; // Or throw an exception, depending on desired error handling
            }
        }
    }

    public static List<Bill> getAllBills() throws SQLException {
        String query = "SELECT * FROM bills ORDER BY bill_date DESC";
        List<Bill> bills = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String dateString = rs.getString("bill_date");
                Date billDate = parseDate(dateString);
                int billId = rs.getInt("bill_id");
                Bill bill = new Bill(
                    billId,
                    rs.getInt("outlet_id"),
                    rs.getDouble("total_CGST"),
                    rs.getDouble("total_SGST"),
                    rs.getDouble("total_amount"),
                    billDate,
                    rs.getString("payment_type"),
                    rs.getString("remarks")
                );
                bill.setBillItems(FXCollections.observableArrayList(getBillItems(billId)));
                bills.add(bill);
            }
        }
        return bills;
    }
} 