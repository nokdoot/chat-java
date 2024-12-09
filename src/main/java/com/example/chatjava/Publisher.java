package com.example.chatjava;

import com.example.chatjava.event.UserJoinEvent;

import com.example.chatjava.event.UserLeaveEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class Publisher {
    private final ApplicationEventPublisher publisher;

    public Publisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void userJoin(
            String userName,
            String string,
            String serverName) {
        this.publisher.publishEvent(new UserJoinEvent(userName, string, serverName));
    }

    public void userLeave(String roomName, String sessionId) {
        this.publisher.publishEvent(new UserLeaveEvent(roomName, sessionId));
    }
}
