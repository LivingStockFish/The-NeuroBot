package com.genuinecoder.aiassistant.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.prefs.Preferences;

public class AdminController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private StackPane rootPane;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome Admin");
    }

    @FXML
private void handleFeedbackButton() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminfeedback.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) rootPane.getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/adminfeedback.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("User Feedbacks");
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("ERROR: Unable to open Feedback page!");
    }
}

    /**
     * CHAT BUTTON → Go to Main Chat Page (MainApplication)
     */
    @FXML
    private void handleChatButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainwindow.fxml"));
            Parent mainRoot = loader.load();

            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(mainRoot);

            scene.getStylesheets().add(
                getClass().getResource("/css/main.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: Unable to open Chat page!");
        }
    }

    @FXML
    private void handleModelsButton() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/models.fxml"));
        Parent modelsRoot = loader.load();

        Stage stage = (Stage) rootPane.getScene().getWindow();
        Scene scene = new Scene(modelsRoot);

        // Apply global admin/theme styles
        scene.getStylesheets().add(
            getClass().getResource("/css/models.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Models");
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("ERROR: Unable to open Models page!");
    }
}


    /**
     * LOGOUT BUTTON → Show Confirmation → If YES → Go to Login Page
     */
    @FXML
private void handleLogoutButton() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/logout_confirmation.fxml"));
        Parent popupRoot = loader.load();
        LogoutConfirmationController controller = loader.getController();

        Scene scene = new Scene(popupRoot);
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

        Stage popupStage = new Stage();

        // Ensure rootPane is attached to a scene before getting window
        Platform.runLater(() -> {
            Stage ownerStage = (Stage) rootPane.getScene().getWindow();
            popupStage.initOwner(ownerStage);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UNDECORATED);  // No title bar
            popupStage.setScene(scene);
            popupStage.showAndWait();

            if (controller.isConfirmed()) {

                // Clear login preferences
                Preferences prefs = Preferences.userNodeForPackage(getClass());
                prefs.putBoolean("isLoggedIn", false);
                prefs.remove("email");
                prefs.remove("role");

                try {
                    // Load login page
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                    Parent loginRoot = loginLoader.load();

                    Scene loginScene = new Scene(loginRoot);
                    loginScene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

                    ownerStage.setScene(loginScene);
                    ownerStage.setTitle("Login");
                    ownerStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Failed to open Logout Confirmation popup");
    }
}

}
