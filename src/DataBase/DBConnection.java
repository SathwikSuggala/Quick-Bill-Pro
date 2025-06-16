package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String DB_NAME = "inventory_db.sqlite"; // SQLite file
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;

    // Returns a connection to the SQLite database file
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);

        // Set busy timeout to 5 seconds (5000 ms) to handle concurrent access
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA busy_timeout = 5000");
        }

        return conn;
    }
}

