package com.genuinecoder.aiassistant.gui;

import com.genuinecoder.aiassistant.util.ContextUtil;
import com.genuinecoder.aiassistant.util.AuthUtil;
import com.genuinecoder.aiassistant.util.ChatHistoryUtil;
import java.util.List;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.MimeTypeUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.util.Optional;
import java.io.File;
import java.io.FileWriter;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.stage.FileChooser;
import javafx.geometry.Pos;

public class MainApplicationController implements Initializable {

    private final Logger log = Logger.getLogger(MainApplicationController.class.getName());

    @FXML public VBox chatContainer;
    @FXML public ScrollPane chatScroll;
    @FXML public TextField textAreaInput;
    @FXML public Button imageButton;
    @FXML public Button sendButton;
    public BorderPane rootPane;
    @FXML private Button newChatBtn;
    @FXML private Button feedbackBtn;
    @FXML private ListView<String> chatHistoryList;
    @FXML private HBox thinkingContainer;
    @FXML private ProgressIndicator thinkingIndicator;
    @FXML private VBox sidebar;
    @FXML private Button menuButton;
    @FXML private Button logoutBtn;

    private Image clipboardImage;
    private boolean isNewChat = false; // Flag to generate title for the first user message
    private int messagesInCurrentChat = 0; // Counter to track number of user messages before generating title
    final StringBuilder conversationHistory = new StringBuilder();
    private final java.util.Map<String, StringBuilder> chatHistoryMap = new java.util.LinkedHashMap<>();
    private String currentChatTitle = null;
    private int currentUserId;
    private String currentUserEmail;

    // To create unique placeholders for new chats (New Chat, New Chat 2, New Chat 3...)
    private int newChatCounter = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.log(Level.INFO, "NeuroBot Loaded!");

        // --- Context Menu for Chat History Items ---
        ContextMenu contextMenu = new ContextMenu();

        MenuItem renameItem = new MenuItem("Rename Chat");
        MenuItem deleteItem = new MenuItem("Delete Chat");
        MenuItem exportItem = new MenuItem("Export Chat (.txt)");

        contextMenu.getItems().addAll(renameItem, deleteItem, exportItem);

