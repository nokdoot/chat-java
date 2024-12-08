package com.example.chatjava;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RoomManager {
    private static int getAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("No available ports found.", e);
        }
    }

    private final Map<String, SocketIOServer> rooms = new HashMap<>();

    private void initListener(SocketIOServer server) {
        server.addConnectListener(client -> {
            System.out.println("Client connected: " + client.getSessionId());
        });

        server.addEventListener("sendMessage", String.class, (client, data, ackSender) -> {
            System.out.println("Received message: " + data);
            server.getAllClients().forEach(c -> {
                c.sendEvent("receiveMessage", data);
            });
        });

        server.addDisconnectListener(c -> {
            System.out.println("Client disconnected: " + c.getSessionId());
            if (server.getAllClients().isEmpty()) {
                server.stop();
                rooms.values().remove(server);
                System.out.println("Server stopped.");
            }
        });
    }

    public SocketIOServer createRoom(List<String> userNames) {
        String serverName = String.join("-", userNames.stream().sorted().toList());
        SocketIOServer server;
        if (rooms.containsKey(serverName)) {
            server = rooms.get(serverName);
            System.out.println("Server already exists: " + serverName);
        }
        else {
            Configuration config = new Configuration();
            config.setPort(getAvailablePort());
            server = new SocketIOServer(config);
            rooms.put(serverName, server);

            initListener(server);
            server.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.stop();
                System.out.println("Server stopped.");
            }));
        }

        return server;
    }
}
