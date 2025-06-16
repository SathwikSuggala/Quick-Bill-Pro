package DataBase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Outlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OutletsDataBase {

    // 1. Create a new outlet
    public static void createNewOutlet(String name, String address, String contactInfo) {
        String sql = "INSERT INTO outlets (name, address, contact_info) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setString(3, contactInfo);
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("New outlet created successfully.");
            } else {
                System.out.println("Failed to create outlet.");
            }

        } catch (SQLException e) {
            System.err.println("Error creating outlet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 2. Update an existing outlet
    public static void updateOutlet(int outletId, String newName, String newAddress, String newContactInfo) {
        String sql = "UPDATE outlets SET name = ?, address = ?, contact_info = ? WHERE outlet_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setString(2, newAddress);
            pstmt.setString(3, newContactInfo);
            pstmt.setInt(4, outletId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Outlet updated successfully.");
            } else {
                System.out.println("No outlet found with ID: " + outletId);
            }

        } catch (SQLException e) {
            System.err.println("Error updating outlet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 3. Get and print all existing outlets
    public ObservableList<Outlet> getAllOutlets() {

        ObservableList<Outlet> outletsList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM outlets";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("----- Existing Outlets -----");
            while (rs.next()) {
                int id = rs.getInt("outlet_id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                String contact = rs.getString("contact_info");

                Outlet outlet = new Outlet(id, name, address, contact);
                outletsList.add(outlet);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving outlets: " + e.getMessage());
        }
        return outletsList;
    }
}
