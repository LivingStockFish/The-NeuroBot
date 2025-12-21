package com.genuinecoder.aiassistant.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FeedbackCardController {

    @FXML private Label lblUser;
    @FXML private Label lblDate;
    @FXML private Label lblMessage;

    @FXML private Label lblUiRating;
    @FXML private Label lblAccuracyRating;
    @FXML private Label lblHelpRating;

    public void setData(FeedbackItem item) {

        lblUser.setText(item.getUser());
        lblDate.setText(item.getDate());
        lblMessage.setText(item.getMessage());

        lblUiRating.setText("⭐".repeat(item.getRatingUi()));
        lblAccuracyRating.setText("⭐".repeat(item.getRatingAccuracy()));
        lblHelpRating.setText("⭐".repeat(item.getRatingHelpfulness()));
    }
}
