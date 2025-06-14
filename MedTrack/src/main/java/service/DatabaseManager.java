// ✅ Manages SQLite database connections for MEDTRACK.
// Always returns a fresh connection to ensure thread-safety and avoid reuse issues.

package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:data/medtrack.db";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL); // always create new one
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("🔌 SQLite connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Failed to close DB connection: " + e.getMessage());
        }
    }
}
