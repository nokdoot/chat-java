package com.example.chatjava;

import com.example.chatjava.dto.UserJoinResponse;
import com.example.chatjava.dto.UserJoinRequest;

import com.example.chatjava.dto.UserPingRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
public class Controller {
    private final RoomManager roomManager;
    private final UserSessionManager userSessionManager;

    public Controller(RoomManager roomManager, UserSessionManager userSessionManager) {
        this.roomManager = roomManager;
        this.userSessionManager = userSessionManager;
    }

    @PostMapping(value = "/join", produces = "application/json")
    public ResponseEntity<UserJoinResponse> joinOneOnOne(@RequestBody
    UserJoinRequest userJoinRequest) {
        Room room = this.roomManager.createRoom(Stream.concat(
                Stream.of(userJoinRequest.getMyName()),
                userJoinRequest.getPartnerNames().stream()
        ).toList());
        return ResponseEntity.ok(new UserJoinResponse("http://localhost:" + room.port(), room.name()));
    }

    @PostMapping(value = "/ping", produces = "application/json")
    public void ping(@RequestBody
            UserPingRequest userPingRequest) {
        this.userSessionManager.userPing(userPingRequest.getMyName(), userPingRequest.getRoomName());
    }


}
