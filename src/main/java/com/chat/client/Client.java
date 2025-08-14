// Fichier: Client.java
package com.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String host;
    private int port;
    private String username;
    private MessageListener messageListener;

    public Client(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        // Envoyer le nom d'utilisateur au serveur
        writer.println(username);

        // Démarrer un thread pour écouter les messages du serveur
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println("Reçu sur le client: " + message); // Debug
                        if (messageListener != null) {
                            messageListener.onMessageReceived(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public void disconnect() {
        try {
            if (socket != null) {
                writer.println("/quit");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface MessageListener {
        void onMessageReceived(String message);
    }
}
