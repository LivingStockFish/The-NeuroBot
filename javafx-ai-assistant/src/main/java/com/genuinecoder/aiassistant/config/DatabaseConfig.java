package com.genuinecoder.aiassistant.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {

    // Absolute path to your database
    private static final String DB_URL = "jdbc:sqlite:C:/Users/ASUS/Downloads/NeuroBot - Copy/users.db";

    static {
        try {
            // Load the SQLite JDBC driver explicitly
            Class.forName("org.sqlite.JDBC");

            // Initialize the database and create table if not exists
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                String createTableQuery = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        email TEXT UNIQUE NOT NULL,
                        password TEXT NOT NULL,
                        role TEXT NOT NULL
                    );
                """;
                stmt.execute(createTableQuery);
                System.out.println("✅ Database connected and user table ready.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ SQLite JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Database initialization error: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
