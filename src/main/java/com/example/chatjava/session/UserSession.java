package com.example.chatjava.session;

import java.util.Date;

public record UserSession(Date lastSeen, String roomName, String sessionId) {
}
