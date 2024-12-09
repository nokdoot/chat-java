package com.example.chatjava;

import com.corundumstudio.socketio.SocketIOServer;

public record Room(String name, Integer port, SocketIOServer server) {
}
