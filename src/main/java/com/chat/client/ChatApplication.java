// Fichier: ChatApplication.java
package com.chat.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApplication extends Application {
    private static final String HOST = "localhost";
    private static final int PORT = 8888;

    private Client client;
    private TextArea chatArea;
    private TextField messageField;
    private TextField usernameField;
    private Button connectButton;
    private Button sendButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Chat Application");

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        messageField = new TextField();
        messageField.setPromptText("Entrez votre message...");
        messageField.setDisable(true);

        sendButton = new Button("Envoyer");
        sendButton.setDisable(true);
        sendButton.setOnAction(e -> sendMessage());

        usernameField = new TextField();
        usernameField.setPromptText("Entrez votre nom d'utilisateur");

        connectButton = new Button("Connecter");
        connectButton.setOnAction(e -> connect());

        HBox connectionBox = new HBox(10, new Label("Nom d'utilisateur:"), usernameField, connectButton);
        connectionBox.setPadding(new Insets(10));

        HBox messageBox = new HBox(10, messageField, sendButton);
        messageBox.setPadding(new Insets(10));

        VBox vBox = new VBox(10, connectionBox, chatArea, messageBox);
        vBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(vBox);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            if (client != null) {
                client.disconnect();
            }
            Platform.exit();
        });
    }

    private void connect() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            chatArea.appendText("Veuillez entrer un nom d'utilisateur\n");
            return;
        }

        try {
            client = new Client(HOST, PORT, username);
            client.connect();
            client.setMessageListener(message -> Platform.runLater(() -> {
                System.out.println("Mise à jour de l'interface avec: " + message); // Debug
                chatArea.appendText(message + "\n");
            }));

            usernameField.setDisable(true);
            connectButton.setDisable(true);
            messageField.setDisable(false);
            sendButton.setDisable(false);
            chatArea.appendText("Connecté au serveur en tant que " + username + "\n");
        } catch (IOException e) {
            chatArea.appendText("Impossible de se connecter au serveur: " + e.getMessage() + "\n");
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && client != null) {
            client.sendMessage(message);
            messageField.clear();
        }
    }
}
