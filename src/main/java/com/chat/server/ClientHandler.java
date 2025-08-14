// Fichier: ClientHandler.java
package com.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private BufferedReader reader;
    private PrintWriter writer;
    private String clientName;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Lire le nom du client
            clientName = reader.readLine();
            System.out.println(clientName + " s'est connecté.");
            server.broadcast(clientName + " a rejoint le chat!", this);

            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equals("/quit")) {
                    break;
                }
                System.out.println("Reçu de " + clientName + ": " + message); // Debug
                server.broadcast(clientName + ": " + message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.removeClient(this);
            server.broadcast(clientName + " a quitté le chat!", this);
            System.out.println(clientName + " s'est déconnecté."); // Debug
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}
