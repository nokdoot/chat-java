package com.example.chatjava.room;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.example.chatjava.event.Publisher;
import lombok.Getter;

import java.util.Date;

@Getter
public class Room {
    private final Integer port;
    private final String name;
    private final SocketIOServer server;
    private final Publisher publisher;
    private final LogRepository logRepository;


    public static Room CreateRoom(String name, Publisher publisher, LogRepository logRepository) {
        Configuration config = new Configuration();
        int port = SocketUtils.GetAvailablePort();
        config.setPort(port);
        SocketIOServer server = new SocketIOServer(config);

        Room room = new Room(port, name, server, publisher, logRepository);
        room.startServer();
        System.out.println("Room created: " + name);
        return room;
    }

    public Room(
            Integer port,
            String name,
            SocketIOServer server,
            Publisher publisher,
            LogRepository logRepository) {
        this.port = port;
        this.name = name;
        this.server = server;
        this.publisher = publisher;
        this.logRepository = logRepository;
    }

    private void startServer() {
        initListeners();
        server.start();
        System.out.println("Room created: " + name);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
    }

    private void stopServer() {
        server.stop();
        System.out.println("Room " + name + " has been stopped.");
    }


    private void onClientConnect(SocketIOClient client) {
        String userName = client.getHandshakeData().getUrlParams().get("myName").get(0);
        String roomName = client.getHandshakeData().getUrlParams().get("roomName").get(0);
        if (userName == null || roomName == null) {
            client.disconnect();
            return;
        }
        publisher.userJoin(userName, roomName, client.getSessionId().toString());
        System.out.println("Client connected: " + client.getSessionId());
    }

    private void onMessageReceived(SocketIOClient client, String data) {
        if (data.length() > 1000) {
            client.sendEvent("error", "Message too long.");
            return;
        }
        logRepository.save(name, data, new Date());
        System.out.println("Received message: " + data);
        server.getAllClients().forEach(c -> c.sendEvent("receiveMessage", data));
    }

    private void onClientDisconnect(SocketIOClient client) {
        System.out.println("Client disconnected: " + client.getSessionId());
        if (server.getAllClients().isEmpty()) {
            server.stop();
            System.out.println("Server stopped.");
        }
    }

    private void initListeners() {
        server.addConnectListener(this::onClientConnect);
        server.addEventListener("sendMessage", String.class, (client, data, ackSender) -> onMessageReceived(client, data));
        server.addDisconnectListener(this::onClientDisconnect);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
            System.out.println("Server stopped.");
        }));
    }

}
