package com.genuinecoder.aiassistant.gui;

import com.genuinecoder.aiassistant.util.AuthUtil;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.prefs.Preferences;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private VBox loginCard;
    @FXML private Label messageLabel;
    @FXML private StackPane rootPane;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) loginCard.getScene().getWindow();
            adjustPadding(stage.getWidth(), stage.getHeight());

            stage.widthProperty().addListener((obs, oldVal, newVal) ->
                    adjustPadding(newVal.doubleValue(), stage.getHeight()));
            stage.heightProperty().addListener((obs, oldVal, newVal) ->
                    adjustPadding(stage.getWidth(), newVal.doubleValue()));
        });
    }

    private void adjustPadding(double width, double height) {
        double padding = (width > 1000 && height > 600) ? 550 : 300;
        loginCard.setPadding(new Insets(padding));
    }

    @FXML
    private void handleLogin(ActionEvent event) {

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Please fill all fields!", "error");
            return;
        }

        String role = AuthUtil.loginUser(email, password);

        if (role == null) {
            showMessage("Invalid email or password.", "error");
            return;
        }

        if (role.equalsIgnoreCase("Admin")) {
            boolean verified = showAdminKeyPopup();
            if (!verified) {
                showMessage("Admin key incorrect. Access denied!", "error");
                return;
            }
        }

        finishLogin(email, role);
    }

    private void finishLogin(String email, String role) {

    showMessage("Login successful as " + role + "!", "success");

    Preferences prefs = Preferences.userNodeForPackage(getClass());
    prefs.putBoolean("isLoggedIn", true);
    prefs.put("email", email);
    prefs.put("role", role);

    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
    pause.setOnFinished(e -> loadDashboard(role));
    pause.play();
    }


    private void loadDashboard(String role) {
    try {
        FXMLLoader loader;

        if (role.equalsIgnoreCase("Admin")) {
            // Load admin dashboard
            loader = new FXMLLoader(getClass().getResource("/fxml/admin.fxml"));
        } else {
            // Load normal user dashboard
            loader = new FXMLLoader(getClass().getResource("/fxml/mainwindow.fxml"));
        }

        Parent root = loader.load();
        Stage stage = (Stage) loginButton.getScene().getWindow();

        stage.setScene(new Scene(root, 1000, 600));
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
        showMessage("Error loading dashboard: " + e.getMessage(), "error");
    }
}


    @FXML
    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();

            stage.setScene(new Scene(root, 1000, 600));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error opening register page: " + e.getMessage(), "error");
        }
    }

    // 🔥 SEPARATE POPUP WINDOW
    private boolean showAdminKeyPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_key_popup.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Admin Verification");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);

            AdminKeyPopupController controller = loader.getController();
            popupStage.showAndWait();

            return controller.isKeyValidated();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        switch (type) {
            case "success": messageLabel.setStyle("-fx-text-fill: #00ff7f; -fx-font-weight: bold;"); break;
            case "info": messageLabel.setStyle("-fx-text-fill: #00bfff; -fx-font-weight: bold;"); break;
            default: messageLabel.setStyle("-fx-text-fill: #ff4c4c; -fx-font-weight: bold;");
        }
    }
}
