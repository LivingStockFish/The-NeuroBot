package com.genuinecoder.aiassistant.gui;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminFeedbackController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox feedbackContainer;

    @FXML
    private ImageView backButton;

    @FXML
    public void initialize() {
        loadBackIcon();
        loadFeedbackFromDB();
    }

    private void loadBackIcon() {
        try {
            InputStream is = getClass().getResourceAsStream("/icon/back.png");
            if (is != null) {
                Image img = new Image(is);
                backButton.setImage(img);
            } else {
                System.err.println("Could not load back.png");
            }
        } catch (Exception e) {
            System.err.println("Error loading back.png: " + e.getMessage());
        }
    }

    @FXML
    public void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** --------------------------------------------------------------
     *  LOAD FEEDBACK FROM DATABASE
     *  -------------------------------------------------------------- */
    private void loadFeedbackFromDB() {

        String url = "jdbc:sqlite:users.db";

        String query = """
                SELECT user_name, rating_satisfaction, rating_usability,
                       rating_support, feedback_text, submitted_at
                FROM feedback
                """;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String user = rs.getString("user_name");
                String message = rs.getString("feedback_text");
                String date = rs.getString("submitted_at");

                int r1 = rs.getInt("rating_satisfaction");
                int r2 = rs.getInt("rating_usability");
                int r3 = rs.getInt("rating_support");

                FeedbackItem item = new FeedbackItem(
                        user,
                        message,
                        date,
                        r1, r2, r3
                );

                addFeedbackCard(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** --------------------------------------------------------------
     *  ADD CARD UI TO PAGE
     *  -------------------------------------------------------------- */
    private void addFeedbackCard(FeedbackItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/feedback_card.fxml"));
            Parent card = loader.load();

            FeedbackCardController controller = loader.getController();
            controller.setData(item);

            feedbackContainer.getChildren().add(card);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
