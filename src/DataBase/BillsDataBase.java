package DataBase;

import models.Bill;
import models.BillItem;
import models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillsDataBase {
    
    public static int createBill(int outletId, double totalAmount, String paymentType, String remarks) throws SQLException {
        String query = "INSERT INTO bills (outlet_id, total_amount, payment_type, remarks) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, outletId);
            stmt.setDouble(2, totalAmount);
            stmt.setString(3, paymentType);
            stmt.setString(4, remarks);
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Failed to get generated bill ID");
            }
        }
    }
    
    public static void addBillItem(int billId, int productId, int quantity, double price) throws SQLException {
        String query = "INSERT INTO bill_items (bill_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, billId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            
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
                    Bill bill = new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("outlet_id"),
                        rs.getDouble("total_amount"),
                        rs.getDate("bill_date"),
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
        String query = "SELECT bi.*, p.name as product_name FROM bill_items bi " +
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
                        rs.getDouble("price")
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
                    rs.getString("inlet_name")
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
            stmt.setDate(4, dueDate);
            
            stmt.executeUpdate();
        }
    }
} 