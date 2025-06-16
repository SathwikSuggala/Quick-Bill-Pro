package DataBase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Purchase;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PurchasesDataBase {

    // 1. Fetch all unique purchase dates
    public ObservableList<LocalDate> fetchAllPurchaseDates() {
        ObservableList<LocalDate> datesList = FXCollections.observableArrayList();
        
        String query = "SELECT DISTINCT strftime('%Y-%m-%d', purchase_date) as formatted_date FROM purchases ORDER BY purchase_date DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String dateStr = rs.getString("formatted_date");
                LocalDate date = LocalDate.parse(dateStr);
                datesList.add(date);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching purchase dates: " + e.getMessage());
            e.printStackTrace();
        }

        return datesList;
    }

    // 2. Fetch all purchases for a specific date
    public ObservableList<Purchase> fetchPurchasesByDate(LocalDate date) {
        ObservableList<Purchase> purchasesList = FXCollections.observableArrayList();
        
        String query = "SELECT p.purchase_id, p.inlet_id, i.name as inlet_name, " +
                      "p.product_id, pr.name as product_name, " +
                      "p.quantity, p.cost_price, strftime('%Y-%m-%d', p.purchase_date) as formatted_date " +
                      "FROM purchases p " +
                      "JOIN inlets i ON p.inlet_id = i.inlet_id " +
                      "JOIN products pr ON p.product_id = pr.product_id " +
                      "WHERE strftime('%Y-%m-%d', p.purchase_date) = ? " +
                      "ORDER BY p.purchase_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int purchaseId = rs.getInt("purchase_id");
                    int inletId = rs.getInt("inlet_id");
                    String inletName = rs.getString("inlet_name");
                    int productId = rs.getInt("product_id");
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    double costPrice = rs.getDouble("cost_price");
                    String dateStr = rs.getString("formatted_date");
                    LocalDate purchaseDate = LocalDate.parse(dateStr);

                    Purchase purchase = new Purchase(purchaseId, inletId, inletName, 
                                                   productId, productName, quantity, 
                                                   costPrice, purchaseDate);
                    purchasesList.add(purchase);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching purchases by date: " + e.getMessage());
            e.printStackTrace();
        }

        return purchasesList;
    }
} 