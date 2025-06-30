package DataBase;

import java.sql.Connection;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InventoryDBSetup {
    private static final Logger logger = LogManager.getLogger(InventoryDBSetup.class);

    // SQLite doesn't require DB creation separately

    // Step 1: Skipped createDatabaseIfNotExists (not applicable in SQLite)

    // Step 2: Create Tables
    public static void createTablesIfNotExist() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Inlets
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS inlets (" +
                    "inlet_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "contact_info TEXT" +
                ");"
            );

            // Users
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "password TEXT" +
                ");"
            );

            // Outlets
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS outlets (" +
                    "outlet_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "address TEXT," +
                    "email TEXT," +
                    "GSTIN TEXT," +
                    "contact_info TEXT" +
                ");"
            );

            // Products
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS products (" +
                    "product_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "inlet_id INTEGER NOT NULL," +
                    "name TEXT NOT NULL," +
                    "description TEXT," +
                    "HSN REAL," +
                    "unit_price REAL NOT NULL," +
                    "CGST REAL NOT NULL," +
                    "SGST REAL NOT NULL," +
                    "FOREIGN KEY (inlet_id) REFERENCES inlets(inlet_id)" +
                ");"
            );

            // Purchases
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS purchases (" +
                    "purchase_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "inlet_id INTEGER NOT NULL," +
                    "product_id INTEGER NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "cost_price REAL NOT NULL," +
                    "purchase_date DATE DEFAULT (DATE('now'))," +
                    "FOREIGN KEY (inlet_id) REFERENCES inlets(inlet_id)," +
                    "FOREIGN KEY (product_id) REFERENCES products(product_id)" +
                ");"
            );

            // Sales
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS sales (" +
                    "sale_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "outlet_id INTEGER NOT NULL," +
                    "product_id INTEGER NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "selling_price REAL NOT NULL," +
                    "sale_date DATE DEFAULT (DATE('now'))," +
                    "FOREIGN KEY (outlet_id) REFERENCES outlets(outlet_id)," +
                    "FOREIGN KEY (product_id) REFERENCES products(product_id)" +
                ");"
            );

            // Bills
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS bills (" +
                    "bill_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "outlet_id INTEGER NOT NULL," +
                    "total_CGST REAL NOT NULL," +
                    "total_SGST REAL NOT NULL," +
                    "total_amount REAL NOT NULL," +
                    "bill_date DATE DEFAULT (DATE('now'))," +
                    "payment_type TEXT DEFAULT 'CASH'," +
                    "remarks TEXT," +
                    "FOREIGN KEY (outlet_id) REFERENCES outlets(outlet_id)" +
                ");"
            );

            // Outlet Credits
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS outlet_credits (" +
                    "credit_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "outlet_id INTEGER NOT NULL," +
                    "bill_id INTEGER," +
                    "credit_amount REAL NOT NULL," +
                    "credit_date DATE DEFAULT (DATE('now'))," +
                    "due_date DATE," +
                    "status TEXT DEFAULT 'PENDING'," +
                    "FOREIGN KEY (outlet_id) REFERENCES outlets(outlet_id)," +
                    "FOREIGN KEY (bill_id) REFERENCES bills(bill_id)" +
                ");"
            );

            // Credit Payments
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS credit_payments (" +
                    "payment_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "credit_id INTEGER NOT NULL," +
                    "payment_amount REAL NOT NULL," +
                    "payment_date DATE DEFAULT (DATE('now'))," +
                    "payment_mode TEXT," +
                    "FOREIGN KEY (credit_id) REFERENCES outlet_credits(credit_id)" +
                ");"
            );

            // Bill Items
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS bill_items (" +
                    "bill_item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "bill_id INTEGER NOT NULL," +
                    "product_id INTEGER NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "price REAL NOT NULL," +
                    "CGST REAL NOT NULL," +
                    "SGST REAL NOT NULL," +
                    "FOREIGN KEY (bill_id) REFERENCES bills(bill_id)," +
                    "FOREIGN KEY (product_id) REFERENCES products(product_id)" +
                ");"
            );

            // Stock
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS stock (" +
                    "stock_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "product_id INTEGER NOT NULL UNIQUE," +
                    "quantity INTEGER NOT NULL," +
                    "FOREIGN KEY (product_id) REFERENCES products(product_id)" +
                ");"
            );

            logger.info("All tables created successfully (if not already present).");

        } catch (Exception e) {
            logger.error("Error creating tables: " + e.getMessage(), e);
        }
    }

    public static void resetAllDataExceptCore() throws Exception {
        String[] tables = {"bill_items", "credit_payments", "outlet_credits", "bills", "purchases", "sales"};
        try (java.sql.Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = OFF;");
                for (String table : tables) {
                    stmt.executeUpdate("DELETE FROM " + table + ";");
                    stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='" + table + "';");
                }
                stmt.execute("PRAGMA foreign_keys = ON;");
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
