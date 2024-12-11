package com.example.chatjava.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    private final ApplicationEventPublisher publisher;

    public EventPublisher(ApplicationEventPublisher publisher) {
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
