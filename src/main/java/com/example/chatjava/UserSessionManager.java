package com.example.chatjava;

import com.example.chatjava.event.UserJoinEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@EnableScheduling
@Component
public class UserSessionManager {
    private final Map<String, Set<UserSession>> userSessions = new HashMap<>();

    private final Publisher publisher;

    public UserSessionManager(Publisher publisher) {
        this.publisher = publisher;
    }

    @EventListener
    public void onUserJoin(UserJoinEvent event) {
        UserSession userSession = new UserSession(
                new Date(),
                event.roomName(),
                event.sessionId());
        if (userSessions.containsKey(event.userName())) {
            userSessions.get(event.userName()).add(userSession);
            System.out.println("User " + event.userName() + " added room " + event.roomName());
        } else {
            userSessions.put(
                    event.userName(),
                    new HashSet<>(Set.of(userSession)));
            System.out.println("User " + event.userName() + " made session, added room " + event.roomName());
        }
    }

    @Scheduled(fixedRate = 3_000)
    public void checkUserLastSeen() {
        Date now = new Date();
        userSessions.entrySet().removeIf(entry -> {
            String roomName = entry.getKey();
            entry.getValue().removeIf(userSession -> {
                boolean isExpired = now.getTime() - userSession.lastSeen().getTime() > 2_000;
                if (isExpired) {
                    System.out.printf("User %s left room %s%n", roomName, userSession.roomName());
                    this.publisher.userLeave(userSession.roomName(), userSession.sessionId());
                }
                return isExpired;
            });
            return entry.getValue().isEmpty();
        });
    }

    public void userPing(String userName, String roomName) {
        if (userSessions.containsKey(userName)) {
            userSessions.get(userName).stream()
                    .filter(userSession -> userSession.roomName().equals(roomName))
                    .findFirst()
                    .ifPresent(userSession -> userSession.lastSeen().setTime(new Date().getTime()));
            System.out.println("User " + userName + " pinged room " + roomName);
        }
    }
}
