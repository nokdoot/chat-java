package com.example.chatjava.dto;

import java.util.List;

public record UserJoinRequest(String myName, List<String> partnerNames) {
}