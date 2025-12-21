package com.genuinecoder.aiassistant.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.sql.*;
import java.util.prefs.Preferences;

public class FeedbackController {

    @FXML private Button q1Star1, q1Star2, q1Star3, q1Star4, q1Star5;
    @FXML private Button q2Star1, q2Star2, q2Star3, q2Star4, q2Star5;
    @FXML private Button q3Star1, q3Star2, q3Star3, q3Star4, q3Star5;
    @FXML private TextArea feedbackText;

    private int q1Rating = 0;
    private int q2Rating = 0;
    private int q3Rating = 0;

    private static final String DB_URL = "jdbc:sqlite:users.db";

    @FXML
    public void initialize() {
        createFeedbackTableIfNotExists();
        initializeStars(q1Star1, q1Star2, q1Star3, q1Star4, q1Star5, 1);
        initializeStars(q2Star1, q2Star2, q2Star3, q2Star4, q2Star5, 2);
        initializeStars(q3Star1, q3Star2, q3Star3, q3Star4, q3Star5, 3);
    }

    /** Create feedback table */
    private void createFeedbackTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS feedback (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_name TEXT NOT NULL UNIQUE,
                rating_satisfaction INTEGER NOT NULL,
                rating_usability INTEGER NOT NULL,
                rating_support INTEGER NOT NULL,
                feedback_text TEXT,
                submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Initialize star buttons */
    private void initializeStars(Button s1, Button s2, Button s3, Button s4, Button s5, int question) {
        Button[] stars = {s1, s2, s3, s4, s5};

        for (int i = 0; i < stars.length; i++) {
            Button star = stars[i];
            int value = i + 1;

            star.setText("☆");
            star.setStyle("-fx-text-fill: gray; -fx-background-color: transparent; -fx-font-size: 24px;");

            star.setOnMouseEntered(e -> highlightHover(stars, value));
            star.setOnMouseExited(e -> updateStars(question));
            star.setOnAction(e -> {
                if (question == 1) q1Rating = value;
                else if (question == 2) q2Rating = value;
                else q3Rating = value;
                updateStars(question);
            });
        }
    }

    private void highlightHover(Button[] stars, int hoverValue) {
        for (int i = 0; i < stars.length; i++) {
            if (i < hoverValue) {
                stars[i].setText("★");
                stars[i].setStyle("-fx-text-fill: gold; -fx-background-color: transparent; -fx-font-size: 24px;");
            } else {
                stars[i].setText("☆");
                stars[i].setStyle("-fx-text-fill: gray; -fx-background-color: transparent; -fx-font-size: 24px;");
            }
        }
    }

    private void updateStars(int question) {
        Button[] stars;
        int rating;

        if (question == 1) {
            stars = new Button[]{q1Star1, q1Star2, q1Star3, q1Star4, q1Star5};
            rating = q1Rating;
        } else if (question == 2) {
            stars = new Button[]{q2Star1, q2Star2, q2Star3, q2Star4, q2Star5};
            rating = q2Rating;
        } else {
            stars = new Button[]{q3Star1, q3Star2, q3Star3, q3Star4, q3Star5};
            rating = q3Rating;
        }

        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setText("★");
                stars[i].setStyle("-fx-text-fill: gold; -fx-background-color: transparent; -fx-font-size: 24px;");
            } else {
                stars[i].setText("☆");
                stars[i].setStyle("-fx-text-fill: gray; -fx-background-color: transparent; -fx-font-size: 24px;");
            }
        }
    }

    /** Get user name using email */
    private String getUserNameFromDB(String email) {
        String sql = "SELECT name FROM users WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Unknown";
    }

    @FXML
    private void handleSubmit(ActionEvent event) {

        String feedback = feedbackText.getText();

        Preferences prefs = Preferences.userNodeForPackage(getClass());
        String email = prefs.get("email", null);

        String userName = getUserNameFromDB(email); // Fetch correct name

        saveFeedbackToDatabase(userName, q1Rating, q2Rating, q3Rating, feedback);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Thank you for your feedback!");
        alert.setContentText("Your feedback has been recorded successfully.");
        alert.showAndWait();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    /** Insert or Update (UPSERT) feedback */
    private void saveFeedbackToDatabase(String userName, int ratingSatisfaction, int ratingUsability,
                                        int ratingSupport, String feedbackText) {

        String sql = """
            INSERT INTO feedback(user_name, rating_satisfaction, rating_usability, rating_support, feedback_text)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(user_name)
            DO UPDATE SET
                rating_satisfaction = excluded.rating_satisfaction,
                rating_usability = excluded.rating_usability,
                rating_support = excluded.rating_support,
                feedback_text = excluded.feedback_text,
                submitted_at = CURRENT_TIMESTAMP;
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userName);
            pstmt.setInt(2, ratingSatisfaction);
            pstmt.setInt(3, ratingUsability);
            pstmt.setInt(4, ratingSupport);
            pstmt.setString(5, feedbackText);

            pstmt.executeUpdate();
            System.out.println("Feedback saved for: " + userName);

        } catch (SQLException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Database Error");
            alert.setContentText("Failed to save feedback: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
