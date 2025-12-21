package com.genuinecoder.aiassistant.gui;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class LogoutConfirmationController {

    @FXML private Button yesButton;
    @FXML private Button noButton;

    private boolean confirmed = false;

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void initialize() {
        yesButton.setOnAction(e -> {
            confirmed = true;
            closeWindow();
        });

        noButton.setOnAction(e -> {
            confirmed = false;
            closeWindow();
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) yesButton.getScene().getWindow();
        stage.close();
    }
}
