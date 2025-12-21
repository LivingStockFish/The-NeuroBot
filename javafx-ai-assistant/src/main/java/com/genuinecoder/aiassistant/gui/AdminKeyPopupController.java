package com.genuinecoder.aiassistant.gui;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class AdminKeyPopupController {

    @FXML
    private PasswordField keyField;

    private boolean keyValidated = false;

    public boolean isKeyValidated() {
        return keyValidated;
    }

    @FXML
private void handleConfirm() {
    String enteredKey = keyField.getText().trim();

    if (enteredKey.equals("Vidyanshu")) {
        keyValidated = true;
        closeWindow();
    } else {
        keyField.clear();
        keyField.setStyle("-fx-border-color: red; -fx-border-width: 2;");
        keyField.setPromptText("Incorrect key!");
    }
}

    @FXML
    private void handleCancel() {
        keyValidated = false;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) keyField.getScene().getWindow();
        stage.close();
    }
}
