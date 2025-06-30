package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class DBConnection {
    private static final String DB_NAME = "inventory_db.sqlite"; // SQLite file
    private static final String DB_URL;
    static {
        String userHome = System.getProperty("user.home");
        String dbDir = userHome + File.separator + "QuickBillPro";
        new File(dbDir).mkdirs();
        DB_URL = "jdbc:sqlite:" + dbDir + File.separator + DB_NAME;
    }

    // Returns a connection to the SQLite database file
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);

        // Set busy timeout to 5 seconds (5000 ms) to handle concurrent access
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA busy_timeout = 5000");
        }

        return conn;
    }

    public static String getDatabaseDirectory() {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + "QuickBillPro";
    }
}

