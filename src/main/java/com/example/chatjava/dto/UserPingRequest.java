package com.example.chatjava.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserPingRequest {
    private String myName;
    private String roomName;
}