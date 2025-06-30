package DataBase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class ProductsDataBase {
    private static final Logger logger = LogManager.getLogger(ProductsDataBase.class);

    // 1. Get products by inlet name
    public ObservableList<Product> getProductsByInletName(String inletName) {
        ObservableList<Product> productsList = FXCollections.observableArrayList();
        String sql = "SELECT p.product_id, p.name, p.description, p.unit_price, " +
                "s.quantity, p.CGST, p.SGST, p.HSN " +
                "FROM products p " +
                "JOIN purchases pu ON p.product_id = pu.product_id " +
                "JOIN inlets i ON pu.inlet_id = i.inlet_id " +
                "JOIN stock s ON p.product_id = s.product_id " +
                "WHERE i.name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, inletName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("unit_price"),
                            rs.getInt("quantity"),
                            inletName,
                            rs.getDouble("CGST"),
                            rs.getDouble("SGST"),
                            rs.getDouble("HSN")
                    );
                    productsList.add(product);
                }
            }

        } catch (SQLException e) {
            logger.error("Error fetching products: " + e.getMessage(), e);
        }
        return productsList;
    }

    // 2. Add product for inlet
    public void addProductForInlet(String inletName, String productName, String description,
                                   double unitPrice, double cgst, double sgst, double hsn) {
        String insertProductQuery = "INSERT INTO products (inlet_id, name, description, unit_price, CGST, SGST, HSN) " +
                "VALUES ((SELECT inlet_id FROM inlets WHERE name = ?), ?, ?, ?, ?, ?, ?)";
        String insertStockQuery = "INSERT INTO stock (product_id, quantity) VALUES (last_insert_rowid(), ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement pstmt = conn.prepareStatement(insertProductQuery)) {
                    pstmt.setString(1, inletName);
                    pstmt.setString(2, productName);
                    pstmt.setString(3, description);
                    pstmt.setDouble(4, unitPrice);
                    pstmt.setDouble(5, cgst);
                    pstmt.setDouble(6, sgst);
                    pstmt.setDouble(7, hsn);
                    pstmt.executeUpdate();
                }

                try (PreparedStatement pstmt = conn.prepareStatement(insertStockQuery)) {
                    pstmt.setInt(1, 0);  // Initially 0 stock; can be updated later
                    pstmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Error adding product: " + e.getMessage(), e);
        }
    }

    // 3. Update product of inlet
    public void updateProductOfInlet(int productId, String productName, String description,
                                     double unitPrice, int quantity, double cgst, double sgst, double hsn) {
        String updateProductQuery = "UPDATE products SET name = ?, description = ?, unit_price = ?, CGST = ?, SGST = ?, HSN = ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(updateProductQuery)) {
                pstmt.setString(1, productName);
                pstmt.setString(2, description);
                pstmt.setDouble(3, unitPrice);
                pstmt.setDouble(4, cgst);
                pstmt.setDouble(5, sgst);
                pstmt.setDouble(6, hsn);
                pstmt.setInt(7, productId);
                pstmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Error updating product: " + e.getMessage(), e);
        }
    }

    // 4. Get all products with available quantity
    public ObservableList<Product> getAllProductsWithAvailableQuantity() {
        ObservableList<Product> productsList = FXCollections.observableArrayList();
        String sql = "SELECT p.product_id, p.name, p.description, p.unit_price, " +
                "s.quantity, i.name as inlet_name, p.CGST, p.SGST, p.HSN " +
                "FROM products p " +
                "LEFT JOIN stock s ON p.product_id = s.product_id " +
                "LEFT JOIN inlets i ON p.inlet_id = i.inlet_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
                productsList.add(product);
            }

        } catch (SQLException e) {
            logger.error("Error fetching product stock: " + e.getMessage(), e);
        }
        return productsList;
    }

    // 5. Search products by name
    public ObservableList<Product> searchProductsByName(String partialName) {
        ObservableList<Product> productsList = FXCollections.observableArrayList();
        String sql = "SELECT p.product_id, p.name, p.description, p.unit_price, " +
                "s.quantity, p.CGST, p.SGST, p.HSN, " +
                "(SELECT i.name FROM inlets i " +
                "JOIN purchases pu ON i.inlet_id = pu.inlet_id " +
                "WHERE pu.product_id = p.product_id " +
                "ORDER BY pu.purchase_date DESC LIMIT 1) as inlet_name " +
                "FROM products p " +
                "LEFT JOIN stock s ON p.product_id = s.product_id " +
                "WHERE LOWER(p.name) LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + partialName.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
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
                    productsList.add(product);
                }
            }

        } catch (SQLException e) {
            logger.error("Error searching products: " + e.getMessage(), e);
        }
        return productsList;
    }

    // 6. Add purchase and update price + stock
    public void addPurchaseForProduct(String inletName, String productName, int quantity, double costPrice) {
        String insertPurchaseQuery = "INSERT INTO purchases (inlet_id, product_id, quantity, cost_price, purchase_date) " +
                "VALUES ((SELECT inlet_id FROM inlets WHERE name = ?), " +
                "(SELECT product_id FROM products WHERE name = ?), ?, ?, DATE('now'))";

        //String updatePriceQuery = "UPDATE products SET unit_price = ? WHERE name = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Insert into purchases
                try (PreparedStatement pstmt = conn.prepareStatement(insertPurchaseQuery)) {
                    pstmt.setString(1, inletName);
                    pstmt.setString(2, productName);
                    pstmt.setInt(3, quantity);
                    pstmt.setDouble(4, costPrice);
                    pstmt.executeUpdate();
                }

//                // Update product price
//                try (PreparedStatement pstmt = conn.prepareStatement(updatePriceQuery)) {
//                    pstmt.setDouble(1, costPrice);
//                    pstmt.setString(2, productName);
//                    pstmt.executeUpdate();
//                }

                // Update quantity using same connection
                addQuantity(conn, productName, quantity);

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            logger.error("Error adding purchase: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }


    // 7. Add stock quantity
    public void addQuantity(Connection conn, String productName, int quantity) throws SQLException {
        String getQuantityQuery = "SELECT quantity FROM stock WHERE product_id = (SELECT product_id FROM products WHERE name = ?)";
        String updateStockQuery = "UPDATE stock SET quantity = ? WHERE product_id = (SELECT product_id FROM products WHERE name = ?)";

        int existingQuantity = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(getQuantityQuery)) {
            pstmt.setString(1, productName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                existingQuantity = rs.getInt("quantity");
            }
        }

        int newQuantity = existingQuantity + quantity;

        try (PreparedStatement pstmt = conn.prepareStatement(updateStockQuery)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setString(2, productName);
            pstmt.executeUpdate();
        }
    }

}
