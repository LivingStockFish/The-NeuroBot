package com.genuinecoder.aiassistant.util;

import com.genuinecoder.aiassistant.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatHistoryUtil {

    // Use DatabaseConfig so the same DB path is used everywhere
    private static Connection connect() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    /**
     * Upsert-style save: if a chat (userId + title) exists -> update conversation,
     * otherwise insert a new row.
     */
    public static void saveChat(int userId, String chatTitle, String conversation) {
        if (chatTitle == null) chatTitle = "Chat";
        String selectQuery = "SELECT id FROM chat_history WHERE user_id = ? AND chat_title = ?";
        String insertQuery = "INSERT INTO chat_history (user_id, chat_title, conversation) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE chat_history SET conversation = ?, timestamp = CURRENT_TIMESTAMP WHERE user_id = ? AND chat_title = ?";

        try (Connection conn = connect();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            selectStmt.setInt(1, userId);
            selectStmt.setString(2, chatTitle);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                // Update existing
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, conversation);
                    updateStmt.setInt(2, userId);
                    updateStmt.setString(3, chatTitle);
                    updateStmt.executeUpdate();
                }
            } else {
                // Insert new
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setString(2, chatTitle);
                    insertStmt.setString(3, conversation);
                    insertStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteChat(int userId, String chatTitle) {
        String query = "DELETE FROM chat_history WHERE user_id = ? AND chat_title = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, chatTitle);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void renameChat(int userId, String oldTitle, String newTitle) {
        String query = "UPDATE chat_history SET chat_title = ? WHERE user_id = ? AND chat_title = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newTitle);
            ps.setInt(2, userId);
            ps.setString(3, oldTitle);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return list of {chat_title, conversation} ordered by newest first.
     */
    public static List<String[]> getUserChats(int userId) {
        List<String[]> chats = new ArrayList<>();
        String sql = "SELECT chat_title, conversation FROM chat_history WHERE user_id = ? ORDER BY timestamp DESC";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                chats.add(new String[]{rs.getString("chat_title"), rs.getString("conversation")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chats;
    }

    /**
     * Save feedback
     */
    public static void saveFeedback(int userId, String feedbackText) {
        if (feedbackText == null || feedbackText.trim().isEmpty()) return;
        String sql = "INSERT INTO feedback (user_id, feedback) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, feedbackText);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Count user messages in a conversation text (used to set messagesInCurrentChat on load)
     */
    public static int countUserMessages(String conversation) {
        if (conversation == null || conversation.isBlank()) return 0;
        int count = 0;
        String[] lines = conversation.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("User:")) count++;
        }
        return count;
    }
}
