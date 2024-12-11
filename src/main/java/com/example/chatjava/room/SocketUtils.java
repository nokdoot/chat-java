package com.example.chatjava.room;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketUtils {
    public static int GetAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("No available ports found.", e);
        }
    }
}