        chatHistoryList.setCellFactory(param -> new ListCell<String>() {
            private final Label titleLabel = new Label();
            private final HBox container = new HBox();
            private final ContextMenu menu = new ContextMenu();

            {
                ImageView dotsIcon = new ImageView(
                        new Image(getClass().getResource("/icon/more_dots_black.png").toExternalForm())
                );
                dotsIcon.setFitWidth(12);
                dotsIcon.setFitHeight(12);

                Button optionsButton = new Button();
                optionsButton.setGraphic(dotsIcon);
                optionsButton.setPrefSize(18, 18);
                optionsButton.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-cursor: hand;"
                );

                // Optional: hover effect (invert colors + image)
                optionsButton.setOnMouseEntered(e -> {
                    optionsButton.setStyle(
                            "-fx-background-color: transparent;" +
                                    "-fx-cursor: hand;"
                    );
                    dotsIcon.setImage(new Image(getClass().getResource("/icon/more_dots_black.png").toExternalForm()));
                });
                optionsButton.setOnMouseExited(e -> {
                    optionsButton.setStyle(
                            "-fx-background-color: transparent;" +
                                    "-fx-cursor: hand;"
                    );
                    dotsIcon.setImage(new Image(getClass().getResource("/icon/more_dots_black.png").toExternalForm()));
                });

                // --- Menu items ---
                MenuItem renameItem = new MenuItem("Rename Chat");
                MenuItem deleteItem = new MenuItem("Delete Chat");
                MenuItem exportItem = new MenuItem("Export Chat (.txt)");

                renameItem.setOnAction(e -> renameChat(getItem()));
                deleteItem.setOnAction(e -> showConfirmationPopup(
                        "Are you sure you want to delete this chat?",
                        confirmed -> { if (confirmed) deleteChat(getItem()); }
                ));
                exportItem.setOnAction(e -> exportChat(getItem()));

                menu.getItems().addAll(renameItem, deleteItem, exportItem);

                // --- Button Action ---
                optionsButton.setOnAction(e -> {
                    if (!menu.isShowing()) {
                        menu.show(optionsButton, Side.BOTTOM, 0, 0);
                    } else {
                        menu.hide();
                    }
                });

                // --- Layout & Alignment Fix ---
                container.getChildren().addAll(titleLabel, optionsButton);
                container.setSpacing(10);
                container.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(titleLabel, Priority.ALWAYS);

                // Vertically align the label with the dots
                titleLabel.setTranslateY(2); // move label slightly downward
                titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    titleLabel.setText(item);
                    setGraphic(container);
                }
            }
        });




        // --- Handle Rename Chat ---
        renameItem.setOnAction(event -> {
            String selectedChat = chatHistoryList.getSelectionModel().getSelectedItem();
            if (selectedChat != null) {
                handleRenameChat(selectedChat);
            }
        });

        // --- Handle Delete Chat (with confirmation popup) ---
        deleteItem.setOnAction(event -> {
            String selectedChat = chatHistoryList.getSelectionModel().getSelectedItem();
            if (selectedChat != null) {
                showConfirmationPopup("Are you sure you want to delete this chat?", confirmed -> {
                    if (confirmed) {
                        handleDeleteChat(selectedChat);
                    }
                });
            }
        });

        // --- Handle Export Chat ---
        exportItem.setOnAction(event -> {
            String selectedChat = chatHistoryList.getSelectionModel().getSelectedItem();
            if (selectedChat != null) {
                handleExportChat(selectedChat);
            }
        });

        // --- Handle Clicking on Chat from History ---
        chatHistoryList.setOnMouseClicked(event -> {
            String selectedChat = chatHistoryList.getSelectionModel().getSelectedItem();
            if (selectedChat != null) {
                loadChatHistory(selectedChat);
            }
        });

        // --- Load User and Chat Data ---
        Platform.runLater(() -> {
            Preferences prefs = Preferences.userNodeForPackage(getClass());
            currentUserEmail = prefs.get("email", null);
            
            // --- Check User Role and Update Feedback Button ---
            String role = AuthUtil.getRoleByEmail(currentUserEmail);

            if (role != null && role.equalsIgnoreCase("admin")) {
                feedbackBtn.setText("Admin Page");
                feedbackBtn.setOnAction(e -> openAdminPage());
            } else {
                feedbackBtn.setText("Feedback");
                feedbackBtn.setOnAction(e -> openFeedbackPage());
            }


            if (currentUserEmail != null) {
                currentUserId = AuthUtil.getUserIdByEmail(currentUserEmail);
                System.out.println("Logged in as: " + currentUserEmail + " | User ID: " + currentUserId);

                // Load previous chat history from DB
                List<String[]> savedChats = ChatHistoryUtil.getUserChats(currentUserId);
                for (String[] chat : savedChats) {
                    String chatTitle = chat[0];
                    String conversation = chat[1];
                    if (!chatHistoryList.getItems().contains(chatTitle)) {
                        chatHistoryList.getItems().add(chatTitle);
                    }
                    chatHistoryMap.put(chatTitle, new StringBuilder(conversation));
                }

                // Do NOT auto-load any chat. Just start a new fresh chat always.
                startNewChatPlaceholder();

            }
        });
    }

    private void renameChat(String oldTitle) {
        TextInputDialog dialog = new TextInputDialog(oldTitle);
        dialog.setTitle("Rename Chat");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter new chat name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newTitle -> {
            if (!newTitle.trim().isEmpty() && !chatHistoryList.getItems().contains(newTitle)) {
                int index = chatHistoryList.getItems().indexOf(oldTitle);
                chatHistoryList.getItems().set(index, newTitle);

                StringBuilder conversation = chatHistoryMap.remove(oldTitle);
                chatHistoryMap.put(newTitle, conversation);
                ChatHistoryUtil.renameChat(currentUserId, oldTitle, newTitle);

                if (oldTitle.equals(currentChatTitle)) {
                    currentChatTitle = newTitle;
                }
            }
        });
    }


    private void deleteChat(String chatTitle) {
        chatHistoryList.getItems().remove(chatTitle);
        chatHistoryMap.remove(chatTitle);
        ChatHistoryUtil.deleteChat(currentUserId, chatTitle);

        if (chatTitle.equals(currentChatTitle)) {
            startNewChatPlaceholder();
        }
    }

    private void exportChat(String chatTitle) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Chat As");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialFileName(chatTitle + ".txt");

            File file = fileChooser.showSaveDialog(chatHistoryList.getScene().getWindow());
            if (file != null) {
                StringBuilder conversation = chatHistoryMap.get(chatTitle);
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(conversation != null ? conversation.toString() : "");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // === CONFIRMATION POPUP ===
    private void showConfirmationPopup(String message, java.util.function.Consumer<Boolean> callback) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        callback.accept(result.isPresent() && result.get() == yesButton);
    }

    // === HANDLE RENAME CHAT ===
    private void handleRenameChat(String oldTitle) {
        TextInputDialog dialog = new TextInputDialog(oldTitle);
        dialog.setTitle("Rename Chat");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter new chat name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newTitle -> {
            if (!newTitle.trim().isEmpty() && !chatHistoryList.getItems().contains(newTitle)) {
                // Update in list
                int index = chatHistoryList.getItems().indexOf(oldTitle);
                chatHistoryList.getItems().set(index, newTitle);

                // Update in map
                StringBuilder conversation = chatHistoryMap.remove(oldTitle);
                chatHistoryMap.put(newTitle, conversation);

                // Update in DB
                ChatHistoryUtil.renameChat(currentUserId, oldTitle, newTitle);

                // Update current chat if it's open
                if (oldTitle.equals(currentChatTitle)) {
                    currentChatTitle = newTitle;
                }
            }
        });
    }

    // === HANDLE DELETE CHAT ===
    private void handleDeleteChat(String chatTitle) {
        chatHistoryList.getItems().remove(chatTitle);
        chatHistoryMap.remove(chatTitle);

        ChatHistoryUtil.deleteChat(currentUserId, chatTitle);

        if (chatTitle.equals(currentChatTitle)) {
            startNewChatPlaceholder();
        }
    }

    // === HANDLE EXPORT CHAT ===
    private void handleExportChat(String chatTitle) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Chat As");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialFileName(chatTitle + ".txt");

            File file = fileChooser.showSaveDialog(chatHistoryList.getScene().getWindow());
            if (file != null) {
                StringBuilder conversation = chatHistoryMap.get(chatTitle);
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(conversation != null ? conversation.toString() : "");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Ensures a new placeholder chat is created with a unique name like:
     * "New Chat" or "New Chat 2" so multiple placeholders don't collide.
     */
    private void startNewChatPlaceholder() {
        chatContainer.getChildren().clear();
        textAreaInput.clear();
        conversationHistory.setLength(0);
        isNewChat = true;
        messagesInCurrentChat = 0;

        String placeholder = "New Chat";
        if (chatHistoryList.getItems().contains(placeholder)) {
            // generate unique placeholder e.g., New Chat 2, New Chat 3...
            while (chatHistoryList.getItems().contains(placeholder + " " + newChatCounter)) {
                newChatCounter++;
            }
            placeholder = placeholder + " " + newChatCounter;
            newChatCounter++;
        }

        currentChatTitle = placeholder;
        chatHistoryMap.put(currentChatTitle, new StringBuilder());
        chatHistoryList.getItems().add(0, currentChatTitle); // most recent at top
        chatHistoryList.getSelectionModel().select(currentChatTitle);
    }

    @FXML
    public void handleNewChatAction(ActionEvent event) {
        // Before creating a new chat, save the current one (if it has content)
        if (currentChatTitle != null && conversationHistory.length() > 0 && currentUserId > 0) {
            // persist current chat
            ChatHistoryUtil.saveChat(currentUserId, currentChatTitle, conversationHistory.toString());
            // update in-memory map
            chatHistoryMap.put(currentChatTitle, new StringBuilder(conversationHistory.toString()));
        }

        // Create a fresh placeholder chat
        startNewChatPlaceholder();
    }

    /** Feedback button action */
    @FXML
    public void handleFeedbackAction(ActionEvent event) {
        openFeedbackPage();
    }

    /** Opens the feedback page in a separate window with proper styling */
    private void openFeedbackPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/feedback.fxml"));
            Parent feedbackRoot = loader.load();

            Stage feedbackStage = new Stage();
            feedbackStage.setTitle("Feedback");

            // Apply the main CSS file
            Scene feedbackScene = new Scene(feedbackRoot);
            feedbackScene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

            feedbackStage.setScene(feedbackScene);
            feedbackStage.initModality(Modality.APPLICATION_MODAL); // blocks main window while open
            feedbackStage.show();

        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to open Feedback page", e);
        }
    }

    private void openAdminPage() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin.fxml"));
        Parent adminRoot = loader.load();

        // Get the current window from any button (feedbackBtn is available)
        Stage currentStage = (Stage) feedbackBtn.getScene().getWindow();

        // Replace the scene content
        Scene newScene = new Scene(adminRoot);
        newScene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

        currentStage.setScene(newScene);
        currentStage.show();

    } catch (IOException e) {
        log.log(Level.SEVERE, "Failed to open Admin Page", e);
    }
}



    /** Load chat from history (without duplicating user messages) */
    private void loadChatHistory(String chatTitle) {
        chatContainer.getChildren().clear();
        conversationHistory.setLength(0);

        StringBuilder savedConversation = chatHistoryMap.get(chatTitle);
        if (savedConversation == null) {
            // If map doesn't have it (rare), try loading from DB
            List<String[]> savedChats = ChatHistoryUtil.getUserChats(currentUserId);
            for (String[] chat : savedChats) {
                if (chat[0].equals(chatTitle)) {
                    savedConversation = new StringBuilder(chat[1]);
                    chatHistoryMap.put(chatTitle, savedConversation);
                    break;
                }
            }
            if (savedConversation == null) return;
        }

        // Populate UI from conversation text
        String[] lines = savedConversation.toString().split("\n");
        for (String line : lines) {
            if (line.startsWith("User: ")) {
                addMessageBubble(line.substring(6), true);
            } else if (line.startsWith("AI: ")) {
                addMessageBubble(line.substring(4), false);
            }
        }

        // restore conversationHistory (after populating UI)
        conversationHistory.append(savedConversation);

        // update counters / state
        currentChatTitle = chatTitle;
        isNewChat = chatTitle.startsWith("New Chat"); // if placeholder, treat as new chat
        messagesInCurrentChat = ChatHistoryUtil.countUserMessages(conversationHistory.toString());
        chatHistoryList.getSelectionModel().select(chatTitle);
    }

    @FXML
    public void handleButtonSendAction(ActionEvent event) {
        String userMessage = textAreaInput.getText();
        if (userMessage == null || userMessage.isEmpty()) return;

        addMessageBubble(userMessage, true);
        textAreaInput.clear();

        messagesInCurrentChat++; // Increment user message counter

        if (clipboardImage != null) {
            processTextWithImagePrompt(userMessage, clipboardImage);
            clipboardImage = null;
        } else {
            processTextOnlyPrompt(userMessage);
        }

        // Generate AI chat title after 2 user messages (i.e. when > 2 messages have been sent)
        if (isNewChat && messagesInCurrentChat > 2) {
            generateChatTitleFromAI();
            // don't set isNewChat=false here — we'll set it after renaming succeeds
        }
    }

    private void generateChatTitleFromAI() {
        ChatClient chatClient = ContextUtil.getApplicationContext().getBean(ChatClient.class);

        String prompt = "Generate a short, descriptive title (maximum 3 words) for this conversation:\n"
                + conversationHistory.toString() + "\nTitle:";

        chatClient.prompt()
                .user(prompt)
                .stream()
                .content()
                .reduce("", String::concat)
                .subscribe(fullTitle -> Platform.runLater(() -> {
                    String generatedTitle;
                    if (fullTitle != null && !fullTitle.isBlank()) {
                        String[] words = fullTitle.trim().split("\\s+");
                        if (words.length > 3) {
                            generatedTitle = String.join(" ", words[0], words[1], words[2]);
                        } else {
                            generatedTitle = String.join(" ", words);
                        }
                    } else {
                        generatedTitle = "Chat";
                    }

                    // If current title is a placeholder, rename it
                    String oldTitle = currentChatTitle;
                    if (oldTitle == null) oldTitle = "Chat " + (chatHistoryMap.size() + 1);

                    // update in-memory map and ListView
                    StringBuilder conv = chatHistoryMap.remove(oldTitle);
                    if (conv == null) conv = new StringBuilder(conversationHistory.toString());

                    // Ensure generatedTitle unique in list; if collision, append suffix
                    String uniqueTitle = generatedTitle;
                    int suffix = 1;
                    while (chatHistoryMap.containsKey(uniqueTitle)) {
                        uniqueTitle = generatedTitle + " (" + suffix + ")";
                        suffix++;
                    }

                    chatHistoryMap.put(uniqueTitle, conv);

                    int index = chatHistoryList.getItems().indexOf(oldTitle);
                    if (index >= 0) {
                        chatHistoryList.getItems().set(index, uniqueTitle);
                    } else {
                        // If oldTitle wasn't in the list, add new title at top
                        chatHistoryList.getItems().add(0, uniqueTitle);
                    }

                    currentChatTitle = uniqueTitle;
                    isNewChat = false; // title created; not a new-chat placeholder anymore

                    // Persist rename / save in database for logged-in user
                    if (currentUserId > 0) {
                        if (oldTitle != null) {
                            ChatHistoryUtil.renameChat(currentUserId, oldTitle, uniqueTitle);
                        }
                        // If rename failed (e.g., oldTitle didn't exist or conflict), ensure saved under new title
                        ChatHistoryUtil.saveChat(currentUserId, uniqueTitle, conv.toString());
                    }
                }));
    }

    /** Image picker button */
    @FXML
    public void handleButtonImagePickerAction(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasImage()) {
            clipboardImage = clipboard.getImage();
            addMessageBubble("[📷 Image added from clipboard]", true);
        } else {
            log.log(Level.INFO, "Clipboard has no image!");
        }
    }

    /** Process text-only prompts with memory */
    private void processTextOnlyPrompt(String userMessage) {
        log.log(Level.INFO, "Processing text-only input...");
        showThinkingIndicator(true);

        ChatClient chatClient = ContextUtil.getApplicationContext().getBean(ChatClient.class);
        // Prevent duplicate user lines if already appended
        if (!conversationHistory.toString().endsWith("User: " + userMessage + "\n")) {
            conversationHistory.append("User: ").append(userMessage).append("\n");
        }

        String promptWithHistory = conversationHistory.toString() + "AI:";

        createBotBubble(botLabel -> {
            StringBuilder botResponse = new StringBuilder();

            chatClient.prompt()
                    .user(promptWithHistory)
                    .stream()
                    .content()
                    .doOnError(err -> {
                        log.log(Level.SEVERE, "Error streaming: ", err);
                        Platform.runLater(() -> showThinkingIndicator(false));
                    })
                    .doOnComplete(() -> Platform.runLater(() -> {
                        if (botResponse.length() > 0) {
                            conversationHistory.append("AI: ").append(botResponse.toString().trim()).append("\n");

                            // Ensure we have a chat title
                            if (currentChatTitle == null) {
                                currentChatTitle = "Chat " + (chatHistoryMap.size() + 1);
                                chatHistoryList.getItems().add(0, currentChatTitle);
                            }

                            // Update in-memory and DB
                            chatHistoryMap.put(currentChatTitle, new StringBuilder(conversationHistory.toString()));
                            if (currentUserId > 0 && currentChatTitle != null) {
                                ChatHistoryUtil.saveChat(currentUserId, currentChatTitle, conversationHistory.toString());
                            }
                        }
                        showThinkingIndicator(false);
                    }))
                    .subscribe(token -> Platform.runLater(() -> {
                        botResponse.append(token);
                        botLabel.setText(botLabel.getText() + token);
                        chatScroll.setVvalue(1.0);
                    }));
        });
    }

    /** Process prompts with an image and maintain context */
    private void processTextWithImagePrompt(String userMessage, Image image) {
        log.log(Level.INFO, "Processing input with image...");
        showThinkingIndicator(true);

        ChatClient chatClient = ContextUtil.getApplicationContext().getBean(ChatClient.class);
        ByteArrayOutputStream baos = convertImageToPng(image);

        // Prevent duplicate user lines if already appended
        if (!conversationHistory.toString().endsWith("User: " + userMessage + "\n")) {
            conversationHistory.append("User: ").append(userMessage).append("\n");
        }

        String promptWithHistory = conversationHistory.toString() + "AI:";


        createBotBubble(botLabel -> {
            chatClient.prompt()
                    .user(spec -> spec.text(promptWithHistory)
                            .media(MimeTypeUtils.IMAGE_PNG,
                                    new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()))))
                    .stream()
                    .content()
                    .doOnError(err -> {
                        log.log(Level.SEVERE, "Error streaming: ", err);
                        Platform.runLater(() -> showThinkingIndicator(false));
                    })
                    .doOnComplete(() -> Platform.runLater(() -> showThinkingIndicator(false)))
                    .subscribe(token -> Platform.runLater(() -> {
                        botLabel.setText(botLabel.getText() + token);
                        chatScroll.setVvalue(1.0);
                    }));

            // Append AI response to conversation history dynamically
            botLabel.textProperty().addListener((obs, oldText, newText) -> {
                if (!newText.isEmpty() && !conversationHistory.toString().contains(newText)) {
                    conversationHistory.append("AI: ").append(newText).append("\n");
                }
                if (currentUserId > 0 && currentChatTitle != null) {
                    ChatHistoryUtil.saveChat(currentUserId, currentChatTitle, conversationHistory.toString());
                }
            });
        });
    }

    /** Show or hide the thinking indicator */
    private void showThinkingIndicator(boolean show) {
        thinkingContainer.setVisible(show);
        thinkingContainer.setManaged(show);
    }

    /** Converts JavaFX Image to PNG byte array */
    private ByteArrayOutputStream convertImageToPng(Image image) {
        try {
            var bufferedImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return baos;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Adds a user or bot bubble with copy button */
    private void addMessageBubble(String message, boolean isUser) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setStyle("-fx-padding: 10; -fx-background-radius: 15;");
        label.setMaxWidth(400);
        label.getStyleClass().add(isUser ? "chat-bubble-user" : "chat-bubble-bot");

        // Copy button
        Button copyButton = new Button("📋");
        copyButton.getStyleClass().add("chat-bubble-copy-btn");
        copyButton.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(label.getText());
            clipboard.setContent(content);
        });

        HBox container = new HBox(5); // spacing between label and button
        container.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        container.getChildren().addAll(label, copyButton);

        label.maxWidthProperty().bind(chatContainer.widthProperty().multiply(0.7));

        chatContainer.getChildren().add(container);
        chatScroll.setVvalue(1.0);
    }

    /** Create a bot bubble with copy button */
    private void createBotBubble(java.util.function.Consumer<Label> consumer) {
        Label botLabel = new Label();
        botLabel.setWrapText(true);
        botLabel.setMaxWidth(400);
        botLabel.getStyleClass().add("chat-bubble-bot");
        botLabel.setStyle("-fx-padding: 10; -fx-background-radius: 15;");

        Button copyButton = new Button("📋");
        copyButton.getStyleClass().add("chat-bubble-copy-btn");
        copyButton.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(botLabel.getText());
            clipboard.setContent(content);
        });

        HBox container = new HBox(5);
        container.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().addAll(botLabel, copyButton);

        botLabel.maxWidthProperty().bind(chatContainer.widthProperty().multiply(0.7));

        chatContainer.getChildren().add(container);
        chatScroll.setVvalue(1.0);

        consumer.accept(botLabel);
    }

    /** Toggle sidebar visibility */
    @FXML
    private void toggleSidebar() {
        boolean isVisible = sidebar.isVisible();
        sidebar.setVisible(!isVisible);
        sidebar.setManaged(!isVisible); // ensures chat area expands/contracts
    }

    @FXML
    private void handleLogoutAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/logout_confirmation.fxml"));
            Parent popupRoot = loader.load();
            LogoutConfirmationController controller = loader.getController();

            Scene scene = new Scene(popupRoot);
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

            Stage popupStage = new Stage();

            // Ensure sidebar is attached to a scene before getting window
            Platform.runLater(() -> {
                Stage ownerStage = (Stage) sidebar.getScene().getWindow();
                popupStage.initOwner(ownerStage);
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.initStyle(StageStyle.UNDECORATED); // No title bar
                popupStage.setScene(scene);
                popupStage.showAndWait();

                if (controller.isConfirmed()) {
                    Preferences prefs = Preferences.userNodeForPackage(getClass());
                    prefs.putBoolean("isLoggedIn", false);
                    prefs.remove("email");
                    try {
                        // Load login page safely using Parent
                        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                        Parent loginRoot = loginLoader.load(); // ✅ Use Parent, not VBox

                        Scene loginScene = new Scene(loginRoot);
                        loginScene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

                        ownerStage.setScene(loginScene);
                        ownerStage.setTitle("Login");
                        ownerStage.show();
                    } catch (IOException e) {
                        log.log(Level.SEVERE, "Failed to load login page", e);
                    }
                }
            });

        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to open Logout Confirmation popup", e);
        }
    }
}
