package com.genuinecoder.aiassistant.gui;

import com.genuinecoder.aiassistant.util.ContextUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;

import java.util.Objects;
import java.util.prefs.Preferences;

public class MainApplication extends Application {

    private static final double WINDOW_WIDTH = 1000;
    private static final double WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage stage) throws Exception {
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String role = prefs.get("role", null);

        Parent parent;

        try {
            if (!isLoggedIn) {
                // Not logged in -> show login page
                parent = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("/fxml/login.fxml")));
            } else {
                // Logged in -> choose destination based on stored role
                if (role != null && role.equalsIgnoreCase("admin")) {
                    parent = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("/fxml/admin.fxml")));
                } else if (role != null) {
                    // Any non-admin role goes to the main chat window
                    parent = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("/fxml/mainwindow.fxml")));
                } else {
                    // Role missing -> safer to show login page so app can re-authenticate
                    parent = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("/fxml/login.fxml")));
                }
            }
        } catch (Exception ex) {
            // If anything fails while loading the preferred page, fallback to login page
            ex.printStackTrace();
            parent = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("/fxml/login.fxml")));
        }

        Scene scene = new Scene(parent);
        stage.setScene(scene);

        stage.setTitle("NeuroBot | Java based Spring AI ChatBot");

        // App icon (safe-guard: wrap in try-catch so missing icon doesn't break startup)
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("/icon/icon.png"))));
        } catch (Exception ignored) { }

        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.setMinWidth(WINDOW_WIDTH);
        stage.setMinHeight(WINDOW_HEIGHT);
        stage.setMaximized(false);
        stage.centerOnScreen();

        // Graceful Spring shutdown
        stage.setOnCloseRequest(event -> SpringApplication.exit(ContextUtil.getApplicationContext()));

        stage.show();
    }

    public static void launchApplication() {
        MainApplication.launch(MainApplication.class, "");
    }
}
