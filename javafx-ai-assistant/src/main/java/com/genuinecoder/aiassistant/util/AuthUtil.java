package com.genuinecoder.aiassistant.util;

import com.genuinecoder.aiassistant.config.DatabaseConfig;
import java.sql.*;

public class AuthUtil {

    public static boolean registerUser(String name, String email, String password, String role) {
        String hashedPassword = PasswordUtil.hashPassword(password);
        String query = "INSERT INTO users(name, email, password, role) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, role);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public static String loginUser(String email, String password) {
        String query = "SELECT password, role FROM users WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (PasswordUtil.verifyPassword(password, storedHash)) {
                    return rs.getString("role"); // Return User/Admin
                }
            }

        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
        }
        return null;
    }

    public static String getRoleByEmail(String email) {
    String query = "SELECT role FROM users WHERE email = ?";
    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getString("role");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
    }


    public static int getUserIdByEmail(String email) {
        String query = "SELECT id FROM users WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Not found
    }

    public static boolean userExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("User existence check failed: " + e.getMessage());
            return false;
        }
    }
}
