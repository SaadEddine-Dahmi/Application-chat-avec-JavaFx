// Fichier: Server.java
package com.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 8888;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Serveur démarré sur le port " + PORT);
            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nouveau client connecté: " + socket);
                
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clients.add(clientHandler);
                
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void broadcast(String message, ClientHandler sender) {
        System.out.println("Diffusion: " + message); // Debug
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
