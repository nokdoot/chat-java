package com.example.chatjava.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserJoinRequest {
    private String myName;
    private List<String> partnerNames;
}