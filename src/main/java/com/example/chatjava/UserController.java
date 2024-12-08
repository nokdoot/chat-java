package com.example.chatjava;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
public class UserController {
    private final RoomManager roomManager;

    public UserController(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @PostMapping("/join")
    public String joinOneOnOne(@RequestBody UserJoinRequest userJoinRequest) {
        SocketIOServer server = this.roomManager.createRoom(Stream.concat(
                Stream.of(userJoinRequest.getMyName()),
                userJoinRequest.getPartnerNames().stream()
        ).toList());
        return "http://localhost:" + server.getConfiguration().getPort();
    }

}
