package com.example.chatjava.room;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class LogRepository {
    public void save(String roomName, String message, Date date) {
        System.out.println("[" + roomName + "] " + message + ": " + date.getTime());
    }
}
