package DataBase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Inlet;

import java.sql.*;

public class InletsDataBase {

    // 1. Fetch all inlets with product count (based on purchases)
    public ObservableList<Inlet> fetchAllInletsWithProductCount() {
        ObservableList<Inlet> inletsList = FXCollections.observableArrayList();

        String query = """
            SELECT i.inlet_id, i.name, i.contact_info, COUNT(DISTINCT p.product_id) AS product_count
            FROM inlets i
            LEFT JOIN purchases p ON i.inlet_id = p.inlet_id
            GROUP BY i.inlet_id, i.name, i.contact_info
            ORDER BY i.inlet_id;
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("inlet_id");
                String name = rs.getString("name");
                String contact = rs.getString("contact_info");
                int count = rs.getInt("product_count");

                Inlet inlet = new Inlet(id, name, contact, count);
                inletsList.add(inlet);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching inlets: " + e.getMessage());
            e.printStackTrace();
        }

        return inletsList;
    }

    // 2. Add a new inlet
    public void addInlet(String name, String contactInfo) {
        String insertQuery = "INSERT INTO inlets (name, contact_info) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, name);
            pstmt.setString(2, contactInfo);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("New inlet added successfully.");
            } else {
                System.out.println("Failed to add new inlet.");
            }

        } catch (SQLException e) {
            System.err.println("Error adding inlet: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
