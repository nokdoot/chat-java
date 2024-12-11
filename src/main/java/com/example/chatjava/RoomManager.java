package com.example.chatjava;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.example.chatjava.event.UserLeaveEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class RoomManager {
    private final Publisher publisher;

    public RoomManager(Publisher publisher) {
        this.publisher = publisher;
    }

    private final Map<String, Room> rooms = new HashMap<>();

    private static int getAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("No available ports found.", e);
        }
    }

    private void initListener(SocketIOServer server) {
        server.addConnectListener(client -> {
            String userName = client.getHandshakeData().getUrlParams().get("myName").get(0);
            String roomName = client.getHandshakeData().getUrlParams().get("roomName").get(0);
            if (userName == null || roomName == null) {
                client.disconnect();
                return;
            }
            publisher.userJoin(userName, roomName, client.getSessionId().toString());
            System.out.println("Client connected: " + client.getSessionId());
        });

        server.addEventListener("sendMessage", String.class, (client, data, ackSender) -> {
            if (data.length() > 1000) {
                return;
            }
            System.out.println("Received message: " + data);
            server.getAllClients().forEach(c -> {
                c.sendEvent("receiveMessage", data);
            });
        });

        server.addDisconnectListener(c -> {
            System.out.println("Client disconnected: " + c.getSessionId());
            Room room = rooms.values().stream()
                             .filter(r -> r.server().getAllClients().contains(c))
                             .findFirst()
                             .orElse(null);
            if (server.getAllClients().isEmpty()) {
                server.stop();
                System.out.println("Server stopped.");
                if (room != null)
                    rooms.remove(room.name());
            }
        });
    }

    @EventListener
    public void onUserLeave(UserLeaveEvent event) {
        Room room = rooms.get(event.roomName());
        if (room != null) {
            SocketIOClient client = room.server().getClient(UUID.fromString(event.sessionId()));
            if (client != null) {
                client.disconnect();
                System.out.println("User disconnected: " + event.sessionId());
            }
        }
    }

    public Room createRoom(List<String> userNames) {
        if (userNames.size() > 100) {
            throw new IllegalArgumentException("Too many users.");
        }
        String roomName = String.join("-", userNames.stream().sorted().toList());
        Room room;
        if (rooms.containsKey(roomName)) {
            room = rooms.get(roomName);
            System.out.println("Server already exists: " + roomName);
        }
        else {
            Configuration config = new Configuration();
            config.setPort(getAvailablePort());
            SocketIOServer server = new SocketIOServer(config);
            room = new Room(roomName, server.getConfiguration().getPort(), server);
            rooms.put(roomName, room);

            initListener(server);
            server.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.stop();
                System.out.println("Server stopped.");
            }));
        }
        System.out.println("Room created: " + roomName);

        return room;
    }


}
