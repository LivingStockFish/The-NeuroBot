package com.genuinecoder.aiassistant.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.InputStream;


public class ModelsController {

    @FXML private ComboBox<String> combo1;
    @FXML private ComboBox<String> combo2;
    @FXML private ComboBox<String> combo3;
    @FXML private ComboBox<String> combo4;
    @FXML private ComboBox<String> combo5;
    @FXML private ComboBox<String> combo6;
    @FXML private ComboBox<String> combo7;
    @FXML private ImageView backButton;


    @FXML
    public void initialize() {
        // Load the back button image
        try {
            InputStream is = getClass().getResourceAsStream("/icon/back.png");
            if (is != null) {
                Image backImage = new Image(is);
                backButton.setImage(backImage);
            } else {
                System.err.println("Could not load back.png");
            }
        } catch (Exception e) {
            System.err.println("Error loading back.png: " + e.getMessage());
            e.printStackTrace();
        }

        addItems(combo1, "4B", "12B", "27B");
        addItems(combo2, "7B", "13B", "34B");
        addItems(combo3, "8B");
        addItems(combo4, "3B", "7B", "32B", "72B");
        addItems(combo5, "2B", "4B", "8B", "30B", "32B", "235B");
        addItems(combo6, "11B", "90B");
        addItems(combo7, "16x17B", "128x17B");
    }

    private void addItems(ComboBox<String> combo, String... values) {
        combo.getItems().addAll(values);
    }

    // -------------------------------
    // POPUP WINDOW
    // -------------------------------
    public void showDownloadPopup(String modelName, String size) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/download_popup.fxml"));
            Parent root = loader.load();

            DownloadPopupController controller = loader.getController();

            Stage popupStage = new Stage();
            controller.setStage(popupStage);
            controller.setModel(modelName, size);

            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setResizable(false);

            Scene scene = new Scene(root);
            popupStage.setScene(scene);
            popupStage.show();

            // Start background process
            controller.startDownloadProcess();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
public void handleBack() {
    try {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin.fxml"));
        Scene scene = backButton.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.setScene(new Scene(root));
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    // -------------------------------
    // DOWNLOAD HANDLERS
    // -------------------------------
    @FXML public void handleDownload1() { start("gemma3", combo1); }
    @FXML public void handleDownload2() { start("llava", combo2); }
    @FXML public void handleDownload3() { start("llava-llama3", combo3); }
    @FXML public void handleDownload4() { start("qwen2.5vl", combo4); }
    @FXML public void handleDownload5() { start("qwen3-vl", combo5); }
    @FXML public void handleDownload6() { start("llama3.2-vision", combo6); }
    @FXML public void handleDownload7() { start("llama4", combo7); }

    private void start(String modelName, ComboBox<String> combo) {
    String size = combo.getSelectionModel().getSelectedItem();
    if (size == null) return;

    String sizeLower = size.toLowerCase();

    showDownloadPopup(modelName, sizeLower);
    }
}
