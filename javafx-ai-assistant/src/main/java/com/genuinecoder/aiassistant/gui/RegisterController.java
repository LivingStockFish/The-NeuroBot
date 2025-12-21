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

import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleBox;
    @FXML private Button registerButton;
    @FXML private VBox registerCard;
    @FXML private Label messageLabel;
    @FXML private StackPane rootPane;

    // Regex
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>/?]).{8,}$");

    @FXML
    private void initialize() {
        roleBox.getItems().addAll("User", "Admin");

        Platform.runLater(() -> {
            Stage stage = (Stage) registerCard.getScene().getWindow();
            adjustPadding(stage.getWidth(), stage.getHeight());

            stage.widthProperty().addListener((obs, oldVal, newVal) ->
                    adjustPadding(newVal.doubleValue(), stage.getHeight()));
            stage.heightProperty().addListener((obs, oldVal, newVal) ->
                    adjustPadding(stage.getWidth(), newVal.doubleValue()));
        });
    }

    private void adjustPadding(double width, double height) {
        double padding = (width > 1000 && height > 600) ? 300 : 200;
        registerCard.setPadding(new Insets(padding));
    }

    @FXML
    private void handleRegister(ActionEvent event) {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleBox.getValue();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showMessage("Please fill all fields!", "error");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showMessage("Invalid email format!", "error");
            return;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showMessage("Password must contain 1 number & 1 special character!", "error");
            return;
        }

        if (AuthUtil.userExists(email)) {
            showMessage("User already exists!", "error");
            return;
        }

        if (role.equalsIgnoreCase("Admin")) {
            boolean verified = showAdminKeyPopup();
            if (!verified) {
                showMessage("Admin key incorrect!", "error");
                return;
            }
        }

        finishRegistration(name, email, password, role);
    }

    private void finishRegistration(String name, String email, String password, String role) {

        if (AuthUtil.registerUser(name, email, password, role)) {
            showMessage("Registration successful!", "success");

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> goToLogin());
            pause.play();

        } else {
            showMessage("Registration failed!", "error");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();

            stage.setScene(new Scene(root, 1000, 600));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error opening login: " + e.getMessage(), "error");
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

    private void showMessage(String msg, String type) {
        messageLabel.setText(msg);
        switch (type) {
            case "success": messageLabel.setStyle("-fx-text-fill:#00ff7f; -fx-font-weight:bold;"); break;
            case "info": messageLabel.setStyle("-fx-text-fill:#00bfff; -fx-font-weight:bold;"); break;
            default: messageLabel.setStyle("-fx-text-fill:#ff4c4c; -fx-font-weight:bold;");
        }
    }
}
