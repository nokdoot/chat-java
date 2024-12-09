package com.example.chatjava;

import java.util.Date;

public record UserSession(Date lastSeen, String roomName, String sessionId) {
}
