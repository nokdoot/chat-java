package com.example.chatjava.event;

public record UserJoinEvent(String userName, String roomName, String sessionId) {
}
