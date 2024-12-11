package com.example.chatjava.room;

import com.corundumstudio.socketio.SocketIOClient;
import com.example.chatjava.event.Publisher;
import com.example.chatjava.event.UserLeaveEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class RoomManager {

    private final Map<String, Room> rooms = new HashMap<>();
    private final Publisher publisher;
    private final LogRepository logRepository;

    public RoomManager(Publisher publisher, LogRepository logRepository) {
        this.publisher = publisher;
        this.logRepository = logRepository;
    }

    @EventListener
    public void onUserLeave(UserLeaveEvent event) {
        Room room = rooms.get(event.roomName());
        if (room != null) {
            SocketIOClient client = room.getServer().getClient(UUID.fromString(event.sessionId()));
            if (client != null) {
                client.disconnect();
                System.out.println("User disconnected: " + event.sessionId());
            }
        }
    }

    public Room joinRoom(List<String> userNames) {
        if (userNames.size() > 100) {
            throw new IllegalArgumentException("Too many users.");
        }
        String roomName = String.join("-", userNames.stream().sorted().toList());
        Room room;
        if (rooms.containsKey(roomName)) {
            room = rooms.get(roomName);
            System.out.println("Server already exists: " + roomName);
        } else {
            room = Room.CreateRoom(roomName, publisher, logRepository);
            rooms.put(roomName, room);
        }
        System.out.println("Room created: " + roomName);

        return room;
    }
}
