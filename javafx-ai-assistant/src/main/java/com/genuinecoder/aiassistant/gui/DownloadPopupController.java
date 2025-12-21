package com.genuinecoder.aiassistant.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;

public class DownloadPopupController {

    @FXML private Label titleLabel;
    @FXML private ProgressIndicator loadingCircle;
    @FXML private Label successLabel;
    @FXML private Label errorLabel;

    private Stage popupStage;

    private String modelName;
    private String size;

    public void setStage(Stage stage) {
        this.popupStage = stage;
    }

    public void setModel(String modelName, String size) {
        this.modelName = modelName;
        this.size = size;
        titleLabel.setText("Downloading " + modelName + ":" + size);
    }

    @FXML
    public void initialize() {
        successLabel.setVisible(false);
        errorLabel.setVisible(false);
        loadingCircle.setVisible(true);
    }

    /** Called AFTER popup is shown */
    public void startDownloadProcess() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            try {

                String modelLower = modelName.toLowerCase();
                String sizeLower = size.toLowerCase();

                // -------------------------------
                // 1. PULL model
                // -------------------------------
                String pullCmd = "ollama pull " + modelLower + ":" + sizeLower;

                if (!runAndDetectError(pullCmd)) {
                    showError();
                    return;
                }

                // -------------------------------
                // 2. RUN test
                // -------------------------------
                String runCmd = "echo test | ollama run " + modelLower + ":" + sizeLower;

                boolean runSuccess = runAndDetectError(runCmd);

                if (!runSuccess) {
                    String rmCmd = "ollama rm " + modelLower + ":" + sizeLower;
                    runSilent(rmCmd);
                    showError();
                    return;
                }

                // -------------------------------
                // 3. SUCCESS → update config
                // -------------------------------
                updateApplicationProperties(modelLower, sizeLower);
                showSuccess();

            } catch (Exception e) {
                e.printStackTrace();
                showError();
            }
        });
    }

    // ------------------------------------------------------------
    private boolean runAndDetectError(String command) {
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.redirectErrorStream(true);

            Process process = builder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            boolean errorFound = false;

            while ((line = reader.readLine()) != null) {
                System.out.println("CMD >> " + line);

                if (line.trim().toLowerCase().startsWith("error")) {
                    errorFound = true;
                }
            }

            process.waitFor();
            return !errorFound;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ------------------------------------------------------------
    private void runSilent(String command) {
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            p.waitFor();
        } catch (Exception ignored) {}
    }

    // ------------------------------------------------------------
    // FIXED: Update application.properties (runtime + source)
    // ------------------------------------------------------------
    private void updateApplicationProperties(String modelLower, String sizeLower) {
    try {
        // Absolute path to your project folder
        Path srcPath = Paths.get("javafx-ai-assistant/src/main/resources/application.properties");

        if (!Files.exists(srcPath)) {
            System.err.println("ERROR: application.properties not found!");
            return;
        }

        // Read old file
        String content = Files.readString(srcPath);

        // Replace ONLY the model line
        content = content.replaceAll(
                "spring\\.ai\\.ollama\\.chat\\.model=.*",
                "spring.ai.ollama.chat.model=" + modelLower + ":" + sizeLower
        );

        // Write back
        Files.writeString(srcPath, content);

        System.out.println("Updated application.properties successfully.");

    } catch (Exception e) {
        e.printStackTrace();
    }
}



    // ------------------------------------------------------------
    private void showSuccess() {
        Platform.runLater(() -> {
            loadingCircle.setVisible(false);
            successLabel.setVisible(true);

            ScheduledExecutorService closer = Executors.newSingleThreadScheduledExecutor();
            closer.schedule(() -> Platform.runLater(popupStage::close), 4, TimeUnit.SECONDS);
        });
    }

    // ------------------------------------------------------------
    private void showError() {
        Platform.runLater(() -> {
            loadingCircle.setVisible(false);
            errorLabel.setVisible(true);

            ScheduledExecutorService closer = Executors.newSingleThreadScheduledExecutor();
            closer.schedule(() -> Platform.runLater(popupStage::close), 4, TimeUnit.SECONDS);
        });
    }
}
